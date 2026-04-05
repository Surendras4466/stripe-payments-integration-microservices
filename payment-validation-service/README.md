
# 🛡️ Payment Validation Service

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-Netflix%20Eureka%20Client-blue?logo=spring)
![Spring Security](https://img.shields.io/badge/Security-Spring%20Security-green?logo=springsecurity)
![MySQL](https://img.shields.io/badge/Database-MySQL-orange?logo=mysql)
![Maven](https://img.shields.io/badge/Build-Maven-red?logo=apachemaven)
![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=java)

A **Payment Validation Microservice** built with Spring Boot. This service acts as the security and validation layer between the merchant and the Stripe Payment Provider Service. It validates incoming payment requests using **HMAC-SHA256** signature verification, checks for **duplicate transactions**, enforces **payment thresholds**, and forwards valid requests to the **Stripe Provider Service** via load-balanced `RestClient`. It registers itself with the **Eureka Discovery Server** for service discovery.

---

## 📖 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [How It Works](#how-it-works)
- [Setup](#setup)
- [Dependencies](#dependencies)
- [Configuration](#configuration)
- [Eureka Registration](#eureka-registration)
- [API Endpoint](#api-endpoint)
- [Request & Response](#request--response)
- [Security — HMAC SHA256](#security--hmac-sha256)
- [Duplicate Transaction Check](#duplicate-transaction-check)
- [Payment Threshold](#payment-threshold)
- [Load Balanced Call to Stripe Service](#load-balanced-call-to-stripe-service)
- [Exception Handling](#exception-handling)
- [Project Structure](#project-structure)

---

## Overview

The **Validation Service** sits between the caller and the Stripe Provider Service. No payment request reaches Stripe without passing through this service first. It is responsible for:

- ✅ Verifying the **HMAC-SHA256** signature of every incoming request
- ✅ Checking for **duplicate transactions** using a MySQL repository
- ✅ Enforcing a **payment threshold** (rejecting payments above/below configured limits)
- ✅ Forwarding valid requests to the **Stripe Provider Service** using a **load-balanced RestClient**
- ✅ Validating and handling the **Stripe response**

---

## 🏗 Architecture

```
  Merchant / Caller
        │
        │  POST /v1/payments
        ▼
┌──────────────────────────┐
│    Validation Service    │
│         :8080            │
│                          │
│  1. HMAC-SHA256 verify   │
│  2. Duplicate TX check ──┼──► MySQL DB
│  3. Threshold check      │
│  4. Forward request   ───┼──► STRIPE-SERVICE (load balanced)
│  5. Response handling    │         │
└──────────────────────────┘         ▼
        │                      Stripe API
        │ response
        ▼
  Merchant / Caller

        │ registers
        ▼
 Eureka Discovery Server
        :8761
```

---

## How It Works

1. **Request received** — The service receives a `POST /v1/payments` request with payment details and an HMAC signature in the header.
2. **HMAC-SHA256 verification** — Spring Security validates the signature using the shared secret key. Requests with invalid or missing signatures are rejected with `401 Unauthorized`.
3. **Duplicate transaction check** — The transaction ID is looked up in MySQL. If it already exists, the request is rejected with `409 Conflict`.
4. **Payment threshold check** — The total payment amount is validated against the configured min/max thresholds. Out-of-range amounts are rejected with `400 Bad Request`.
5. **Forward to Stripe Service** — The validated request is forwarded to **STRIPE-SERVICE** using a load-balanced `RestClient` (resolved via Eureka — no hardcoded URL).
6. **Response handling** — The `hostedPageUrl` returned by Stripe is passed back to the caller.

---

## 🚀 Setup

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/validation-service.git
cd validation-service
```

### 2. Build

```bash
./mvnw clean install
```

### 3. Run

```bash
./mvnw spring-boot:run
```

> ⚠️ Start services in this order:
> 1. **Eureka Discovery Server** (`:8761`)
> 2. **Stripe Provider Service** (`:8081`)
> 3. **Validation Service** (`:8080`)

---

## 📦 Dependencies

`pom.xml`:

```xml
<!-- Eureka Client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

<!-- Load Balanced RestClient -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>

<!-- Spring Security (HMAC SHA256) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Spring Data JPA + MySQL -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## ⚙️ Configuration

`src/main/resources/application.properties`:

```properties
server.port=8080
spring.application.name=validation-service

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true

# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/payment_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# HMAC SHA256
hmac.secret.key=${HMAC_SECRET_KEY}

# Payment Threshold
payment.threshold.min=100
payment.threshold.max=100000

# Stripe Provider Service (resolved via Eureka)
stripe.service.name=stripe-service
```

> ⚠️ Use environment variables for all secrets — never commit credentials to version control.

---

## 🔗 Eureka Registration

```java
@SpringBootApplication
@EnableDiscoveryClient
public class ValidationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ValidationServiceApplication.class, args);
    }
}
```

Once running, this service appears as **VALIDATION-SERVICE** in the Eureka Dashboard at `http://localhost:8761`.

---

## 📡 API Endpoint

| Method | Endpoint        | Description                                        |
|--------|-----------------|----------------------------------------------------|
| POST   | `/v1/payments`  | Validate and forward payment to Stripe Service     |
| GET    | `/actuator/health` | Health check (used by Eureka)                   |

---

## 📨 Request & Response

### Request Body

`POST http://localhost:8080/v1/payments`

```json
{
  "transactionId": "txn_abc123xyz",
  "successUrl": "https://example.com/success",
  "cancelUrl": "https://example.com/cancel",
  "lineItems": [
    {
      "currency": "usd",
      "productName": "Smartphone",
      "unitAmount": 700,
      "quantity": 1
    },
    {
      "currency": "usd",
      "productName": "Wireless Earbuds",
      "unitAmount": 400,
      "quantity": 2
    }
  ]
}
```

### Required Headers

| Header              | Description                                      |
|---------------------|--------------------------------------------------|
| `X-HMAC-Signature`  | HMAC-SHA256 signature of the request body        |
| `Content-Type`      | `application/json`                               |

---

### ✅ Success Response — `200 OK`

```json
{
  "hostedPageUrl": "https://checkout.stripe.com/c/pay/cs_test_..."
}
```

### ❌ Error Responses

```json
{ "status": 401, "error": "Unauthorized",  "message": "Invalid HMAC signature"         }
{ "status": 409, "error": "Conflict",      "message": "Duplicate transaction detected"  }
{ "status": 400, "error": "Bad Request",   "message": "Payment amount exceeds threshold"}
{ "status": 500, "error": "Server Error",  "message": "Failed to reach Stripe service"  }
```

---

## 🔐 Security — HMAC SHA256

Every incoming request must include a valid **HMAC-SHA256** signature in the `X-HMAC-Signature` header. The signature is computed by the caller using the shared secret key and the raw request body.

```
HMAC-SHA256(secretKey, requestBody) → Base64 encoded signature
```

Spring Security intercepts the request, recomputes the signature server-side, and compares it with the header value. A mismatch results in `401 Unauthorized` — the request never reaches the business logic.

```java
// Signature verification (conceptual)
Mac mac = Mac.getInstance("HmacSHA256");
mac.init(new SecretKeySpec(secretKey.getBytes(), "HmacSHA256"));
String computed = Base64.encode(mac.doFinal(requestBody.getBytes()));
if (!computed.equals(incomingSignature)) throw new UnauthorizedException();
```

---

## 🔁 Duplicate Transaction Check

Before forwarding any request to Stripe, the service checks the `transactionId` against the MySQL database.

- If the `transactionId` **does not exist** → proceed and save it to the DB
- If the `transactionId` **already exists** → reject with `409 Conflict`

This prevents the same payment from being processed more than once due to retries or network errors.

```
Request → Check transactionId in MySQL
             │
      ┌──────┴──────┐
   Exists?         Not Exists
      │                 │
  409 Conflict     Save & proceed
```

---

## 💰 Payment Threshold

The total payment amount is validated against configured min/max limits before forwarding to Stripe.

| Rule            | Configured Value | Behaviour on Violation  |
|-----------------|------------------|-------------------------|
| Minimum amount  | `100` (cents)    | `400 Bad Request`       |
| Maximum amount  | `100000` (cents) | `400 Bad Request`       |

> Threshold values are configurable via `application.properties` — no code changes needed.

---

## ⚖️ Load Balanced Call to Stripe Service

The Validation Service calls the **Stripe Provider Service** by its Eureka service name — not a hardcoded URL. Spring Cloud LoadBalancer resolves the actual host and port at runtime.

```java
@Bean
@LoadBalanced
public RestClient.Builder restClientBuilder() {
    return RestClient.builder();
}

// Usage — service name resolved via Eureka
restClient.post()
    .uri("http://stripe-service/v1/payments")
    .body(paymentRequest)
    .retrieve()
    .toEntity(PaymentResponse.class);
```

This means if multiple instances of `stripe-service` are running, requests are automatically distributed across them.

---

## ⚠️ Exception Handling

All exceptions are handled globally via `@RestControllerAdvice` and returned as structured JSON.

| Scenario                        | HTTP Status | Description                               |
|---------------------------------|-------------|-------------------------------------------|
| Invalid HMAC signature          | `401`       | Signature mismatch or missing header      |
| Duplicate transaction           | `409`       | `transactionId` already exists in MySQL   |
| Amount below minimum threshold  | `400`       | Payment amount too low                    |
| Amount above maximum threshold  | `400`       | Payment amount too high                   |
| Stripe service unreachable      | `503`       | Load balancer could not reach Stripe      |
| Unexpected server error         | `500`       | Internal error during processing          |

---

## 📁 Project Structure

```
src/
└── main/
    ├── java/
    │   └── com/yourorg/validationservice/
    │       ├── ValidationServiceApplication.java  # Main entry point
    │       ├── controller/                         # REST controllers — /v1/payments
    │       ├── service/
    │       │   └── Interface/                      # Service interfaces & implementations
    │       ├── security/                           # HMAC-SHA256 filter & Spring Security config
    │       ├── repository/                         # JPA repository — duplicate TX check
    │       ├── entity/                             # Transaction entity (MySQL)
    │       ├── exception/                          # Global exception handler & custom exceptions
    │       ├── http/                               # Load balanced RestClient — calls Stripe service
    │       ├── pojo/                               # Request & Response model classes
    │       ├── threshold/                          # Payment threshold validation logic
    │       └── config/                             # RestClient bean, Security & Stripe config
    └── resources/
        └── application.properties                  # App, Eureka, MySQL & HMAC configuration
pom.xml
```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
