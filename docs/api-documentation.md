# Ecommerce API Documentation

### Base URL
http://localhost:8080/api

### Authentication
The API uses JWT (JSON Web Token) for authentication.
- Register at `/api/auth/register`
- Login at `/api/auth/login`
- Include token in header: `Authorization: Bearer <token>`

---

## API Endpoints

### Authentication
#### Register User
```http request
POST /api/auth/register
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password123"
} 
```
Response:
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "user@example.com",
    "role": "USER",
    "firstName": "New",
    "lastName": "User"
}
```
#### Login User
```http request
POST /api/auth/login
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password123"
}
```
Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYW1iYXBhcmFzdTIwMDRAZ21haWwuY29tIiwiaWF0IjoxNzY3NzYwODk2LCJleHAiOjE3Njc4NDcyOTZ9.sZK57cWeg-04tDtrs6ob1i7iyvSX_6RSQXafvsXmfEo",
  "email": "user@example.com",
  "role": "USER",
  "fistName": "firstName",
  "lastName": "lastName"
}
```
### Products
#### Get All Products
```http 
GET /api/products
Query Parameters:
  - page: Page number (default: 0)
  - size: Page size (default: 20)
  - sort: Sort field (default: createdAt,desc)
```
#### Get Product by ID
```http request
GET /api/products/{id}
```
#### Create Product(Admin only)
```http request
POST /api/products
Authorization: Bearer <admin_token>
Content-Type: application/json

{
    "name": "Product Name",
    "description": "Product description",
    "price": 999.99,
    "quantity": 100,
    "categoryId": 1,
    "brand": "Brand Name",
    "imageUrl": "https://example.com/image.jpg"
}
```
#### Update Product(Admin only)
```http request
PUT /api/products/{id}
Authorization: Bearer <admin_token>
```
#### Delete Product(Admin only)
```http request
DELETE /api/products/{id}
Authorization: Bearer <admin_token>
```
#### Search Products
```http request
GET /api/products/search?keyword={keyword}
```
### Categories
#### Get All Categories
```http request
GET /api/categories
```
#### Get Category By ID
```
GET /api/categories/{id}
```
#### Create Category(Admin only)
```http request
POST /api/categories
Authorization: Bearer <admin_token>
```
### User 
#### Get User Profile
```http request
GET /api/users/profile
Authorization: Bearer <user_token>
```
#### Update User Profile
```http request
PUT /api/users/profile
Authorization: Bearer <user_token>
Content-Type: application/json

{
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+911234567890",
    "address": "123 Main St",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pincode": "400001"
}
```
### Cart
#### Add to Cart
```http request
POST /api/cart/add
Authorization: Bearer <user_token>
Content-Type: application/json

{
    "productId": 1,
    "quantity": 2
}
```
#### Get Cart
```http request
GET /api/cart
Authorization: Bearer <user_token>
```
#### Update Cart Item
```http request
PUT /api/cart/items/{itemId}?quantity=3
Authorization: Bearer <user_token>
```
#### Remove Cart Item
```http request
DELETE /api/cart/items/{itemId}
Authorization: Bearer <user_token>
```
#### Clear Cart
```http request
DELETE /api/cart/clear
Authorization: Bearer <user_token>
```
### Orders
#### Create Order
```http request
POST /api/orders
Authorization: Bearer <user_token>
Content-Type: application/json

{
    "shippingAddress": "123 Main St, Mumbai",
    "paymentMethod": "COD"
}
```
#### Get Order by ID
```http request
GET /api/orders/{id}
Authorization: Bearer <user_token>
```
#### Get User Orders
```http request
GET /api/orders?page=0&size=10
Authorization: Bearer <user_token>
```
#### Update Order Status (Admin only)
```http request
PUT /api/orders/{id}/status?status=SHIPPED
Authorization: Bearer <admin_token>
```


## Error Codes
```
400	Bad Request - Invalid input
401	Unauthorized - Invalid or missing token
403	Forbidden - Insufficient permissions
404	Not Found - Resource not found
409	Conflict - Resource already exists
500	- Internal Server Error
```

## Data Models
### Product
```json
{
    "id": 1,
    "name": "iPhone 14 Pro",
    "description": "Latest smartphone",
    "price": 129999.00,
    "discountedPrice": 124999.00,
    "quantity": 50,
    "brand": "Apple",
    "imageUrl": "https://example.com/iphone.jpg",
    "category": {
        "id": 1,
        "name": "Electronics"
    },
    "rating": 4.5,
    "createdAt": "2024-01-01T10:30:00"
}
```

### Order
```json
{
    "id": 1,
    "orderNumber": "ORD-123456789",
    "totalAmount": 249998.00,
    "shippingCharge": 50.00,
    "tax": 45000.00,
    "finalAmount": 295048.00,
    "shippingAddress": "123 Main St, Mumbai",
    "status": "PENDING",
    "paymentMethod": "COD",
    "paymentStatus": "PENDING",
    "createdAt": "2024-01-01T10:30:00",
    "items": [
        {
            "product": {
                "id": 1,
                "name": "iPhone 14 Pro",
                "price": 124999.00
            },
            "quantity": 2,
            "unitPrice": 124999.00,
            "totalPrice": 249998.00
        }
    ]
}
```