# Electricity Billing System

A comprehensive backend system for managing electricity billing, customer management, meter readings, and tariff calculations.

## Features

- Customer Management
- Meter Management
- Consumption Tracking
- Billing Generation
- Tariff Management
- JWT Authentication
- RESTful API Architecture

## Tech Stack

- Java 17
- Spring Boot
- Spring Security
- JWT Authentication
- MySQL Database
- Maven

## Prerequisites

- Java 17 or higher
- Maven
- MySQL Database

## Getting Started

1. Clone the repository
```bash
git clone <repository-url>
cd ElectricityBilling
```

2. Configure the database
- Create a MySQL database
- Update `application.properties` with your database credentials

3. Build the project
```bash
mvn clean install
```

4. Run the application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

### Authentication APIs

#### Register Customer
```http
POST /api/customers/register
Content-Type: application/json

{
    "email": "customer@example.com",
    "password": "password123",
    "fullName": "John Doe",
    "phone": "1234567890"
}
```

#### Login
```http
POST /api/customers/login
Content-Type: application/json

{
    "email": "customer@example.com",
    "password": "password123"
}
```

### Customer APIs
```http
GET /api/customers
GET /api/customers/{id}
```

### Billing APIs
```http
GET /api/bills
GET /api/bills/{billId}
GET /api/bills/customer/{customerId}
GET /api/bills/customer/{customerId}/date-range?start=2024-01-01&end=2024-12-31
POST /api/bills
PUT /api/bills/{billId}/status
DELETE /api/bills/{billId}
```

### Meter APIs
```http
GET /api/meters
GET /api/meters/{meterId}
GET /api/meters/customer/{customerId}
POST /api/meters
DELETE /api/meters/{meterId}
```

### Consumption APIs
```http
GET /api/consumptions
GET /api/consumptions/{id}
GET /api/consumptions/meter/{meterId}
GET /api/consumptions/meter/{meterId}/year/{year}
POST /api/consumptions
DELETE /api/consumptions/{id}
```

### Tariff APIs
```http
GET /api/tariffs
GET /api/tariffs/{id}
GET /api/tariffs/current?date=2024-03-20
POST /api/tariffs
DELETE /api/tariffs/{id}
```

## Security

- JWT-based authentication
- Password encryption using BCrypt
- CORS enabled
- Protected endpoints require valid JWT token

### Authentication Header
```http
Authorization: Bearer <your-jwt-token>
```

## Error Handling

The API uses standard HTTP status codes:
- 200: Success
- 201: Created
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details

