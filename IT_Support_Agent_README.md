# IT Support Agent

An AI-powered internal IT support agent built for the AIONOS Agentic AI
Factory assignment.

The application allows employees to describe IT issues in natural
language. The Spring Boot backend uses an AI model to understand the
request, searches the internal MySQL knowledge base, and returns one of
three controlled outcomes:

-   `RESOLVED` --- a matching policy/resolution is found.
-   `FOLLOW_UP` --- more information is required.
-   `ESCALATED` --- the issue requires human/security review and a
    structured ticket is created.

The AI model is used as an understanding/classification layer. It does
not directly access the database or perform unrestricted system actions.

## Features

-   Natural-language IT issue handling
-   AI-assisted issue classification
-   MySQL knowledge-base search
-   Follow-up questions for unclear requests
-   Policy/source citation in resolved responses
-   Security-related escalation
-   Structured ticket creation
-   Audit logging of important agent actions
-   React frontend
-   Spring Boot REST API
-   Controlled backend decision flow
-   API key kept outside the Git repository using an environment
    variable

## Architecture

``` text
Employee
   |
   v
React + Vite Frontend
   |
   | POST /api/agent/chat
   v
Spring Boot Backend
   |
   v
AgentService
   |
   +----> OpenAI API
   |       |
   |       +--> Issue understanding / classification
   |
   +----> MySQL Knowledge Base
   |
   v
Decision
   |
   +--> RESOLVED
   |      |
   |      +--> Answer + KB source
   |
   +--> FOLLOW_UP
   |      |
   |      +--> Ask employee for more information
   |
   +--> ESCALATED
          |
          +--> Create ticket
          +--> Save audit log
```

The backend remains responsible for database searches, ticket creation,
and audit logging.

## Technology Stack

### Frontend

-   React
-   Vite
-   JavaScript
-   HTML
-   CSS

### Backend

-   Java
-   Spring Boot
-   Spring Web
-   Spring Data JPA
-   REST APIs
-   Maven
-   Spring AI / OpenAI integration

### Database

-   MySQL
-   Hibernate / JPA

### Tools

-   IntelliJ IDEA
-   VS Code
-   Postman
-   Git
-   GitHub

## Project Structure

``` text
it-support-agent/
|
├── Backend/
│   ├── .mvn/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/veridian/it_support_agent/
│   │   │   │       ├── controller/
│   │   │   │       ├── dto/
│   │   │   │       ├── entity/
│   │   │   │       ├── repository/
│   │   │   │       └── service/
│   │   │   │           ├── AgentService.java
│   │   │   │           ├── AuditLogService.java
│   │   │   │           ├── EscalationService.java
│   │   │   │           ├── GeminiService.java
│   │   │   │           └── PolicySearchService.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── data.sql
│   │   └── test/
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
│
├── Frontend/
│   └── it-support-frontend/
│       ├── src/
│       ├── public/
│       ├── package.json
│       ├── package-lock.json
│       └── vite.config.js
│
└── README.md
```

> Some backend class/file names still contain `Gemini` because the
> project evolved from an earlier Gemini-based design. The current
> `application.properties` configuration uses `spring.ai.openai.*`.

## Main API

### Agent Chat

``` http
POST /api/agent/chat
Content-Type: application/json
```

Example request:

``` json
{
  "employeeName": "Hariom",
  "message": "My laptop is completely dead"
}
```

Example response:

``` json
{
  "answer": "Laptops are eligible for replacement after 3 years of service, or earlier in case of verified hardware failure.",
  "status": "RESOLVED",
  "source": "KB-03",
  "ticketId": null,
  "escalated": false
}
```

### Other APIs

``` text
GET /api/requests
GET /api/tickets
GET /api/policies/search?query=VPN
GET /api/audit
```

## Agent Decision Flow

1.  Receive the employee's issue.
2.  Use the configured AI service to understand/classify the issue.
3.  Enrich the policy search using the detected intent/category.
4.  Search the internal MySQL knowledge base.
5.  If the request is unclear, return `FOLLOW_UP`.
6.  If a confident policy match is found, return `RESOLVED`.
7.  If the request indicates a security or escalation case, return
    `ESCALATED`.
