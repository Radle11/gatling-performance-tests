package com.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import com.performance.config.Config
import scala.concurrent.duration._

class TokenFeederSimulation extends Simulation {
  val httpProtocol = http
    .baseUrl(Config.baseUrl)
    .acceptHeader("application/json")
    .userAgentHeader("Gatling Performance Test")

  // First, get the token
  val tokenScenario = scenario("Get OAuth2 Token")
    .exec(
      http("OAuth2 Token Request")
        .post(Config.authUrl)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .formParam("grant_type", "client_credentials")
        .formParam("client_id", Config.clientId)
        .formParam("client_secret", Config.clientSecret)
        .formParam("scope", Config.scope)
        .check(
          jsonPath("$.access_token").saveAs("access_token"),
          jsonPath("$.expires_in").saveAs("expires_in")
        )
    )
    .exec { session =>
      println(s"Token acquired: ${session("access_token").as[String]}")
      session
    }

  // Then use the token in API requests
  val apiScenario = scenario("API Requests with Token")
    .exec(
      http("Get Data")
        .get("/api/data")
        .header("Authorization", "Bearer ${access_token}")
        .check(status.is(200))
    )
    .pause(1.seconds, 3.seconds)
    .exec(
      http("Post Data")
        .post("/api/data")
        .header("Authorization", "Bearer ${access_token}")
        .body(StringBody("""{"key": "value"}"""))
        .check(status.is(201))
    )

  // Run the token scenario first, then the API scenario
  setUp(
    tokenScenario.inject(atOnceUsers(1))
      .andThen(
        apiScenario.inject(rampUsers(10).during(10.seconds))
      )
  ).protocols(httpProtocol)
} 