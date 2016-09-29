package com.example.actor
import scala.concurrent.duration._
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.example.actor.EligibilityCriteriaEvaluator.{BlackListed, CheckEligibility, WhiteListed}
import com.example.actor.Registry.Register
import com.example.actor.UserRegistrationSystem.RegisterUser
import com.example.model.User
import org.slf4j.LoggerFactory

import scala.language.postfixOps

object UserRegistrationSystem {

  sealed trait RequestMessage
  case class RegisterUser(user:User) extends RequestMessage

  // Props is a configuration class to specify options to create actors
  def props(eligibilityCriteriaEvaluator: ActorRef, registry: ActorRef) =
    Props(new UserRegistrationSystem(eligibilityCriteriaEvaluator, registry))
}

class UserRegistrationSystem(eligibilityCriteriaEvaluator: ActorRef, registry: ActorRef) extends Actor {
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout = Timeout(5 seconds)

  val logger = LoggerFactory getLogger UserRegistrationSystem.getClass

  override def receive = {

    case RegisterUser(user) =>
      logger debug "Received 'RegisterUser' message"
      eligibilityCriteriaEvaluator ? CheckEligibility(user) map {
        case BlackListed(evaluatedUser) => logger debug ("{} is ineligible!", evaluatedUser)
        case WhiteListed(evaluatedUser) => registry ! Register(evaluatedUser)
      }
    case _ => logger warn "Unknown Message"
  }

}
