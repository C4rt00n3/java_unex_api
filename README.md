# java_unex_api

# Dedê Eventos API

API Java para gerenciamento de usuários, organizadores e eventos, implementada com JDBC puro, MySQL e HikariCP.

## Requisitos

- Java 21+
- MySQL 8+
- Gradle

## Banco de dados

Crie o banco e as tabelas com:

```bash
mysql -u user -p < src/main/resources/schema.sql
```

O arquivo `src/main/resources/schema.sql` está sincronizado com `banco_de_dados.sql`, que foi o script passado pelo professor. Atualize `src/main/resources/application.properties` com usuário e senha do MySQL.

## Execução

```bash
./gradlew run
```

Servidor HTTP padrão: `http://localhost:8080`.

## Testes

Execute os testes unitários com:

```bash
./gradlew test
```

## Endpoints

- `POST /users`
- `GET /users/{id}`
- `PUT /users/{id}`
- `PATCH /users/{id}/status`
- `POST /organizers`
- `POST /events`
- `GET /events/{id}`
- `PUT /events/{id}`
- `DELETE /events/{id}`
- `GET /organizers/{organizerId}/events`

## Exemplo de JSON

Criar organizador:

```json
{
  "name": "Maria Organizadora",
  "birthDate": "1995-04-12",
  "gender": "F",
  "email": "maria@eventos.com",
  "password": "123456"
}
```

Criar evento:

```json
{
  "organizerId": 1,
  "title": "Show Dede",
  "description": "Evento principal",
  "eventType": "SHOW",
  "modality": "PRESENCIAL",
  "location": "Salvador",
  "startDate": "2026-06-20T20:00:00",
  "endDate": "2026-06-20T23:00:00",
  "maximumCapacity": 300,
  "ticketPrice": 50.00,
  "refundTicket": true,
  "refundFee": 10.00,
  "active": true
}
```
