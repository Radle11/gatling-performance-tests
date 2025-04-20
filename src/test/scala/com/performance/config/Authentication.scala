package com.performance.config

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.jsonpath.JsonPath
import scala.concurrent.duration._
import io.gatling.core.structure.ChainBuilder

object Authentication {
  private val tokenCache = scala.collection.mutable.Map[String, (String, Long)]()
  private val tokenExpiryBuffer = 300000 // 5 minutes in milliseconds

  def getOAuth2Token(httpProtocol: HttpProtocolBuilder): String = {
    val cacheKey = s"${Config.clientId}:${Config.clientSecret}"
    
    tokenCache.get(cacheKey) match {
      case Some((token, expiry)) if System.currentTimeMillis() < expiry - tokenExpiryBuffer =>
        token
      case _ =>
        // For Gatling 3.6.1, we should use a feeder to provide the token
        // This will be set up in the simulation class
        "${access_token}"
    }
  }

  def tokenRequest = {
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
  }

  def addAuthHeader(token: String): Map[String, String] = {
    Config.commonHeaders + ("Authorization" -> s"Bearer $token")
  }
} 