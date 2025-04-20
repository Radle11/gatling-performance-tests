# Gatling Performance Testing Framework

This is a performance testing framework built using Gatling for testing REST APIs with OAuth2 authentication.

## Project Structure

```
src/test/scala/com/performance/
├── config/
│   ├── Config.scala         # Configuration settings
│   └── Authentication.scala # OAuth2 authentication helper
├── simulations/
│   ├── BaseSimulation.scala    # Base simulation class
│   └── AnalystSimulation.scala # Example simulation
├── scenarios/               # Place for scenario definitions
└── models/                 # Place for data models
```

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Scala 2.13 or higher

## Configuration

The framework uses system properties for configuration. You can set these in the command line or update the default values in `Config.scala`:

- `baseUrl`: Base URL of the API
- `authUrl`: OAuth2 token endpoint
- `clientId`: OAuth2 client ID
- `clientSecret`: OAuth2 client secret
- `scope`: OAuth2 scope
- `rampUpTime`: Ramp-up time in seconds
- `testDuration`: Test duration in seconds
- `users`: Number of concurrent users

## Running Tests

### Run all simulations
```bash
mvn gatling:test
```

### Run a specific simulation
```bash
mvn gatling:test -Dgatling.simulationClass=com.performance.simulations.AnalystSimulation
```

### Run with custom parameters
```bash
mvn gatling:test -DbaseUrl=https://api.example.com -Dusers=50 -DrampUpTime=30 -DtestDuration=300
```

## Creating New Simulations

1. Create a new Scala class in the `simulations` package
2. Extend `BaseSimulation`
3. Define your scenarios using the `createRequest` helper method
4. Use `defaultLoadProfile` or create custom load profiles

Example:
```scala
class MyNewSimulation extends BaseSimulation {
  val myScenario = scenario("My Scenario")
    .exec(
      createRequest("My Request", "GET", "/my/endpoint")
        .check(status.is(200))
    )

  defaultLoadProfile(myScenario)
}
```

## Best Practices

1. Use meaningful names for scenarios and requests
2. Add appropriate checks for response validation
3. Use pauses between requests to simulate real user behavior
4. Group related endpoints in the same simulation
5. Use feeders for data-driven testing
6. Add appropriate assertions for performance criteria

## Reports

After test execution, reports can be found in:
```
target/gatling/results/
```

The report includes:
- Response time distribution
- Response time percentiles
- Number of requests per second
- Number of responses per second
- Error rate 