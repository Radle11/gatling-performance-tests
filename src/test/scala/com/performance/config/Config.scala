package com.performance.config

object Config {
  val baseUrl: String = System.getProperty("baseUrl", "https://api.yourcompany.com")
  val authUrl: String = System.getProperty("authUrl", "https://auth.yourcompany.com/oauth2/token")
  
  // OAuth2 Configuration
  val clientId: String = System.getProperty("clientId", "your-client-id")
  val clientSecret: String = System.getProperty("clientSecret", "your-client-secret")
  val scope: String = System.getProperty("scope", "api")

  // Performance Test Configuration
  val rampUpTime: Int = System.getProperty("rampUpTime", "60").toInt
  val testDuration: Int = System.getProperty("testDuration", "300").toInt
  val usersCount: Int = System.getProperty("users", "100").toInt

  // Headers
  val commonHeaders = Map(
    "Accept" -> "application/json",
    "Content-Type" -> "application/json"
  )
} 