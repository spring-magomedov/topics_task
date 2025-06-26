# Spring Boot CRUD API

## Description

This Spring Boot application implements a RESTful CRUD API for managing entities: `Topics`, `Reactions` and `Questions`. The application also integrates with an external API to fetch weather data by city name.

## Technologies

*   **Programming Language:** Java 17
*   **Framework:** Spring Boot (3.x)
*   **Dependency Management:** Maven
*   **Development Acceleration:** Lombok
*   **Web Development:** Spring Web
*   **Data Handling:** Spring Data JPA
*   **Database Migrations:** Liquibase
*   **Database Driver:** PostgreSQL
*   **Data Validation:** Validation API (jakarta.validation)
*   **Monitoring & Management:** Spring Boot Actuator
*   **Metrics:** Prometheus
*   **Integration Testing:** Testcontainers, JUnit 5
*   **HTTP Client:** OpenFeign

## Prerequisites

Ensure the following software is installed on your machine:

*   Java 17
*   Maven
*   Docker (for running via Docker Compose)

## Building the Project

Run the following command in the project root directory:

```bash
mvn clean install
```
To build the project while skipping tests:

```bash
mvn clean install -DskipTests
```
# Running the Application
## Run via Spring Boot
You can run the application directly from your IDE or execute the following command in the project root directory:
```bash
mvn spring-boot:run
```
## Run via Docker Compose
1. Ensure Docker is installed on your machine.
2. Build the Docker Compose images:
```
docker compose build
```
3. Start the application and PostgreSQL using Docker Compose:
```
docker-compose up -d
```
Make sure your docker-compose.yml file is configured to connect to the PostgreSQL database.
# API Endpoints
## Topics
* **GET /topics/weather?city={city}**: Fetches weather for the specified city (uses OpenFeign to call an external API).
* **GET /topics**: Retrieves all topics with pagination and filtering by title.
  * Parameters: page, size, title (optional).
* **GET /topics/{id}**: Retrieves a topic by ID.
* **POST /topics**: Creates a new topic.
* **PUT /topics/{id}**: Updates an existing topic.
* **PATCH /topics/{id}**: Partially updates an existing topic.
* **DELETE /topics/{id}**: Deletes a topic.
* **GET /topics/extended/{id}**: Retrieves a topic by ID with extended information.
##  Questions
*  **GET /questions**: Retrieves all questions with pagination and filtering by question text.
   * Parameters: ```page```, ```size```, ```questions``` (optional).
*  **GET /questions/{id}**: Retrieves a question by ID.
*  **POST /questions**: Creates a new question.
*  **PUT /questions/{id}**: Updates an existing question.
*  **PATCH /questions/{id}**: Partially updates an existing question.
*  **DELETE /questions/{id}**: Deletes a question.
*  **GET /questions/extended/{id}**: Retrieves a question by ID with extended information.
##   Reactions
*   **GET /reactions**: Retrieves all reactions.
*   **POST /reactions**: Creates a new reaction.
*   **PUT /reactions/{id}**: Updates an existing reaction.
*   **PATCH /reactions/{id}**: Partially updates an existing reaction.
*   **DELETE /reactions/{id}**: Deletes a reaction.
#    Testing
To run integration tests using Testcontainers, execute the following command:
```
mvn test
```
The integration tests deploy a PostgreSQL container using Testcontainers and verify database interactions.
# Database Migrations
Liquibase is used for managing the database schema. Migration files are located in the ```src/main/resources/db/changelog.``` directory. Migrations are applied automatically upon application startup.
# Configuration
Application configuration is found in the ```src/main/resources/application.yml``` file. In the current version of the project, there's no profile-based configuration (e.g., application-local.yml).
# Monitoring
Spring Boot Actuator is used for monitoring the application. Access Actuator endpoints at ```http://localhost:8080/actuator``` (the port may vary based on your configuration).

Integration with Graphite is not implemented in the current version.

To view Prometheus metrics, navigate to ```http://localhost:8080/actuator/prometheus```.


# Docker
## Dockerfile
A Dockerfile is present in the root directory of the project and is used to build the Docker image of the application.

## Docker Compose
The docker-compose.yml file in the root directory is used to run the application along with dependencies such as PostgreSQL and Prometheus. Example docker-compose.yml:
```
volumes:
  pg_topics:
  prometheus_data:

services:
  topics_db:
    image: postgres
    container_name: topics_db
    restart: always
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=1234
      - POSTGRES_DB=topics
    volumes:
      - pg_topics:/var/lib/postgresql/data
    ports:
      - "5432:5432"
  prometheus:
    image: prom/prometheus
    container_name: prometheus
    restart: always
    volumes:
      - prometheus_data:/prometheus
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"
  app:
    restart: always
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - topics_db
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://topics_db:5432/topics
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=1234
```
Replace ```your-app-name```, ```your_db_user```, ```your_db_password``` and ```your_db_name``` with appropriate values. Create a ```prometheus.yml``` file with basic configuration for collecting metrics.
# Running the Application via Docker Compose
1. Build Docker Compose images (if not already done): ```docker compose build```
2. Start the application via Docker Compose: ```docker compose up -d```
