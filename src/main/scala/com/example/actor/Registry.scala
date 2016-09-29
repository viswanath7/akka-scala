package com.example.actor

import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive
import com.example.actor.Registry.Register
import com.example.model.User
import org.slf4j.LoggerFactory

object Registry {

  sealed trait RequestMessage
  case class Register(user:User) extends RequestMessage

  // Props is a configuration class to specify options to create a RequestMessage actor
  def props = Props[Registry]
}

class Registry extends Actor {

  val logger = LoggerFactory getLogger Registry.getClass
  private [this] var userRegistry = List.empty[User]

  override def receive: Receive = {
    case Register(user) =>
      logger debug ("Received 'Register' {} message", user)
      userRegistry = user::userRegistry
      logger debug ("Added user {} to the registry", user)
    case _ => logger warn "Unknown message"
  }
}
