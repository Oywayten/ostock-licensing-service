# Optima Growth Asset Management

> A microservices web-based asset management application that provides inventory, licensing, and expense management.

## About the application

- All services deployed as Docker containers.
- Spring Cloud Config server manages a services configuration information using a file system/ classpath or
  GitHub-based repository.
- Eureka server running as a Spring-Cloud based service. This service allows multiple service instances to register
  with it. Clients that need to call a service use Eureka to look up the physical location of the target service.
- Organization service manages organization's data.
- Licensing service manage licensing data used within Stock.
- Postgresql Database use to hold the data.

## Technology stack:

+ Java 17;
+ Maven;
+ Spring boot 3.2.4;
+ Spring Cloud 2023.0.1
+ Spring Data;
+ Spring Config Server;
+ Netflix Eureka;
+ Feign;
+ Gateway;
+ Docker;
+ Lombok;
+ Postgres. 

## How To Use

Check, that required tools was installed and run this commands from your command line:

```shell
# Clone this repository
git clone https://github.com/Oywayten/ostock.git
```

```shell
# Go into the repository
cd ostock
```

```shell
# Build JAR packages
mvn clean package dockerfile:build
```

```shell
# run with Docker Swarm
docker stack deploy -c .\docker-compose.yml stack
```
