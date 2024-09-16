# Explanation of Design Choices

This document outlines the design choices made for the architecture and implementation of this microservices-based system, focusing on scalability, resiliency, and best practices.

## 1. **Microservices Architecture**
The system is broken down into distinct microservices (User Service, Task Service, Event Service), each responsible for a specific domain of the application. This approach offers several benefits:
- **Loose Coupling**: Each microservice operates independently of the others, enabling easier updates and deployments.
- **Scalability**: Services can be scaled independently based on demand. For instance, if task creation spikes, only the Task Service needs to scale, rather than the entire system.
- **Separation of Concerns**: Each service has a distinct responsibility, making the system easier to maintain and evolve.

### Why Microservices?
- **Modularization**: By breaking the application into smaller pieces, we achieve better modularity and easier debugging.
- **Independent Deployment**: Microservices can be deployed without affecting other parts of the system, enabling continuous delivery and development.

## 2. **Database Design and Entity Management**
The system utilizes PostgreSQL as a centralized relational database, but each service interacts only with the tables relevant to its domain, ensuring encapsulation and separation of concerns.

- **Task Service** is responsible for managing tasks, which are tied to the `users` and `task_statuses` tables. It also writes to the `outbox` table for event-driven actions.
- **Event Service** consumes from the `outbox` table, ensuring reliable and decoupled event notifications, thereby facilitating asynchronous communication between services.

### Best Practices:
- **Normalization**: The database schema follows the principles of normalization, reducing redundancy and ensuring data integrity.
- **Event-Driven Outbox Pattern**: This pattern ensures that even if communication between services fails, the event data is preserved in the outbox table for retries.

## 3. **Asynchronous Communication using Kafka**
Kafka is used for handling inter-service communication in an asynchronous, event-driven manner. This is crucial for:
- **Decoupling Services**: Event-driven architectures naturally decouple services, as they do not require synchronous communication.
- **Resiliency**: If one service is down, the events can still be published and processed later once the service is back online.
- **Scalability**: Kafka's distributed nature enables horizontal scaling, allowing services to scale independently as load increases.

Kafka Topics:
- `task-events`: Listens for events related to task creation, updates, and deletions.
- `notifications`: Manages notifications sent out to users based on task events.

### Why Kafka?
- **Fault Tolerance**: Kafka provides built-in fault tolerance through replication of messages across brokers.
- **Scalability**: Kafka’s partitioning mechanism allows it to handle a large volume of messages while maintaining high throughput.

## 4. **Docker-Based Deployment**
The entire system is containerized using Docker. This ensures that the application can run consistently across different environments (development, staging, production) without the "works on my machine" issues.

- **Microservices in Containers**: Each microservice (User, Task, Event) runs in its own Docker container, ensuring isolation and easier management.
- **Database in a Container**: PostgreSQL runs in a Docker container, simplifying database setup and ensuring consistency across environments.
- **Kafka in a Container**: Kafka and Zookeeper run in Docker containers, providing the necessary infrastructure for event-driven communication.

### Benefits:
- **Consistency**: Docker ensures that all dependencies are packaged together, leading to consistent behavior across environments.
- **Portability**: The Docker-based setup allows the application to be deployed easily on any system that supports Docker.

## 5. **Logging and Monitoring**
Each microservice logs its activity, including successful and failed requests, into Docker logs. These logs can be further aggregated using logging tools like ELK Stack (Elasticsearch, Logstash, Kibana) or any other centralized logging solution.

- **Centralized Logging**: By utilizing Docker logs, we capture each service's logs in a centralized manner, providing insight into the overall health of the system.
- **Error Tracking**: Logs include detailed error messages and stack traces, aiding in debugging and performance monitoring.

### Future Enhancements:
- **Monitoring Tools**: Integrating tools like Prometheus and Grafana for real-time monitoring of services.
- **Log Aggregation**: Using a centralized logging system for better observability and quicker debugging.

## 6. **Resiliency Features**
The system incorporates several resiliency features:
- **Retry Mechanism**: For Kafka, a Dead Letter Queue (DLQ) is implemented. If a message fails to be processed, it is retried or moved to the DLQ for future processing.
- **Idempotency**: To prevent duplicate processing of the same message, idempotency checks are used within the services consuming from Kafka.
- **Graceful Error Handling**: Services are designed to handle failures gracefully, without causing cascading failures across the system.

### Resiliency Best Practices:
- **Circuit Breaker Pattern**: This pattern can be implemented in future iterations to avoid overwhelming dependent services during failures.
- **Health Checks**: Implementing health check endpoints for each microservice to ensure that services are functioning correctly.

## 7. **API Gateway (Planned)**
A future enhancement is the addition of an API Gateway, which will act as a single entry point for all client requests. This provides:
- **Load Balancing**: The gateway will distribute incoming requests to the appropriate microservice, balancing the load across instances.
- **Security**: The gateway can be used to authenticate and authorize requests, ensuring that only valid users can access the services.
- **Request Routing**: Based on the request, the gateway will forward traffic to the appropriate microservice.

---

This architecture is built with scalability, maintainability, and resiliency in mind. It provides a solid foundation for future expansion, allowing more microservices to be added as the system grows.
