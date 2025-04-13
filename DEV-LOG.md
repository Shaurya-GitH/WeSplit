# 🛠️ WeSplit Project — Dev Logs

A development log to track daily progress, ideas, bugs, and reflections while building **WeSplit**, a Splitwise-inspired expense sharing app.

---

## 📦 Project Setup Summary
## 🏗️ Initial Project Setup (First Commit Only)

- I started with **requirement elicitation** for the project to clearly define the scope and features of WeSplit.
- Then I created an **ER diagram** to visualize and understand the structure of the database.
- Based on the ER diagram, I implemented the **database using Spring Data JPA**, ensuring proper entity relationships and normalizing the schema up to **Third Normal Form (3NF)**.
- I structured the project into **separate layers** (Controller, Service, Repository, Entity, DTO) following **RESTful API architecture** principles.
- After writing the business logic for core features, I **tested each endpoint using Postman** and incorporated **feedback from peers** to refine and improve the implementation.
- Through working with **Swagger documentation**, I realized the **importance of using DTOs** to clearly expose only relevant data in APIs and improve the structure of responses.
- Finally, I implemented **HTTP Basic Authorization** to secure the project endpoints and restrict unauthorized access.

---

## 📅 2025-04-13

### ✅ What I did today
- Explored the **difference between Basic Authentication and JWT-based authentication**:
    - Learned that **Basic Auth** sends the username and password encoded in the header with every request, making it simpler but less secure for stateless applications.
    - Understood that **JWT (JSON Web Token)** is a stateless, token-based authentication system where the server issues a signed token after login, which is then sent in headers for subsequent requests.
- Gained a deeper understanding of **what JWT actually is**:
    - A compact, URL-safe token format that includes a header, payload, and signature.
    - Realized that JWT can store user identity and other metadata in its payload, allowing for efficient and secure user verification without hitting the database every time.

---

