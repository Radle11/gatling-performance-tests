# Scheduled Performance Tests

This directory contains the Jenkins pipeline configuration for scheduled performance testing.

## Schedule
- **Frequency**: Weekly
- **Day**: Every Sunday
- **Time**: 12:00 PM (noon)

## Pipeline Configuration
The pipeline is configured to:
1. Run all Gatling performance tests
2. Archive test results
3. Clean up workspace after execution

## Parameters
The following parameters can be configured in Jenkins:
- `BASE_URL`: The base URL of the API to test
- `USERS`: Number of concurrent users (default: 100)
- `RAMP_UP_TIME`: Ramp-up time in seconds (default: 60)
- `TEST_DURATION`: Test duration in seconds (default: 300)

## Reports
After each execution, test reports will be archived in Jenkins and can be found in:
- `target/gatling/**/*`

## Manual Execution
While the pipeline is scheduled to run automatically, it can also be triggered manually through the Jenkins interface.

## Troubleshooting
If the pipeline fails:
1. Check the Jenkins console output for error messages
2. Verify the BASE_URL is accessible
3. Ensure sufficient system resources are available
4. Check the Gatling reports for performance bottlenecks 