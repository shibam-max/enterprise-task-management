# Deployment Guide

## Local Development

### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- PostgreSQL 15+

### Quick Start
```bash
# Clone repository
git clone https://github.com/shibam-max/enterprise-task-management.git
cd enterprise-task-management

# Start infrastructure
cd docker
docker-compose up -d postgres redis kafka

# Run backend
cd ../backend
./mvnw spring-boot:run

# Run frontend
cd ../frontend
npm install
npm start
```

## Docker Deployment

### Development
```bash
docker-compose up --build
```

### Production
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Kubernetes Deployment

### Prerequisites
- Kubernetes cluster
- kubectl configured
- Docker images built and pushed

### Deploy
```bash
# Apply all configurations
kubectl apply -f k8s/

# Check deployment status
kubectl get pods -n task-management

# Access application
kubectl port-forward svc/frontend-service 3000:3000 -n task-management
```

### Scaling
```bash
# Scale backend
kubectl scale deployment task-management-backend --replicas=5 -n task-management

# Scale frontend
kubectl scale deployment task-management-frontend --replicas=3 -n task-management
```

## Monitoring

### Prometheus
- URL: http://localhost:9090
- Metrics: Application, JVM, Database

### Grafana
- URL: http://localhost:3001
- Credentials: admin/admin
- Dashboards: Pre-configured for application monitoring

## Health Checks

### Backend Health
```bash
curl http://localhost:8080/actuator/health
```

### Frontend Health
```bash
curl http://localhost:3000/health
```

## Environment Variables

### Backend
- `SPRING_PROFILES_ACTIVE`: Application profile
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_REDIS_HOST`: Redis host
- `SPRING_KAFKA_BOOTSTRAP_SERVERS`: Kafka servers

### Frontend
- `REACT_APP_API_URL`: Backend API URL
- `REACT_APP_ENVIRONMENT`: Environment name