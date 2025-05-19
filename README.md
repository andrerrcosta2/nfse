# Spring Boot + Kafka + Angular 19 Integration Tests

This project demonstrates integration between a Spring Boot backend and an Angular 19 frontend using Kafka as the communication layer.

## How to Run

> 1. Make sure Docker is installed and running.

- **On Windows:**
```bash
  docker compose up -d --build
```

Depending on your docker version you may need to run:
> (add --no-cache to the build to avoid cached dependencies)

```bash
docker-compose build
docker-compose up
```

- **Linux/macOS**:
```shell
   docker-compose up -d --build
```

> 2. Open your browser and navigate to [http://localhost:4200](http://localhost:4200).

> 3. In order to see the clusters on kafka-manager you must access [http://localhost:9000](http://localhost:9000) and register it manually.
- Use the host: 'zookeeper:2181' without quotes

### Stack and Versions
- Java	17
- Maven	3.8.6
- Spring Boot	3.2.5
- Spring Kafka	3.1.1
- Angular CLI	19.2.11
- Node.js	22.15.1
- npm	11.3.0
- OS	win32 x64
- Docker Desktop 4.40.0 (187762)
- Compose: v2.34.0-desktop.1
- Engine: 28.0.4
