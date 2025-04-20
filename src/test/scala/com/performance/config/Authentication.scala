package com.performance.config

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

object Authentication {
  private val tokenCache = scala.collection.mutable.Map[String, (String, Long)]()
  private val tokenExpiryBuffer = 300000 // 5 minutes in milliseconds

  def getOAuth2Token(httpProtocol: HttpProtocolBuilder): String = {
    val cacheKey = s"${Config.clientId}:${Config.clientSecret}"
    
    tokenCache.get(cacheKey) match {
      case Some((token, expiry)) if System.currentTimeMillis() < expiry - tokenExpiryBuffer =>
        token
      case _ =>
        val response = requestNewToken(httpProtocol)
        val token = response._1
        val expiryTime = System.currentTimeMillis() + (response._2 * 1000)
        tokenCache.put(cacheKey, (token, expiryTime))
        token
    }
  }

  private def requestNewToken(httpProtocol: HttpProtocolBuilder): (String, Long) = {
    val tokenRequest = http("OAuth2 Token Request")
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

    // Execute the request synchronously
    val response = tokenRequest.build()
    val token = response.getOrElse("access_token", "").toString
    val expiresIn = response.getOrElse("expires_in", "3600").toString.toLong

    (token, expiresIn)
  }

  def addAuthHeader(token: String): Map[String, String] = {
    Config.commonHeaders + ("Authorization" -> s"Bearer $token")
  }
} 