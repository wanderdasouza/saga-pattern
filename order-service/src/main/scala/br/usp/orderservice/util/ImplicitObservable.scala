package br.usp.orderservice.util

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import org.mongodb.scala._

sealed trait ImplicitObservable[C] {
  val observable: Observable[C]
  val converter: (C) => String
  def results(): Seq[C] = Await.result(observable.toFuture(), Duration(10, TimeUnit.SECONDS))
  def headResult() = Await.result(observable.head(), Duration(10, TimeUnit.SECONDS))
  def printResults(initial: String = ""): Unit = {
    if (initial.length > 0) print(initial)
    results().foreach(res => println(converter(res)))
  }
  def printHeadResult(initial: String = ""): Unit = println(s"${initial}${converter(headResult())}")
}

object ImplicitObservable {
  implicit class DocumentObservable[C](val observable: Observable[Document]) extends ImplicitObservable[Document] {
    override val converter: (Document) => String = (doc) => doc.toJson
  }
  implicit class GenericObservable[C](val observable: Observable[C]) extends ImplicitObservable[C] {
    override val converter: (C) => String = (doc) => doc.toString
  }
}