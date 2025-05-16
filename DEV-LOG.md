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

## 🗓️ 2025-04-14

- ✅ Integrated **JWT authentication and authorization** into the project.
- ✅ Configured **security filters** to validate JWTs and extract user details.
- ✅ Secured API endpoints using **role-based access** with `.hasRole(...)` in the security config.
- ✅ Learned **Redis** concepts, caching strategies, and planned its integration with utility methods.

## 🗓️ 2025-04-15

- ✅ Fully integrated Redis caching of frequently accessed data
- ✅ Caching reduced retrieval time to as low as 10ms

## 🗓️ 2025-04-16

- ✅ Added API to retrieve balances
- ✅ Added proper logging for errors

## 🗓️ 2025-04-17

- ✅ Added pessimistic locking for balance modification ensuring that two users don't settle expenses simultaneously

## 🗓️ 2025-04-21

- ✅ Explored redis and its functionalities and implemented API rate limiting using redis operations
- ✅ Added check to prevent calling APIs with user1 as the user2

## 🗓️ 2025-04-22

- ✅ Integrated external API for currency conversion
- ✅ Automated currency conversion by making it possible to pay in any currency
- ✅ Shifted my entire workspace from windows to **linux** using **WSL**

## 🗓️ 2025-04-29

- ✅ Learned and planned on decoupling the monolithic structure into microservices
- ✅ Developed algorithm for group debt settlement in minimal cash flow
- ✅ Added checks for not allowing negative values as payment or expense
- ✅ Made the security authorization role based

## 🗓️ 2025-05-12

- ✅ Added query optimization using DTO projection custom query
- ✅ Added lazy loading to List of friends in FriendList

## 🗓️ 2025-05-13

- ✅ Added Groups entity,Repository,Service and Controller layers
- ✅ Added addGroup functionality with endpoint API
- ✅ Designed all the GroupService endpoints
- ✅ used nmap,ping and mtr commands to troubleshoot external API not responding

## 🗓️ 2025-05-14

- ✅ Added addGroupMember API
- ✅ Added cache invalidation on adding expense or friend
- ✅ Refactored Expense,Payment and Balance entity to include groupId
- ✅ Added default cookie value for getBalance API and added comments for currency service

## 🗓️ 2025-05-15

- ✅ Added createGroupExpense API
- ✅ Used hashmaps,2-D Arrays and priorityQueues for the cash flow minimization algorithm
- ✅ Greedy approach to minimizing cash flow
- ✅ Refactored solo balances and expense methods to include groups
- ✅ Added a payload UserDebt for the group algorithm
- ✅ Added updateGroupBalance method
- ✅ Added @EqualsAndHashCode(exclude="") to tackle stackOverflowError due to bidirectional mapping between User and FriendList
- ✅ Added settlement logic for group expenses
- ✅ Added getGroups API
- ✅ Added getGroupUnsettledExpenses API and getGroupSettledExpenses API
- ✅ Integrated expenseSplits into expense GET APIs and removed GET getExpenseSplits API

## 🗓️ 2025-05-16

- ✅ Added getGroupBalance API
- ✅ Added caching using redis for currency service.Added currency feature for group balances
- ✅ Added a common id generator for payment and expense entity
- ✅ Added createGroupPayment API
- ✅ Removed .equals() with BigDecimal to .compareTo() 
- ✅ Added Logout API which uses a blacklist stored in redis cache
- ✅ Added redis caching for group expenses and cache invalidation on creating a group expense

## Future plan for the project

- I will be starting work on the frontend using ReactJS on the weekdays
- I will work on the backend in the weekends

### Backend work to be done:
- Start the decoupling by separating balance and group service using Event driven architecture through Apache kafka
- Using gRPC for inter-service communication

