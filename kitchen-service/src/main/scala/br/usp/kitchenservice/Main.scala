package br.usp.kitchenservice

import br.usp.kitchenservice.domain.{Order, Ticket}
import cats.effect.IO
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Await
import io.finch._
import io.finch.catsEffect._
import io.finch.circe._
import io.circe.generic.auto._

import scala.collection.JavaConverters._


object Main extends App {

  val repository = new KitchenRepository()

  def healthcheck: Endpoint[IO, String] = get(pathEmpty) {
    Ok("OK")
  }

  def postTicket: Endpoint[IO, String] = post("tickets" :: jsonBody[Order]) { order: Order =>
    val resultId = repository.createTicket(order)
    Ok(resultId)
  }

  def readUser: Endpoint[IO, Ticket] = get("tickets" :: path[String]) { id: String =>
    val maybeTicket = repository.getTicketByOrderId(id)
    maybeTicket match {
      case Some(ticket) => Ok(ticket)
      case None => NotFound(new Exception("Não foi encontrado ticket para este pedido"))
    }
  }

  val topics = Array("order-created").toList.asJava
  KitchenConsumer.consumeFromKafka(topics)

  def service: Service[Request, Response] = Bootstrap
    .serve[Text.Plain](healthcheck)
    .serve[Application.Json](postTicket :+: readUser)
    .toService

  Await.ready(Http.server.serve(":8083", service))
}