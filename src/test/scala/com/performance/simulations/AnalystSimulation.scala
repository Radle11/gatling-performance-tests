package com.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import com.performance.config.{Config, Authentication}

class AnalystSimulation extends BaseSimulation {
  
  // First, get the token
  val tokenScenario = scenario("Get OAuth2 Token")
    .exec(
      http("OAuth2 Token Request")
        .post(Config.authUrl)
        .headers(Authentication.tokenRequestHeaders)
        .formParamMap(Authentication.tokenRequestParams)
        .check(
          jsonPath("$.access_token").saveAs("access_token"),
          jsonPath("$.expires_in").saveAs("expires_in")
        )
    )
  
  val analystAssessmentScenario = scenario("Analyst Assessment Scenario")
    .exec(
      createRequest("Get Analyst Assessment", "GET", "/analyst/assessment")
        .check(status.is(200))
        .check(jsonPath("$[*]").exists)
    )
    .pause(minPause.seconds, maxPause.seconds)
    .exec(
      createRequest("Create Analyst Assessment", "POST", "/analyst/assessment")
        .body(StringBody(
          """{
            |  "assessmentType": "quarterly",
            |  "date": "${date}",
            |  "score": ${score},
            |  "comments": "Performance test assessment"
            |}""".stripMargin))
        .check(status.is(201))
    )
    .pause(minPause.seconds, maxPause.seconds)
    .exec(
      createRequest("Update Analyst Assessment", "PUT", "/analyst/assessment/${assessmentId}")
        .body(StringBody(
          """{
            |  "score": ${newScore},
            |  "comments": "Updated assessment comment"
            |}""".stripMargin))
        .check(status.is(200))
    )
    .pause(minPause.seconds, maxPause.seconds)
    .exec(
      createRequest("Delete Analyst Assessment", "DELETE", "/analyst/assessment/${assessmentId}")
        .check(status.is(204))
    )

  // Set up the simulation with the token scenario first, then the analyst assessment scenario
  setUp(
    tokenScenario.inject(atOnceUsers(1))
      .andThen(
        analystAssessmentScenario.inject(
          rampUsers(Config.usersCount).during(Config.rampUpTime)
        )
      )
  ).protocols(httpProtocol)
    .maxDuration(Config.testDuration)
} 