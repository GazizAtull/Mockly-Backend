# Mock Interview Platform Backend (H3 Integration)

## Project Overview

This project is a backend system for a **mock interview platform** with geospatial analytics based on **H3**.  

### Main idea of H3 usage
We do not store coordinates only for display.  
We convert session coordinates into **H3 index** and use it for analytics aggregation:

- identify zones with frequent call quality issues
- detect where **voice-only fallback** is recommended
- support engineering decisions based on real zone-level quality data

---

## System Services (3 services)

### 1) `user-service`
Responsible for:
- user profile management
- RBAC-protected endpoints
- publishing audit events (e.g., profile updated)

### 2) `h3-integration-service`
Responsible for:
- receiving session quality metrics (latency, packet loss, disconnects)
- converting `latitude/longitude` → `h3_index`
- calculating quality score
- aggregating analytics by H3 zones
- recommending fallback mode for problematic zones
- publishing audit events

### 3) `audit-service` (event consumer)
Responsible for:
- consuming audit events from RabbitMQ
- storing/processing audit logs (e.g., user actions, H3 analytics recorded events)

> If your actual service names differ, rename them in this README to match your repo folders.

---

## Group Member Roles

- **230103133 Atullayev Gaziz**
    - Backend Developer

- **230103006 Yermekbay Danial**
    - DevOps Developer
- **230103229 Akhmetzhan Nursultan** 
  - Android Developer
- **230103295 Nurkassym Baglan**

---

## How H3 is Used in the System

### Why H3
H3 is used as a **hexagonal geospatial indexing system** to group session metrics by zone.  
Instead of analyzing individual coordinates only, we aggregate quality metrics per H3 cell.

### H3 usage in this project
In `h3-integration-service`:

1. Client sends session quality metrics:
    - `sessionId`
    - `latitude`
    - `longitude`
    - `avgLatencyMs`
    - `packetLossPercent`
    - `disconnectCount`

2. Backend converts coordinates to H3 index:
    - `lat/lng -> h3_index` using Uber H3 library
    - resolution: **7**

3. Backend calculates `qualityScore` using:
    - latency
    - packet loss
    - disconnects

4. Backend marks zone/session as:
    - `fallbackRecommended = true` if quality score is below threshold

5. Backend aggregates records by `h3_index` and returns:
    - active sessions count
    - average quality
    - average latency
    - average packet loss
    - total disconnects
    - fallback recommendation for the zone


## How to Run System

## Prerequisites

Make sure the following are installed and running:

- **Java 21**
- **Maven 3.9+**
- **PostgreSQL**
- **RabbitMQ**
- **Keycloak** (for secured endpoints in user-service)
-  Docker / Docker Compose for local infrastructure

---

## Infrastructure Setup (Local)

### 1) Start infrastructure (Docker Compose)

```bash
docker compose up
```

## Run Services (Recommended order)

### 1. Start `audit-service`
This service should be started first so it can consume audit events from RabbitMQ.

```bash
cd audit-service
mvn spring-boot:run
```
### 2. Start `user-service`
```bash
cd user-service
mvn spring-boot:run
```
### 3. Start `h3-integration`
```bash
cd h3-integration
mvn spring-boot:run
```
