package br.usp.accountingservice

object AccountingEvent extends Enumeration {
    // Assigning values
    val ConsumerCreated = Value("ConsumerCreated")
    val ConsumerNameUpdated = Value("ConsumerNameUpdated")
    val ConsumerDeleted = Value("ConsumerDeleted")
}
