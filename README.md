Web Starter Application
========================================

This is a sample web application using Spring Boot, implementing user session management, authentication and data caching using Redis.

## Requirements

Java 8+
Redis 3.2+
MySQL 5.6+ or other relational DB (untested)

## Configuring

Edit file 'src/main/resources/application.properties' to set DB and Redis properties:
```sh
spring.datasource.url=jdbc:mysql://localhost:3306/webapp
spring.datasource.username=webapp2
spring.datasource.password=webapp
spring.session.redis.host=localhost
spring.session.redis.port=6379
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

## Running

```bash
mvn spring-boot:run
```

After Spring Boot starts, point browser to http://localhost:8080

