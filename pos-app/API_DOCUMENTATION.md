# POS Application API Documentation

## Overview
This document provides comprehensive documentation for all REST API endpoints in the POS application, including authentication, authorization, and usage examples.

## Base URL
```
http://localhost:8080
```

## Authentication
The application uses session-based authentication with JSESSIONID cookies.

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "adminpass"
}
```

**Response:**
```json
{
  "email": "admin@example.com",
  "role": "supervisor"
}
```

### Logout
```http
POST /api/auth/logout
```

## User Roles
- **supervisor**: Full access to all features including uploads and reports
- **operator**: Limited access (no uploads, no reports)

## API Endpoints

### 1. Authentication (`/api/auth`)

| Method | Endpoint | Description | Auth Required | Roles |
|--------|----------|-------------|---------------|-------|
| POST | `/api/auth/login` | User login | No | All |
| POST | `/api/auth/logout` | User logout | Yes | All |

### 2. Product Management (`/api/product`)

| Method | Endpoint | Description | Auth Required | Roles |
|--------|----------|-------------|---------------|-------|
| POST | `/api/product` | Add new product | Yes | operator, supervisor |
| GET | `/api/product` | Get all products | Yes | operator, supervisor |
| GET | `/api/product/search` | Search products by barcode | Yes | operator, supervisor |
| GET | `/api/product/barcode/{barcode}` | Get product by barcode | Yes | operator, supervisor |
| POST | `/api/product/upload-tsv` | Upload product master (TSV) | Yes | supervisor only |
| PUT | `/api/product/{id}` | Update product | Yes | operator, supervisor |

**Example - Add Product:**
```http
POST /api/product
Content-Type: application/json

{
  "barcode": "123456789",
  "clientName": "Client A",
  "name": "Product Name",
  "mrp": 100.50
}
```

### 3. Inventory Management (`/api/inventory`)

| Method | Endpoint | Description | Auth Required | Roles |
|--------|----------|-------------|---------------|-------|
| GET | `/api/inventory` | Get all inventory | Yes | operator, supervisor |
| GET | `/api/inventory/search` | Search inventory by barcode | Yes | operator, supervisor |
| POST | `/api/inventory/upload` | Upload inventory (TSV) | Yes | supervisor only |
| POST | `/api/inventory` | Add inventory item | Yes | operator, supervisor |
| PUT | `/api/inventory/{barcode}` | Update inventory | Yes | operator, supervisor |

**Example - Add Inventory:**
```http
POST /api/inventory
Content-Type: application/json

{
  "barcode": "123456789",
  "quantity": 50
}
```

### 4. Order Management (`/api/order`)

| Method | Endpoint | Description | Auth Required | Roles |
|--------|----------|-------------|---------------|-------|
| GET | `/api/order` | Get all orders | Yes | operator, supervisor |
| GET | `/api/order/{id}` | Get order items by order ID | Yes | operator, supervisor |
| POST | `/api/order` | Place new order | Yes | operator, supervisor |
| GET | `/api/order/search` | Search orders with filters | Yes | operator, supervisor |

**Example - Place Order:**
```http
POST /api/order
Content-Type: application/json

{
  "items": [
    {
      "barcode": "123456789",
      "quantity": 2,
      "sellingPrice": 95.00
    }
  ]
}
```

### 5. Client Management (`/api/client`)

| Method | Endpoint | Description | Auth Required | Roles |
|--------|----------|-------------|---------------|-------|
| POST | `/api/client` | Add new client | Yes | operator, supervisor |
| GET | `/api/client` | Get all clients | Yes | operator, supervisor |
| GET | `/api/client/search` | Search clients by query | Yes | operator, supervisor |
| PUT | `/api/client/{id}` | Update client | Yes | operator, supervisor |

**Example - Add Client:**
```http
POST /api/client
Content-Type: application/json

{
  "name": "Client Name",
  "email": "client@example.com"
}
```

### 6. Invoice Management (`/api/invoice`)

| Method | Endpoint | Description | Auth Required | Roles |
|--------|----------|-------------|---------------|-------|
| GET | `/api/invoice/{orderId}` | Download invoice | Yes | operator, supervisor |
| GET | `/api/invoice/generate/{orderId}` | Generate invoice | Yes | operator, supervisor |

### 7. Day Sales Reports (`/api/reports/day-sales`)

| Method | Endpoint | Description | Auth Required | Roles |
|--------|----------|-------------|---------------|-------|
| GET | `/api/reports/day-sales` | Get sales for date range | Yes | supervisor only |
| POST | `/api/reports/day-sales/generate` | Generate today's report | Yes | supervisor only |

**Example - Get Sales Report:**
```http
GET /api/reports/day-sales?start=2024-01-01&end=2024-01-31
```

### 8. Sales Reports (`/api/report/sales`)

| Method | Endpoint | Description | Auth Required | Roles |
|--------|----------|-------------|---------------|-------|
| POST | `/api/report/sales/search` | Get sales report with filters | Yes | supervisor only |

### 9. System Information (`/api/about`)

| Method | Endpoint | Description | Auth Required | Roles |
|--------|----------|-------------|---------------|-------|
| GET | `/api/about` | Get application info | No | All |

## Error Handling

### 403 Forbidden (Access Denied)
```json
{
  "message": "Access denied: You don't have permission to perform this action"
}
```

### 400 Bad Request (Validation Error)
```json
{
  "message": "Validation failed: field: error message; "
}
```

### 500 Internal Server Error
```json
{
  "message": "An unknown error has occurred - error details"
}
```

## Security Features

### Role-Based Access Control
- **Supervisor**: Full access to all features
- **Operator**: Limited access (no uploads, no reports)

### Protected Endpoints
- Upload endpoints require supervisor role
- Report endpoints require supervisor role
- All other endpoints require authentication

### Session Management
- Uses JSESSIONID cookies for session management
- Sessions are invalidated on logout
- CORS configured for frontend integration

## Testing Examples

### 1. Login as Supervisor
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"adminpass"}' \
  -c cookies.txt
```

### 2. Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/client -b cookies.txt
```

### 3. Test Access Denied
```bash
# Login as operator
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"userpass"}' \
  -c operator_cookies.txt

# Try to access supervisor-only endpoint
curl -X GET http://localhost:8080/api/reports/day-sales -b operator_cookies.txt
```

## Swagger Documentation
Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

## User Accounts

| Email | Password | Role | Permissions |
|-------|----------|------|-------------|
| `admin@example.com` | `adminpass` | supervisor | Full access |
| `user@example.com` | `userpass` | operator | Limited access |

## File Upload Endpoints

### Product Upload (TSV Format)
```http
POST /api/product/upload-tsv
Content-Type: multipart/form-data

file: [TSV file with columns: barcode, clientName, name, mrp, imageUrl]
```

### Inventory Upload (TSV Format)
```http
POST /api/inventory/upload
Content-Type: multipart/form-data

file: [TSV file with columns: barcode, quantity]
```

## Pagination
Most list endpoints support pagination with these parameters:
- `page`: Page number (0-based, default: 0)
- `pageSize`: Items per page (default: 10)

**Response Format:**
```json
{
  "content": [...],
  "currentPage": 0,
  "totalPages": 5,
  "totalItems": 50,
  "pageSize": 10
}
``` 