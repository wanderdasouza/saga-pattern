package br.usp.consumerservice

object ConsumerEventType extends Enumeration {
    // Assigning values
    val ConsumerCreated = Value("ConsumerCreated")
    val ConsumerNameUpdated = Value("ConsumerNameUpdated")
    val ConsumerDeleted = Value("ConsumerDeleted")
}
