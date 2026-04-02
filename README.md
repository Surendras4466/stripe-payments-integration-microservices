# Stripe Payments Integration — Microservices

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)
![Stripe](https://img.shields.io/badge/Stripe-API-blueviolet)
![AWS](https://img.shields.io/badge/AWS-EC2_RDS_SecretsManager-orange)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

A scalable and secure payment system built using Spring Boot and Microservices Architecture, integrating Stripe for real-time payment processing and webhook handling.

---

## 📖 Overview

This project demonstrates a real-world Stripe Payment Integration System designed with a microservices architecture. It focuses on:

- Secure payment processing
- Validation and business rule handling
- Asynchronous webhook notification handling
- Scalable and maintainable service design

---

## 🏗️ Architecture

![Architecture Diagram] <img width="600"  alt="Screenshot 2026-04-02 131936" src="https://github.com/user-attachments/assets/9530a998-5fd2-4606-a33d-17b32c776c37" />
)
 
This project consists of the following services:

| Service | Port | Responsibility |
|---|---|---|
| Eureka Server | 8761 | Service discovery and registry |
| Payment Validation Service | 8080 | Request validation, Redis cache, HMAC signature verification |
| Stripe Provider Service | 8083 | Stripe API — create / retrieve / expire checkout session |

---

## 🧩 Responsibilities & Contributions

- Implemented Stripe integration using Spring Boot and Microservices
- Developed **Stripe Provider Service** for API integration:
  - Create Session
  - Retrieve Session
  - Expire Session
- Built **Payment Validation Service** with:
  - Modular validation framework
  - Flexible business rule design
  - Redis caching for performance
- Secured APIs using:
  - HmacSHA256 signature verification
  - Spring Security
  - Stripe Basic Auth
- Developed custom error codes and centralized exception handling
- Processed Stripe webhook events and performed end-to-end testing
- Worked with MySQL (Spring JDBC) and AWS (EC2, RDS, Secrets Manager)
- Applied design patterns: Factory, Builder, and core OOP principles

---

## ⚙️ Tech Stack

**Backend**
Java · Spring Boot · Spring Security · Spring JDBC

**Microservices & Architecture**
Microservices Architecture · Eureka Service Registry · Circuit Breaker

**Integration**
Stripe API · REST APIs (JSON) · Postman · Swagger

**Database & Cache**
MySQL (AWS RDS) · Redis Cache

**Cloud & DevOps**
AWS EC2 · AWS RDS · AWS Secrets Manager · Maven

**Testing**
JUnit · Mockito

**Tools**
Git · Bitbucket · SourceTree · Eclipse · DBeaver · Jira · SLF4J · Logback · SonarQube · Lombok · Jackson

---

## ✨ Features

- Secure Stripe payment integration
- Webhook handling with HMAC signature verification
- Real-time payment status updates
- Microservices-based architecture with Eureka service discovery
- Local webhook testing using Stripe CLI

---

## 🔄 Webhook Flow

1. User initiates a payment request
2. Payment Validation Service validates and forwards the request
3. Stripe Provider Service creates a checkout session via the Stripe API
4. Stripe processes the payment and sends a webhook event
5. Webhook endpoint verifies the HMAC signature
6. Payment status is updated in the database

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Redis (running on `localhost:6379`)
- [Stripe CLI](https://stripe.com/docs/stripe-cli)
- AWS credentials configured (EC2, RDS, Secrets Manager access)

### Configuration

Copy the environment template and fill in your values:

```bash
cp .env.example .env
```

All secrets (Stripe API key, DB credentials) are managed via AWS Secrets Manager. See [Configuration Reference](#configuration-reference) below.

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/stripe-payments-microservices.git
   cd stripe-payments-microservices
   ```

2. **Start Eureka Server** (must be first)
   ```bash
   cd eureka-server
   mvn spring-boot:run
   ```

3. **Start Payment Validation Service**
   ```bash
   cd payment-validation-service
   mvn spring-boot:run
   ```

4. **Start Stripe Provider Service**
   ```bash
   cd stripe-provider-service
   mvn spring-boot:run
   ```

5. **Forward Stripe webhooks locally**
   ```bash
   stripe listen --forward-to localhost:8083/v1/stripe/notifications
   ```

6. **Verify** — open `http://localhost:8761` to confirm all services are registered in the Eureka console.

---

## 📡 API Endpoints

### Payment Validation Service — `:8080`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/v1/payments` | Stripe Basic Auth | Validate and forward a payment request |

### Stripe Provider Service — `:8083`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/v1/payments` | Stripe Basic Auth | Create a Stripe checkout session |
| GET | `/v1/payments/{id}` | Stripe Basic Auth | Retrieve a session by ID |
| DELETE | `/v1/payments/{id}` | Stripe Basic Auth | Expire a checkout session |
| POST | `/v1/stripe/notifications` | HMAC signature | Receive and process Stripe webhook events |

---

## ⚙️ Configuration Reference

### stripe-provider-service

| Variable | Description | Source |
|---|---|---|
| `STRIPE_SECRET_KEY` | Stripe secret API key | AWS Secrets Manager |
| `STRIPE_WEBHOOK_SECRET` | Webhook signing secret | AWS Secrets Manager |
| `DB_URL` | MySQL RDS connection URL | AWS Secrets Manager |
| `EUREKA_HOST` | Eureka server hostname | `application.yml` |
| `REDIS_HOST` | Redis host address | `application.yml` |

See `.env.example` for the full list of required variables.

---

## 🧪 Testing

Run unit tests for each service:

```bash
# Payment Validation Service
cd payment-validation-service
mvn test

# Stripe Provider Service
cd stripe-provider-service
mvn test
```

For webhook testing, use the Stripe CLI as shown in Getting Started step 5. Check the CLI output for event delivery confirmation and signature verification logs.

---

## 📁 Repository Structure

```
stripe-payments-microservices/
├── docs/
│   ├── architecture.svg
│   └── postman-collection.json
├── eureka-server/
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── payment-validation-service/
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── stripe-provider-service/
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── .env.example
├── pom.xml
└── README.md
```

---

## 👨‍💻 Author

**Vadde Gola Surendra**
Java Full Stack Developer | Microservices Enthusiast
🎓 B.Tech in Computer Science Engineering (2024 Graduate)

[![LinkedIn](https://img.shields.io/badge/LinkedIn-surendra2-blue?logo=linkedin)](https://www.linkedin.com/in/surendra2/)
[![Demo](https://img.shields.io/badge/Demo-LinkedIn_Video-blueviolet)](https://www.linkedin.com/posts/surendra2_hiring-opentowork-javadeveloper-ugcPost-7444063846241300480-5Kq8)

📧 vgssurendra4466@gmail.com
