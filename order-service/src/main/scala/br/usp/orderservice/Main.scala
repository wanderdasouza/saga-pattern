package br.usp.orderservice

import cats.effect.IO
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Await
import io.finch._
import io.finch.catsEffect._
import io.finch.circe._
import io.circe.generic.auto._

object Main extends App {

  case class Order(id: String, consumerId: String, status: String)

  case class OrderRequest(consumerId: String)

  val repository = new OrderRepository()

  def healthcheck: Endpoint[IO, String] = get(pathEmpty) {
    Ok("OK")
  }


  def postOrder: Endpoint[IO, String] = post("orders" :: jsonBody[OrderRequest]) { req: OrderRequest =>
    val resultId = repository.createOrder(req.consumerId)
    Ok(resultId)
  }

  def service: Service[Request, Response] = Bootstrap
    .serve[Text.Plain](healthcheck)
    .serve[Application.Json](postOrder)
    .toService

  Await.ready(Http.server.serve(":8081", service))
}