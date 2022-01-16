package br.usp.accountingservice

import org.bson.types.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters
import org.mongodb.scala.{MongoClient, MongoCollection}

class AccountingRepository {

  val mongoClient: MongoClient = MongoClient("mongodb://localhost:3003")

  val accountCollection: MongoCollection[Document] = mongoClient.getDatabase("accounting").getCollection("accounting-sync")

//  def getAccountByOrder(id: String): Option[Ticket] = {
//    val orderId = new ObjectId(id)
//    val kitchenEvents = kitchenCollection.find(Filters.eq("event_data.order_id", orderId)).results()
//    var ticket = Option.empty[Ticket]
//    kitchenEvents.foreach(e => {
//      val eventData = e.get("event_data").get.asDocument
//      e.getString("event_type") match {
//        case "TicketCreated" => ticket = Some(Ticket(id, eventData.get("consumer_id").asObjectId().getValue.toString))
//        case "TicketDeleted" => ticket = Option.empty[Ticket]
//      }
//    })
//    ticket
//  }

  def consumerVerified()

  def approveTicket()


  def createAccount(order: Order): String = {
    val eventId = new ObjectId()
    val ticketId = new ObjectId()

    val ticketDoc = Document(
      "_id" -> eventId,
      "event_type" -> "TicketCreated",
      "entity_id" -> ticketId,
      "event_data" -> Document(
        "consumer_id" -> new ObjectId(order.consumerId),
        "order_id" -> new ObjectId(order.orderId),
      )
    )
    kitchenCollection.insertOne(ticketDoc).printHeadResult()
    writeToKafka("ticket-created", ticketDoc.toJson())
    ticketId.toString
  }
}
