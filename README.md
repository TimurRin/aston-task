# Aston Task

Design and implement a RESTful API, backing service and data model to create bank accounts
and transfer money between them. Interaction with API will be using HTTP requests

## Configuration

Written in Java 8

Database settings `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.datasource.platform=h2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```