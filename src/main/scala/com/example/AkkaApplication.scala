package com.example

import akka.actor.ActorSystem
import com.example.actor.UserRegistrationSystem.RegisterUser
import com.example.actor.{EligibilityCriteriaEvaluator, Registry, UserRegistrationSystem}
import com.example.model.User
import org.slf4j.LoggerFactory

object AkkaApplication extends App {

  val logger = LoggerFactory getLogger AkkaApplication.getClass

  logger debug "Creating actor system ..."
  val system = ActorSystem("akka-scala-actor-system")

  logger debug "Creating Registry actor ..."
  val registry = system.actorOf(Registry.props, "registry")

  logger debug "Creating Eligibility Criteria Evaluator actor ..."
  val eligibilityCriteriaEvaluator = system.actorOf(EligibilityCriteriaEvaluator.props, "eligibility-evaluator")

  logger debug "Creating User Registration System ..."
  val userRegistrationSystem = system.actorOf(UserRegistrationSystem.props(eligibilityCriteriaEvaluator, registry), "user-registration-system")

  logger debug "Adding Homer Simpson to user registry ..."
  userRegistrationSystem ! RegisterUser ( User("Homer Simpson", "Homer.Simpson@fox.tv") )
  logger debug "Adding Lisa Simpson to user registry ..."
  userRegistrationSystem ! RegisterUser ( User("Lisa Simpson", "Lisa.Simpson@fox.tv") )

  Thread sleep 100
  logger info "Shutting down the actor system"
  system terminate
}
