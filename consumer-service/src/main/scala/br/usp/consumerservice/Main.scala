package br.usp.consumerservice

import cats.effect.IO
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Await
import io.finch._
import io.finch.catsEffect._
import io.finch.circe._
import io.circe.generic.auto._

import scala.collection.JavaConverters._


object Main extends App {

  case class Consumer(id: String, name: String)
  case class ConsumerRequest(name: String)

  val repository = new ConsumerRepository()

  def healthcheck: Endpoint[IO, String] = get(pathEmpty) {
    Ok("OK")
  }

  def readUser: Endpoint[IO, Consumer] = get("consumers" :: path[String]) { id: String =>
    val maybeConsumer = repository.getConsumerById(id)
    maybeConsumer match {
      case Some(consumer) => Ok(consumer)
      case None => NotFound(new Exception("Não foi encontrado cliente com este id"))
    }
  }

  def postConsumer: Endpoint[IO, String] = post("consumers" :: jsonBody[ConsumerRequest]) { req: ConsumerRequest =>
    val resultId = repository.createConsumer(req.name)
    Ok(resultId)
  }

  val topics = Array("order-created").toList.asJava
  ConsumerConsumer.consumeFromKafka(topics)

  def service: Service[Request, Response] = Bootstrap
    .serve[Text.Plain](healthcheck)
    .serve[Application.Json](postConsumer :+: readUser)
    .toService

  Await.ready(Http.server.serve(":8082", service))
}