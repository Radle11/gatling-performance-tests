package com.performance.simulations

import com.performance.config.{Config, Authentication}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class BaseSimulation extends Simulation {
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(Config.baseUrl)
    .inferHtmlResources()
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Gatling Performance Test")

  // Get OAuth2 token and add it to headers
  val token: String = Authentication.getOAuth2Token(httpProtocol)
  val headers: Map[String, String] = Authentication.addAuthHeader(token)

  // Common pause configuration
  val minPause = 1
  val maxPause = 3

  // Helper methods for scenario configuration
  def defaultLoadProfile = {
    scenario => {
      setUp(
        scenario.inject(
          rampUsers(Config.usersCount).during(Config.rampUpTime)
        )
      ).protocols(httpProtocol)
        .maxDuration(Config.testDuration)
    }
  }

  // Helper method to create a request with common configuration
  def createRequest(name: String, method: String, path: String) = {
    http(name)
      .httpRequest(method, path)
      .headers(headers)
  }
} 