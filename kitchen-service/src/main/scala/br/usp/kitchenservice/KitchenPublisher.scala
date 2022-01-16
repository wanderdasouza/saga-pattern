package br.usp.kitchenservice

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.Properties

object KitchenPublisher {
    def writeToKafka(topic: String, eventJson: String): Unit = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9094")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)
    val record = new ProducerRecord[String, String](topic, "key", eventJson)
    producer.send(record)
    producer.close()
  }
}
