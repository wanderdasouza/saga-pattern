package br.usp.orderservice

import org.mongodb.scala.MongoClient
import util.ImplicitObservable._
import org.mongodb.scala.bson.collection.immutable.Document
import OrderPublisher._
import br.usp.orderservice.Main.Order
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters

class OrderRepository {
  val mongoClient = MongoClient()

  val orders = mongoClient.getDatabase("order").getCollection("order-sync")


  def getOrderById(id: String): Option[Order] = {
    val orderId = new ObjectId(id)
    val orderEvents = orders.find(Filters.eq("entity_id", consumerId)).results()
    var order = Option.empty[Order]
    orderEvents.foreach(event => {
      val eventData = event.get("event_data")
      event.get("event_type") match {
        case "OrderCreated" => order = Some(Order(orderId, eventData.get("consumer_id"), "PENDING"))
        case "OrderApproved" => order = Some(order.get.copy(status = "APPROVED"))
        case "ConsumerDeleted" => order = Option.empty[Order]
      }
    })
    order
  }

  def approveOrder(orderId: String) = {
    val eventId = new ObjectId()
    val eventDoc = Document(
      "_id" -> eventId,
      "event_type" -> "OrderApproved",
      "entity_id" -> orderId,
      "event_data" -> Document(
        "status" -> "APPROVED"
      )
    )
    orders.insertOne(eventDoc).printHeadResult()
  }

  def createOrder(consumerId: String): String = {
    val eventId = new ObjectId()
    val orderId = new ObjectId()
    val orderDoc = Document(
      "_id" -> eventId,
      "event_type" -> "OrderCreated",
      "entity_id" -> orderId,
      "event_data" -> Document(
        "consumer_id" -> consumerId,
        "status" -> "PENDING"
      )
    )
    orders.insertOne(orderDoc).printHeadResult()
    writeToKafka("order-created", orderDoc.toJson())
    orderId.toString
  }
}
