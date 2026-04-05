# 💳 Stripe Payment Provider Service

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-Netflix%20Eureka%20Client-blue?logo=spring)
![Maven](https://img.shields.io/badge/Build-Maven-red?logo=apachemaven)
![Stripe](https://img.shields.io/badge/Payment-Stripe-635bff?logo=stripe)
![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=java)

A **Stripe Payment Provider Microservice** built with Spring Boot. This service contains the full business logic for initiating a Stripe Checkout Session — calling the Stripe API via **RestClient**, handling the response, and managing exceptions. It registers itself with the **Eureka Discovery Server** for service discovery.

---

## 📖 Table of Contents

- [Overview](#overview)
- [How It Works](#how-it-works)
- [Setup](#setup)
- [Dependencies](#dependencies)
- [Configuration](#configuration)
- [Eureka Registration](#eureka-registration)
- [API Endpoint](#api-endpoint)
- [Request & Response](#request--response)
- [Exception Handling](#exception-handling)
- [Project Structure](#project-structure)

---

## Overview

This service acts as the **payment layer** of the microservices ecosystem. It accepts a checkout request containing line items, success and cancel URLs, then calls the **Stripe Checkout Sessions API** using Spring's `RestClient`. The response (session URL) is returned to the caller, and all Stripe/runtime errors are handled gracefully.

```
  Caller / API Gateway
         │
         │  POST /api/payment/checkout
         ▼
  ┌─────────────────────┐
  │   Stripe Service    │  ──── RestClient ────►  Stripe Checkout API
  │      :8082          │  ◄─── Session URL ─────  (api.stripe.com)
  └────────┬────────────┘
           │ registers
           ▼
  Eureka Discovery Server
        :8761
```

---

## How It Works

1. **Request received** — The service receives a `POST` request with `successUrl`, `cancelUrl`, and a list of `lineItems`.
2. **Business logic** — Each line item is mapped into a Stripe-compatible `price_data` object.
3. **RestClient call** — The service calls the Stripe Checkout Sessions API using Spring's `RestClient` with the secret key in the `Authorization` header.
4. **Response handling** — The Stripe session URL is extracted from the response and returned to the caller.
5. **Exception handling** — Stripe API errors and unexpected runtime errors are caught and returned as structured error responses.

---

## 🚀 Setup

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/stripe-service.git
cd stripe-service
```

### 2. Build

```bash
./mvnw clean install
```

### 3. Run

```bash
./mvnw spring-boot:run
```

> ⚠️ Make sure the **Eureka Discovery Server** is running at `http://localhost:8761` before starting this service.

---

## 📦 Dependencies

`pom.xml`:

```xml
<!-- Eureka Client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

> Spring's built-in `RestClient` (available from Spring Boot 3.2+) is used to call the Stripe API — no additional HTTP client dependency required.

---

## ⚙️ Configuration

`src/main/resources/application.properties`:

```properties
server.port=8082
spring.application.name=stripe-service

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true

# Stripe
stripe.api.key=${STRIPE_SECRET_KEY}
stripe.api.base-url=https://api.stripe.com/v1
```

> ⚠️ **Never hardcode your Stripe secret key.** Always use an environment variable:
> ```bash
> export STRIPE_SECRET_KEY=sk_test_your_key_here
> ```

---

## 🔗 Eureka Registration

```java
@SpringBootApplication
@EnableDiscoveryClient
public class StripeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(StripeServiceApplication.class, args);
    }
}
```

Once running, this service appears as **STRIPE-SERVICE** in the Eureka Dashboard at `http://localhost:8761`.

---

## 📡 API Endpoint

| Method | Endpoint                    | Description                          |
|--------|-----------------------------|--------------------------------------|
| POST   | `/api/payment/checkout`     | Create a Stripe Checkout Session     |
| GET    | `/actuator/health`          | Health check (used by Eureka)        |

---

## 📨 Request & Response

### Request Body

`POST /api/payment/checkout`

```json
{
  "successUrl": "https://example.com/success",
  "cancelUrl": "https://example.com/cancel",
  "lineItems": [
    {
      "currency": "usd",
      "productName": "Product A",
      "unitAmount": 1000,
      "quantity": 1
    },
    {
      "currency": "usd",
      "productName": "Product B",
      "unitAmount": 2000,
      "quantity": 2
    }
  ]
}
```

| Field                        | Type    | Description                                            |
|------------------------------|---------|--------------------------------------------------------|
| `successUrl`                 | String  | URL to redirect to after successful payment            |
| `cancelUrl`                  | String  | URL to redirect to if payment is cancelled             |
| `lineItems`                  | Array   | List of products in the checkout session               |
| `lineItems[].currency`       | String  | Currency code (e.g. `usd`, `eur`)                     |
| `lineItems[].productName`    | String  | Display name of the product on the Stripe checkout page|
| `lineItems[].unitAmount`     | Integer | Price in smallest currency unit (e.g. cents for USD)  |
| `lineItems[].quantity`       | Integer | Quantity of the product                                |

> 💡 `unitAmount` is in the **smallest currency unit** — `1000` = $10.00 USD.

---

### Success Response

```json
{
  "sessionUrl": "https://checkout.stripe.com/pay/cs_test_abc123..."
}
```

Redirect the user to `sessionUrl` to complete payment on the Stripe-hosted checkout page.

---

### Error Response

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid line item: unitAmount must be greater than 0"
}
```

---

## ⚠️ Exception Handling

All exceptions are handled globally and returned as structured JSON responses.

| Scenario                          | HTTP Status | Description                                      |
|-----------------------------------|-------------|--------------------------------------------------|
| Invalid request body              | `400`       | Missing or invalid fields in the request         |
| Stripe API authentication failure | `401`       | Invalid or missing Stripe secret key             |
| Stripe API error                  | `402`       | Payment/session creation failed on Stripe's side |
| Unexpected server error           | `500`       | Internal error during processing                 |

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/yourorg/stripeservice/
│   │       ├── StripeServiceApplication.java   # Main entry point
│   │       ├── controller/
│   │       │   └── PaymentController.java      # POST /api/payment/checkout
│   │       ├── service/
│   │       │   └── StripeService.java          # Business logic & RestClient call
│   │       ├── model/
│   │       │   ├── CheckoutRequest.java        # Incoming request model
│   │       │   ├── LineItem.java               # Line item model
│   │       │   └── CheckoutResponse.java       # Response model (sessionUrl)
│   │       ├── exception/
│   │       │   ├── GlobalExceptionHandler.java # @RestControllerAdvice handler
│   │       │   └── StripeApiException.java     # Custom Stripe exception
│   │       └── config/
│   │           └── RestClientConfig.java       # RestClient bean configuration
│   └── resources/
│       └── application.properties              # App & Eureka config
└── test/
    └── java/
        └── com/yourorg/stripeservice/          # Unit & integration tests
pom.xml
```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
