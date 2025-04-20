package com.performance.config

object Authentication {
  private val tokenCache = scala.collection.mutable.Map[String, (String, Long)]()
  private val tokenExpiryBuffer = 300000 // 5 minutes in milliseconds

  def getOAuth2Token(): String = {
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

  // Helper method to create token request headers
  def tokenRequestHeaders: Map[String, String] = Map(
    "Content-Type" -> "application/x-www-form-urlencoded"
  )

  // Helper method to create token request parameters
  def tokenRequestParams: Map[String, String] = Map(
    "grant_type" -> "client_credentials",
    "client_id" -> Config.clientId,
    "client_secret" -> Config.clientSecret,
    "scope" -> Config.scope
  )

  def addAuthHeader(token: String): Map[String, String] = {
    Config.commonHeaders + ("Authorization" -> s"Bearer $token")
  }
} 