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
- [Architecture](#architecture)
- [How It Works](#how-it-works)
- [Setup](#setup)
- [Dependencies](#dependencies)
- [Configuration](#configuration)
- [Eureka Registration](#eureka-registration)
- [API Endpoint](#api-endpoint)
- [Request & Response](#request--response)
- [Stripe Checkout Page](#stripe-checkout-page)
- [Exception Handling](#exception-handling)
- [Project Structure](#project-structure)

---

## Overview

This service acts as the **payment layer** of the microservices ecosystem. It accepts a checkout request containing line items, success and cancel URLs, then calls the **Stripe Checkout Sessions API** using Spring's `RestClient`. The response (hosted page URL) is returned to the caller, and all Stripe/runtime errors are handled gracefully.

---

## 🏗 Architecture

![Payment Flow](Stripe_Arch.png)

> **fig: payment flow**

The diagram above shows the full request-response cycle:

- **Merchant Account** sends a payment request to the **Controller**
- **Controller** delegates to the **Service** layer
- **Service** uses the **Http Service Engine** (business logic) to build the Stripe request
- The response from Stripe comes back as **JSON** through the Http layer
- **Java Object** mapping converts the JSON into typed response models
- A **Webhook** handles async Stripe events (e.g. payment confirmation)
- The final **response** is returned to the Merchant Account

---

## How It Works

1. **Request received** — The service receives a `POST` request with `successUrl`, `cancelUrl`, and a list of `lineItems`.
2. **Business logic** — Each line item is mapped into a Stripe-compatible `price_data` object inside the `Service` layer.
3. **RestClient call** — The `Http` layer calls the Stripe Checkout Sessions API using Spring's `RestClient` with the secret key in the `Authorization` header.
4. **Response handling** — The Stripe `hostedPageUrl` is extracted from the JSON response and returned to the caller.
5. **Exception handling** — Stripe API errors and unexpected runtime errors are caught by the `Exception` layer and returned as structured error responses.

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
server.port=8081
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

| Method | Endpoint            | Description                      |
|--------|---------------------|----------------------------------|
| POST   | `/v1/payments`      | Create a Stripe Checkout Session |
| GET    | `/actuator/health`  | Health check (used by Eureka)    |

---

## 📨 Request & Response

### Request Body

`POST http://localhost:8081/v1/payments`

```json
{
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

| Field                         | Type    | Description                                                    |
|-------------------------------|---------|----------------------------------------------------------------|
| `successUrl`                  | String  | Redirect URL after successful payment                          |
| `cancelUrl`                   | String  | Redirect URL if payment is cancelled                           |
| `lineItems[].currency`        | String  | Currency code (e.g. `usd`, `eur`)                             |
| `lineItems[].productName`     | String  | Product name shown on the Stripe checkout page                 |
| `lineItems[].unitAmount`      | Integer | Price in smallest currency unit (e.g. cents: `700` = $7.00)   |
| `lineItems[].quantity`        | Integer | Quantity of the product                                        |

---

### ✅ Success Response — `200 OK`

![Success Response - Postman](Sucess_respone.png)

On success, the service returns a `hostedPageUrl` — the Stripe-generated checkout URL to redirect the user to:

```json
{
  "hostedPageUrl": "https://checkout.stripe.com/c/pay/cs_test_..."
}
```

---

## 💳 Stripe Checkout Page

Once the user is redirected to the `hostedPageUrl`, they land on the Stripe-hosted checkout page:

![Stripe Checkout Page](payment_sucess_card_details.png)

The checkout page shows:
- **Line items** — product names, quantities, and prices (e.g. Smartphone × 1, Wireless Earbuds × 2)
- **Currency selection** — supports INR and USD with live conversion
- **Payment method** — card details (number, expiry, CVV)
- **Contact information** — email field
- **Pay button** — submits the payment to Stripe

> 🔒 The checkout page is fully hosted and secured by Stripe — no card data touches your service.

---

## ⚠️ Exception Handling

All exceptions are handled globally and returned as structured JSON responses.

| Scenario                          | HTTP Status | Description                              |
|-----------------------------------|-------------|------------------------------------------|
| Invalid request body              | `400`       | Missing or invalid fields in the request |
| Stripe API authentication failure | `401`       | Invalid or missing Stripe secret key     |
| Stripe API error                  | `402`       | Session creation failed on Stripe's side |
| Unexpected server error           | `500`       | Internal error during processing         |

---

## 📁 Project Structure

```
src/
└── main/
    ├── java/
    │   └── com/yourorg/stripeservice/
    │       ├── Constant/               # App-wide constants (API URLs, headers, etc.)
    │       ├── Controller/             # REST controllers — exposes /v1/payments
    │       ├── Exception/              # Global exception handler & custom exceptions
    │       ├── Http/                   # RestClient setup & Stripe API calls
    │       ├── Pojo/                   # Request & Response model classes
    │       ├── Service/
    │       │   └── Interface/          # Service interfaces & implementations
    │       ├── Stripe/                 # Stripe-specific mapping & session logic
    │       ├── Utill/                  # Utility/helper classes
    │       └── config/                 # Spring configuration (RestClient bean, etc.)
    └── resources/
        └── application.properties      # App, Eureka & Stripe configuration
pom.xml
```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
