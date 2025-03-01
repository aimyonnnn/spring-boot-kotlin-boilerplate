## Spring Boot Boilerplate (Kotlin)

### Environment & Skills

- `Application`
  - Kotlin 2.0
  - Jdk 21
  - Spring boot 3.4.0
  - Gradle 8.10
  - Spring Security
  - Spring Batch
  - Springdoc OpenAPI
  - Postgresql
  - h2 database (PostgreSQL mode) - localhost environment
  - Jpa
  - QueryDSL
  - Redis
  - Jwt
  - Validation
  - Sentry
  - Kotlin Logging
  - Flyway

- `Test`
  - Spring Boot Starter Test
  - Spring Security
  - Spring Batch
  - Junit 5
  - Mockito Kotlin
  - Kotest
  - Mockk
  - Instancio
  - h2 database (PostgreSQL mode)
  - Flyway

- `Etc`
  - Pgadmin
  - Ktlint
  - Detekt

### Project Guide

- common
- domain (post, user, auth)
- security
  - spring security + jwt logic
- utils
- resources
  - prod, dev, local, common, test, secret-{environment}
    - common: Write common variables for the project.
    - test: Create the variables needed for your test environment.
    - secret-{environment}: auth (jwt, api key), database information

### Description

- lint
  - ktlint
    - [using the official lint rules by default.](gradle.properties)
      - [Please refer to the lint rules for this project here.](.editorconfig)
    - report output
      - build/reports/ktlint
  - detekt
    - [using rules](detekt.yml)
    - report output
      - build/reports/detekt
- docker-compose
  - If you plan to use it, you need to check the environment variables.
- create spring batch metadata table (localhost, development and production environments.)
  - Run your ddl script or Please refer
    to [github - spring batch](https://github.com/spring-projects/spring-batch/blob/5.0.x/spring-batch-core/src/main/resources/org/springframework/batch/core/schema-postgresql.sql)
    - Since this project uses postgresql, the spring.batch.jdbc.initialize-schema: always option does not work.
    - localhost & test environment,
      generating [batch-postgresql-metadata-schema.sql](src/main/resources/db/sql/batch-postgresql-metadata-schema.sql).
      - [application-test.yml](src/main/resources/application-test.yml)
- two types of tests
  - [mockito](src/test/kotlin/com/example/demo/mockito)
  - [kotest & mockk](src/test/kotlin/com/example/demo/kotest)
    - **if you want to bypass Spring Security authentication issues.**
      - [SecurityListenerFactory](src/test/kotlin/com/example/demo/kotest/common/security/SecurityListenerFactory.kt)
      - [BaseIntegrationController](src/test/kotlin/com/example/demo/kotest/common/BaseIntegrationController.kt)
        ```kotlin
        // example

        listeners(SecurityListenerFactory())

        Then("Call DELETE /api/v1/users/{userId}").config(tags = setOf(SecurityListenerFactory.NonSecurityOption)) {
          // ...
        }
        ```

### Author

Hyunwoo Park
