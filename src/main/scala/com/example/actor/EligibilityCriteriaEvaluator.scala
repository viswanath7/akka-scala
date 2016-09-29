package com.example.actor

import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive
import com.example.actor.EligibilityCriteriaEvaluator.{BlackListed, CheckEligibility, WhiteListed}
import com.example.model.User
import org.slf4j.LoggerFactory


object EligibilityCriteriaEvaluator {

  sealed trait RequestMessage
  case class CheckEligibility(user:User) extends RequestMessage

  sealed trait ResponseMessage
  case class BlackListed(user: User) extends ResponseMessage
  case class WhiteListed(user: User) extends ResponseMessage

  // Props is a configuration class to specify options to create EligibilityCriteriaEvaluator actor
  def props = Props[EligibilityCriteriaEvaluator]
}

class EligibilityCriteriaEvaluator extends Actor {

  val logger = LoggerFactory getLogger EligibilityCriteriaEvaluator.getClass

  val blackListed = List( User("Homer Simpson", "Homer.Simpson@fox.tv"), User("Bart Simpson", "Bart.Simpson@fox.tv") )

  override def receive = {

    case CheckEligibility(user) if blackListed contains user =>
      logger debug ("User {} is blacklisted!", user)
      sender() ! BlackListed(user)
    case CheckEligibility(user) =>
      logger debug ("User {} is eligible!", user)
      sender() ! WhiteListed(user)
    case _ => logger warn "Unknown message"
  }
}
