# WeSplit - Expense Sharing Web App 

**WeSplit** is a full-stack expense sharing application inspired by Splitwise. Designed to make managing shared expenses, group balances, and settlements easier, it's ideal for roommates, travel groups, or anyone who splits costs regularly.

-Frontend repository: https://github.com/Shaurya-GitH/WeSplit-Frontend

---

## 🔑 Features

###  User Authentication
- Secure user registration and login using **Spring Security** and **JWT tokens**.

###  Expense Tracking
- Add expenses, assign participants, and split amounts evenly or unevenly.

###  Group Management
- Create and manage groups to organize shared expenses among multiple users.
- Cash flow minimization algorithm
- Automatically settle expenses through payments or creating expenses

###  Balance Calculation
- Real-time tracking of who owes whom and how much.
- External API integration for automatic currency conversion while settling balances

###  Robust REST API
- Clean and modular APIs built with **Spring Boot** and validated thoroughly.
- Follows RESTful principles with DTO projections and proper layering.

###  MySQL + Hibernate/JPA
- Efficient relational data handling with **eager loading**, **DTO projections**, and prevention of **N+1 query issues**.

### Caching using Redis
- Used NoSQL database **Redis** for caching of data which require heavy SQL queries and accessed frequently reducing time to as low as **10ms**
- API rate limiting using Redis 

###  API Documentation
- Integrated **Swagger UI** for easy visualization and testing of endpoints.

---

## 🛣️ Planned Features

-  **Activity Feed** using **Kafka**
-  **Notification System** for expenses & settlements using websockets
-  **Test Coverage** using JUnit & Mockito

---

## 💻 Tech Stack

- **Backend:** Java, Spring Boot, Spring Security, Spring Data JPA, MySQL, Redis
- **Tools:** Swagger, Postman, IntelliJ IDEA, Maven
- **Version Control:** Git, GitHub

---

