package br.usp.consumerservice

import org.mongodb.scala.{MongoClient, MongoCollection}
import util.ImplicitObservable._
import org.mongodb.scala.bson.collection.immutable.Document
import br.usp.consumerservice.Main.Consumer
import org.bson.types.ObjectId
import org.mongodb.scala.model.Filters

class ConsumerRepository {
  val mongoClient: MongoClient = MongoClient("mongodb://localhost:3001")

  val consumersCollection: MongoCollection[Document] = mongoClient.getDatabase("consumer").getCollection("consumer-sync")


  def getConsumerById(id: String): Option[Consumer] = {
    val consumerId = new ObjectId(id)
    val consumerEvents = consumersCollection.find(Filters.eq("entity_id", consumerId)).results()
    var consumer = Option.empty[Consumer]
    consumerEvents.foreach(e =>
      e.getString("event_type") match {
        case "ConsumerCreated" => consumer = Some(Consumer(id, e.get("event_data").get.asDocument.getString("name").getValue))
        case "ConsumerNameUpdated" => consumer = Some(Consumer(consumer.get.id, e.get("event_data").get.asDocument.getString("name").getValue))
        case "ConsumerDeleted" => consumer = Option.empty[Consumer]
      }
    )
    consumer
  }

  def createConsumer(name: String): String = {
    val consumerId = new ObjectId()
    val eventId = new ObjectId()
    val eventDoc = Document(
      "_id" -> eventId,
      "event_type" -> ConsumerEventType.ConsumerCreated.toString,
      "entity_id" -> consumerId,
      "event_data" -> Document(
        "name" -> name,
      )
    )
    consumersCollection.insertOne(eventDoc).printHeadResult()
    consumerId.toString
  }
}
