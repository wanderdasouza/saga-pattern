package br.usp.accountingservice.domain

trait State
case object PendingAuthorization extends State
case object PendingConsumerVerification extends State
case object PendingTicketApproval extends State
case object CreditCardAuthorized extends State

case class Account(orderId: String, consumerId: String, state: State)
