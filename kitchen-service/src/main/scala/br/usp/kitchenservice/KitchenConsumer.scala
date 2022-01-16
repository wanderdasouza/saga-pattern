package br.usp.kitchenservice

import br.usp.kitchenservice.domain.Order
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.collection.immutable.Document

import java.util.{Collection, Properties}
import java.util.concurrent.Executors
import scala.jdk.CollectionConverters.iterableAsScalaIterableConverter

object KitchenConsumer {
  def consumeFromKafka(topics: Collection[String]) = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9094")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("auto.offset.reset", "latest")
    props.put("group.id", "kitchen-group")

    val repository = new KitchenRepository()

    val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
    consumer.subscribe(topics)
    Executors.newSingleThreadExecutor.execute(() => {
      while (true) {
        val records = consumer.poll(100).asScala

        for (record <- records) {
          val b = BsonDocument(record.value())
          val eventDoc = Document(b)
          val eventData = eventDoc.get("event_data").get.asDocument
          eventDoc.getString("event_type") match {
            case "OrderCreated" =>
              val orderId = eventDoc.get("entity_id").get.asObjectId().getValue
              val consumerId = eventData.getString("consumer_id").getValue
              repository.createTicket(Order(orderId.toString, consumerId))
          }
          println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset())
        }
      }
    })

  }
}