8.  Create a structured ticket for escalated cases.
9.  Save important actions in the audit trail.

## Example Scenarios

### Ambiguous Request

``` text
Something is wrong with my computer
```

Expected:

``` text
FOLLOW_UP
```

### Laptop Issue

``` text
My laptop is completely dead
```

Expected:

``` text
RESOLVED
```

### Security Incident

``` text
I received a suspicious phishing email
```

Expected:

``` text
ESCALATED
```

A structured ticket should be created and the action should be recorded
in the audit log.

## Database

The application uses MySQL.

Main logical tables:

``` text
knowledge_base
employee_requests
tickets
audit_logs
```

Create the database:

``` sql
CREATE DATABASE veridian_it_support;
```

Update the database username/password in
`Backend/src/main/resources/application.properties` if your local MySQL
configuration is different.

## OpenAI API Key

**Never store a real API key in `application.properties` or commit it to
GitHub.**

The application reads the key from:

``` properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
```

### Windows PowerShell

``` powershell
setx OPENAI_API_KEY "YOUR_OPENAI_API_KEY"
```

Restart IntelliJ/your terminal after setting the variable.

Check it with:

``` powershell
echo $env:OPENAI_API_KEY
```

Use your own API key. If a key is accidentally committed, revoke/rotate
it immediately.

## Run the Backend

Open a terminal in the backend:

``` bash
cd Backend
```

On Windows:

``` bash
mvnw.cmd clean compile
mvnw.cmd spring-boot:run
```

Backend:

``` text
http://localhost:8080
```

If Maven reports a `JAVA_HOME` error, configure a valid JDK before
running the Maven wrapper.

## Run the Frontend

``` bash
cd Frontend/it-support-frontend
npm install
npm run dev
```

Frontend:

``` text
http://localhost:5173
```

## Running After Cloning from GitHub

``` bash
git clone https://github.com/Hariom9162/it-support-agent.git
cd it-support-agent
```

Then:

1.  Install Java and configure `JAVA_HOME`.
2.  Install/configure MySQL.
3.  Create the `veridian_it_support` database.
4.  Configure local MySQL credentials.
5.  Set your own `OPENAI_API_KEY`.
6.  Start the Spring Boot backend.
7.  Install frontend dependencies with `npm install`.
8.  Start the Vite frontend with `npm run dev`.

The repository intentionally contains **no real API key**. Each
developer provides their own key through an environment variable.

## Testing Checklist

  Scenario                   Expected Result
  -------------------------- ----------------------------
  Ambiguous computer issue   `FOLLOW_UP`
  Laptop hardware issue      `RESOLVED`
  VPN/policy issue           `RESOLVED`
  Security/phishing issue    `ESCALATED`
  Unknown/unclear issue      `FOLLOW_UP` or `ESCALATED`
  Escalated request          Ticket + Audit Log

## Audit Trail

Important agent actions are recorded in `audit_logs`, including:

-   Original request
-   Follow-up question when applicable
-   Detected intent
-   Knowledge-base source
-   Action taken
-   Escalation status
-   Timestamp

## Security / Control Design

``` text
AI Model
   |
   | classification / understanding
   v
Spring Boot AgentService
   |
   +--> Controlled MySQL search
   |
   +--> Controlled ticket creation
   |
   +--> Controlled audit logging
```

The model does not receive unrestricted database access or permission to
execute arbitrary application actions.

## Current Development Status

The repository contains the backend, frontend, database initialization
resources, REST controllers, services, entities, repositories, and
documentation.

The project is currently being tested and debugged locally, including:

-   Spring Boot startup
-   Maven/JDK configuration
-   MySQL connectivity
-   AI API configuration
-   Agent decision flow
-   Frontend/backend integration

## Future Improvements

-   Authentication and role-based access
-   Admin dashboard for tickets
-   Advanced RAG/vector search
-   Persistent multi-turn conversation history
-   Human ticket management
-   Automated escalation notifications
-   Docker deployment
-   Automated unit/integration tests
-   Production monitoring and observability

## Author

**Hariom Kumar**

B.Tech --- Computer Science Engineering

GitHub: https://github.com/Hariom9162

## License

This project was developed as an academic/technical assessment project.
