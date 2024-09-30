
# VetOptim - Veterinary Management System

## Overview
VetOptim is a comprehensive microservices-based system designed to manage veterinary operations. It covers pet management, appointment scheduling, task and resource management, video consultations, and billing for services provided.

The system uses an event-driven architecture to ensure efficient communication between microservices and leverages modern cloud solutions for scalability and reliability. 

## Table of Contents
1. [Microservices](#microservices)
2. [Tech Stack](#tech-stack)
3. [Services Overview](#services-overview)
4. [Event-Driven Communication](#event-driven-communication)
5. [Setup Instructions](#setup-instructions)
6. [Testing and Monitoring](#testing-and-monitoring)

---

## Microservices

### 1. **Pet Management Service**
- Manages pet profiles, medical history, vaccinations, and treatments.
- Communicates with the Task & Resource Management Service and Appointment Service.

### 2. **Appointment Service**
- Manages appointments scheduling, rescheduling, and cancellations.
- Integrates with the Video Consultation Service for virtual appointments.
- Communicates with Owner Management and Pet Management Services.

### 3. **Task & Resource Management Service**
- Assigns and tracks tasks for veterinarians.
- Manages medical resources (e.g., medicines, surgical rooms).
- Sends notifications for urgent tasks and low resources.
- Communicates with the Appointment, Pet Management, and Billing Services.

### 4. **Video Consultation Service**
- Manages WebRTC-based video consultations.
- Uses AWS S3 for video storage and AWS MediaConvert for transcoding.
- Integrates with the Appointment Service to schedule video consultations.

### 5. **Owner Management Service**
- Manages owner profiles and syncs with the Appointment and Pet Management services.

### 6. **Billing Service** (Planned)
- Handles invoicing, payment processing, and billing based on task/resource usage.
- Communicates with the Task & Resource Management and Appointment Services.

---

## Tech Stack

- **Backend**: Spring Boot (Java)
- **Database**: PostgreSQL (for Pet Management & Task Management), MySQL (for Appointment & Owner Management), MongoDB (for Video Consultation)
- **Message Queue**: RabbitMQ
- **Storage**: AWS S3 (Video Recordings)
- **Video Processing**: AWS MediaConvert
- **Authentication**: Spring Security with OAuth 2.0
- **WebRTC**: For video calls in the Video Consultation Service

---

## Services Overview

Each microservice is deployed and maintained independently, ensuring scalability and fault isolation:

- **Pet Management**: CRUD operations for pets and medical history.
- **Appointment Management**: Scheduling and rescheduling appointments.
- **Task & Resource Management**: Tracks tasks and resource usage for treatments.
- **Video Consultation**: Handles real-time video calls between veterinarians and pet owners, with video storage and transcoding.
- **Owner Management**: CRUD operations for owner data and integration with pet and appointment services.

---

## Event-Driven Communication

RabbitMQ is used for event-driven communication between services:
- **Task Events**: For assigning, updating, or completing tasks.
- **Appointment Events**: For managing and notifying about appointments.
- **Owner Events**: For syncing owner data across services.
- **Video Session Events**: For starting, ending, and managing video consultations.

---

## Setup Instructions

1. **Clone the repository**:
   ```
   git clone https://github.com/your-repo-url
   cd vetoptim
   ```

2. **Set up environment variables**:
   Each microservice requires different configurations, including database connection strings and AWS credentials for the Video Consultation Service.
   
   Set these variables in `.env` files for each service (or use Docker Compose):
   ```
   POSTGRESQL_URL=your-postgresql-url
   MYSQL_URL=your-mysql-url
   AWS_S3_ACCESS_KEY=your-access-key
   AWS_S3_SECRET_KEY=your-secret-key
   ```

3. **Build and Run**:
   Use Maven to build each microservice:
   ```
   mvn clean install
   mvn spring-boot:run
   ```
   Alternatively, use Docker Compose to spin up all the services at once.

4. **Run Tests**:
   Each microservice has its own unit and integration tests.
   Run the tests using:
   ```
   mvn test
   ```

---

## Testing and Monitoring

The system has unit tests implemented for every microservice. Key areas covered include:
- **Pet Management**: Pet creation, updates, and medical history management.
- **Appointment Management**: Scheduling, rescheduling, and notifications.
- **Video Consultation**: WebRTC signaling, session management, video recording, and AWS S3/MediaConvert integration.

---

## Future Plans

1. Implement the **Billing Service** to handle task-based billing and integrate with the Appointment and Task Management Services.
2. Enhance the **Video Consultation Service** to support more advanced WebRTC features, like screen sharing and better session management.
3. Potential addition of an **Analytics/Reporting Service** to analyze veterinary performance, pet health, and appointment trends.

---

## License

This project is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 4.0 International License - see the [LICENSE.md](LICENSEs.md) file for details.

[![CC BY-NC-ND 4.0][cc-by-nc-nd-image]][cc-by-nc-nd]

[cc-by-nc-nd]: http://creativecommons.org/licenses/by-nc-nd/4.0/
[cc-by-nc-nd-image]: https://licensebuttons.net/l/by-nc-nd/4.0/88x31.png
[cc-by-nc-nd-shield]: https://img.shields.io/badge/License-CC%20BY--NC--ND%204.0-lightgrey.svg