package br.usp.kitchenservice

object KitchenEventType extends Enumeration {
    // Assigning values
    val ConsumerCreated = Value("ConsumerCreated")
    val ConsumerNameUpdated = Value("ConsumerNameUpdated")
    val ConsumerDeleted = Value("ConsumerDeleted")
}
