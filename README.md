# POC Observabilidade Distribuída

## Pré-requisitos
- Java 17
- Maven
- Docker Desktop

## Como rodar

### 1. Instalar o pedido-comum
cd pedido-comum && mvn clean install

### 2. Subir o Jaeger
docker compose up -d

### 3. Subir os serviços (cada um em um terminal)
cd estoque-service && mvn spring-boot:run
cd pedido-api && mvn spring-boot:run
cd notificacao-service && mvn spring-boot:run

### 4. Testar
curl -X POST http://localhost:8080/pedidos \
  -H "Content-Type: application/json" \
  -d '{"produtoId": "PROD-001", "quantidade": 2, "clienteNome": "João"}'

### 5. Ver traces
http://localhost:16686