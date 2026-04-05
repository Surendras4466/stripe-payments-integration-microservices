# 🌐 Eureka Discovery Server

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-Netflix%20Eureka-blue?logo=spring)
![Maven](https://img.shields.io/badge/Build-Maven-red?logo=apachemaven)
![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=java)

A lightweight **Netflix Eureka Discovery Server** that acts as the service registry for the microservices ecosystem. Client services register themselves here on startup and are discoverable by other services — no hardcoded URLs needed.

---

## 📖 Table of Contents

- [Overview](#overview)
- [Registered Services](#registered-services)
- [Setup](#setup)
- [Dependency](#dependency)
- [Configuration](#configuration)
- [Eureka Dashboard](#eureka-dashboard)

---

## Overview

This is a **minimal Eureka Server** setup. It requires only:

1. The Eureka Server dependency in `pom.xml`
2. The `application.properties` configuration
3. The `@EnableEurekaServer` annotation on the main class

No additional business logic lives in this service — it is purely the **service registry**.

```
        ┌──────────────────────────────┐
        │     Eureka Discovery Server  │
        │       localhost:8761         │
        └────────┬─────────────────────┘
                 │
        ┌────────┴────────┐
        │                 │
 ┌──────▼──────┐   ┌──────▼────────────┐
 │Stripe Service│   │Validation Service │
 └─────────────┘   └───────────────────┘
```

---

## ✅ Registered Services

The following microservices are registered with this Eureka Server:

| Service Name         | Description                                      |
|----------------------|--------------------------------------------------|
| **Stripe Service**   | Handles payment processing via Stripe API        |
| **Validation Service** | Handles input/data validation across the system |

---

## 🚀 Setup

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/eureka-server.git
cd eureka-server
```

### 2. Build

```bash
./mvnw clean install
```

### 3. Run

```bash
./mvnw spring-boot:run
```

> ⚠️ Start this server **before** launching the Stripe Service or Validation Service.

---

## 📦 Dependency

Add the following to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

---

## ⚙️ Configuration

`src/main/resources/application.properties`:

```properties
server.port=8761
spring.application.name=eureka-discovery-server

# Prevent the server from registering itself as a client
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

---

## 🖥 Eureka Dashboard

Once running, open the dashboard at:

```
http://localhost:8761
```

You will see **STRIPE-SERVICE** and **VALIDATION-SERVICE** listed under *Instances currently registered with Eureka* once they are up and running.

---

## 🏷 Main Class

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

That's all it takes — dependency + config + annotation.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
