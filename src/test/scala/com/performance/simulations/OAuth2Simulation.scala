package com.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import com.performance.config.{Config, Authentication}

class OAuth2Simulation extends Simulation {
  val httpProtocol = http
    .baseUrl(Config.baseUrl)
    .acceptHeader("application/json")
    .userAgentHeader("Gatling Performance Test")

  val tokenScenario = scenario("OAuth2 Token Acquisition")
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
    .exec { session =>
      println(s"Token acquired: ${session("access_token").as[String]}")
      session
    }

  setUp(
    tokenScenario.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
} 