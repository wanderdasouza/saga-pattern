package br.usp.accountingservice

import cats.effect.IO
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch._
import io.finch.catsEffect._
import io.finch.circe._

import scala.collection.JavaConverters._


object Main extends App {

  case class AccountRequest(orderId: String)

  val repository = new AccountingRepository()

  def healthcheck: Endpoint[IO, String] = get(pathEmpty) {
    Ok("OK")
  }

  def readAccount: Endpoint[IO, AccountRequest] = get("accounting" :: path[String]) { id: String =>
    val maybeAccount = repository.getAccountByOrderId(id)
    maybeAccount match {
      case Some(account) => Ok(account)
      case None => NotFound(new Exception("Não foi encontrado pagamento para este pedido"))
    }
  }

  val topics = Array("order-created", "consumer-verified", "ticket-created").toList.asJava
  AccountingConsumer.consumeFromKafka(topics)

  def service: Service[Request, Response] = Bootstrap
    .serve[Text.Plain](healthcheck)
    .serve[Application.Json](readAccount)
    .toService

  Await.ready(Http.server.serve(":8084", service))
}