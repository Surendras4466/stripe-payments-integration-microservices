# Stripe Payments Integration (Microservices)

A scalable and secure payment system built using Spring Boot and Microservices Architecture, integrating Stripe for real-time payment processing and webhook handling.

## 📖 Overview

This project demonstrates a real-world **Stripe Payment Integration System** designed with a microservices architecture.  

It focuses on:
- Secure payment processing  
- Validation and business rule handling  
- Asynchronous webhook notification handling  
- Scalable and maintainable service design  
The system is designed to be scalable, maintainable, and production-ready.


## 🏗️ Architecture
l
This project consists of the following services:

- **Stripe Provider Service**  
  Handles Stripe API integration and webhook notifications.

- **Payment Validation Service**  
  Validates payment requests and ensures data integrity.

- **Eureka Server**  
  Service discovery for managing microservices communication.

👉 Bonus: Add an architecture diagram image (very powerful for selection)
<img width="600" height="600" alt="Payment processing architecture diagram" src="https://github.com/user-attachments/assets/ff9b495c-65d7-4776-8f1d-62c3a352caf1" />


## 🧩 Responsibilities & Contributions

- Implemented Stripe integration using **Spring Boot & Microservices**
- Developed **Stripe Provider Service** for API integration:
  - Create Session  
  - Retrieve Session  
  - Expire Session  

- Built **Payment Validation Service** with:
  - Modular validation framework  
  - Flexible business rule design  
  - Redis caching for performance  

- Secured APIs using:
  - **HmacSHA256 signature verification**
  - **Spring Security**
  - **Stripe Basic Auth**

- Developed:
  - Custom error codes  
  - Centralized exception handling  

- Processed Stripe webhook events and performed testing  

- Worked with:
  - MySQL (Spring JDBC)  
  - AWS (EC2, RDS, Secrets Manager)  

- Applied:
  - Design Patterns (Factory, Builder)  
  - OOP principles  

---
## ⚙️ Tech Stack

### 👨‍💻 Backend
- Java  
- Spring Boot  
- Spring Security  
- Spring JDBC  

### 🔗 Microservices & Architecture
- Microservices Architecture  
- Eureka Service Registry  
- Circuit Breaker  

### 💳 Integration
- Stripe API  
- REST APIs (JSON, CURL, Postman, Swagger)  

### 🗄️ Database & Cache
- MySQL (AWS RDS)  
- Redis Cache  

### ☁️ Cloud & DevOps
- AWS (EC2, RDS, Secrets Manager)  
- Maven  

### 🧪 Testing
- JUnit  
- Mockito  

### 🛠️ Tools
- Git, Bitbucket, SourceTree  
- Eclipse, DBeaver  
- Jira  
- SLF4J, Logback  
- SonarQube  
- Lombok, Jackson  

### 🤖 AI Tools
- GitHub Copilot  
- ChatGPT  
- Claude  

---

## ✨ Features

- Secure Stripe payment integration
- Webhook handling with signature verification
- Real-time payment status updates
- Microservices-based architecture
- Service discovery using Eureka
- Local webhook testing using Stripe CLI

This shows real understanding:

## 🔄 Webhook Flow
This shows real understanding:

1. User initiates payment
2. Stripe processes the payment
3. Stripe sends a webhook event
4. Webhook endpoint verifies signature
5. Payment status is updated in the system
🚀 7. How to Run the Project
## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven
- Stripe CLI
-redis cache
### Steps
1. Clone the repository
2. Start Eureka Server
3.  Start Payment Validation Service
4. Start Stripe Provider Service
5. Run Stripe CLI:
   stripe listen --forward-to localhost:8080/webhook
🔗 8. API Endpoints (Optional but Powerful)
## 📡 API Endpoints

- POST http://localhost:8080/v1/payments  ->Validation service 
- POST http://localhost:8083/v1/payments  ->Stripe Service
- POST http://localhost:8083/v1/stripe/notifications -> webhook 

📸 Add Screenshots / Demo Video

👉 Add:
   
**Postman screenshots**
<img width="600" alt="Sucess respone" src="https://github.com/user-attachments/assets/9ed80962-5f2d-4470-9ccd-78e34646ef54" />
<img width="600" alt="payment thresold" src="https://github.com/user-attachments/assets/a76288e4-c42c-4807-a7b4-a86104b92426" />

**Stripe CLI logs**


<img width="600" alt="Stripe cli events" src="https://github.com/user-attachments/assets/4465271e-6d46-481a-a717-196da8cf499f" />

**Eureka Console**


<img width="600" alt="Eureka console" src="https://github.com/user-attachments/assets/0bba382e-721c-40fe-93a2-ecf2927f9df2" />



****payment chekcout session ****



<img width="600" alt="payment sucess card details" src="https://github.com/user-attachments/assets/1a8dc923-164a-42d9-a7ed-b21ad96fc7da" />




<img width="600" alt="payment fail card decline" src="https://github.com/user-attachments/assets/94b527fc-f050-403e-b52d-ce9276401cf3" />




**Your LinkedIn video link:**

https://www.linkedin.com/posts/surendra2_hiring-opentowork-javadeveloper-ugcPost-7444063846241300480-5Kq8?utm_source=share&utm_medium=member_desktop&rcm=ACoAAEQy49gBM4sVxfbnhAred5p5UYF2RJfksus


**👨‍💻 Author**

**Vadde Gola Surendra**

Java Full Stack Developer | Microservices Enthusiast


🎓 B.Tech in Computer Science Engineering (2024 Graduate)

💼 LinkedIn: https://www.linkedin.com/in/surendra2/

📧 Email: vgssurendra4466@gmail.com
