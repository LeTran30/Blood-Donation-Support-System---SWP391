# Blood Donation Support System - Business Flows

This document outlines the main business flows in the Blood Donation Support System to help frontend developers understand the application's functionality and implement the corresponding UI components.

## Table of Contents
1. [User Management](#1-user-management)
2. [Blood Type Management](#2-blood-type-management)
3. [Health Checks](#3-health-checks)
4. [Appointment Management](#4-appointment-management)
5. [Blood Donation Process](#5-blood-donation-process)
6. [Donor Search](#6-donor-search)
7. [Reminder System](#7-reminder-system)
8. [Blood Request Management](#8-blood-request-management)

## 1. User Management

### 1.1 User Registration
- **Endpoint**: `POST /api/v1/auth/register`
- **Description**: Registers a new user in the system
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "password123",
    "fullName": "John Doe",
    "phoneNumber": "0123456789"
  }
  ```
- **Response**:
  ```json
  {
    "status": 200,
    "message": "User registered successfully"
  }
  ```
- **Business Rules**:
    - Email must be unique
    - Password must meet security requirements
    - New users are assigned the MEMBER role by default

### 1.2 User Authentication
- **Endpoint**: `POST /api/v1/auth/login`
- **Description**: Authenticates a user and provides access tokens
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```
- **Response**:
  ```json
  {
    "status": 200,
    "message": "Login successful",
    "data": {
      "user": {
        "id": 1,
        "fullName": "John Doe",
        "email": "user@example.com",
        "phoneNumber": "0123456789",
        "role": "MEMBER"
      },
      "token": "jwt_token_here",
      "refreshToken": "refresh_token_here"
    }
  }
  ```

### 1.3 Get Current User Info
- **Endpoint**: `GET /api/v1/user/me`
- **Description**: Retrieves the current user's information
- **Authentication**: Required (Bearer token)
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Success",
    "data": {
      "id": 1,
      "fullName": "John Doe",
      "email": "user@example.com",
      "phoneNumber": "0123456789",
      "address": "123 Main St",
      "gender": "MALE",
      "dateOfBirth": "1990-01-01",
      "bloodTypeId": 1,
      "bloodTypeName": "A_POS",
      "latitude": 10.123456,
      "longitude": 106.789012
    }
  }
  ```

### 1.4 Update User Profile
- **Endpoint**: `PUT /api/v1/user/{id}`
- **Description**: Updates a user's profile information
- **Authentication**: Required (Bearer token)
- **Authorization**: User can only update their own profile unless they have admin/staff role
- **Request Body**:
  ```json
  {
    "fullName": "John Doe Updated",
    "phoneNumber": "9876543210",
    "address": "456 New St",
    "gender": "MALE",
    "dateOfBirth": "1990-01-01",
    "bloodTypeId": 1,
    "latitude": 10.123456,
    "longitude": 106.789012
  }
  ```
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "User updated successfully",
    "data": {
      "id": 1,
      "fullName": "John Doe Updated",
      "email": "user@example.com",
      "phoneNumber": "9876543210",
      "address": "456 New St",
      "gender": "MALE",
      "dateOfBirth": "1990-01-01",
      "bloodTypeId": 1,
      "bloodTypeName": "A_POS",
      "latitude": 10.123456,
      "longitude": 106.789012
    }
  }
  ```

## 2. Blood Type Management

### 2.1 Get All Blood Types
- **Endpoint**: `GET /api/v1/blood-type`
- **Description**: Retrieves a list of all blood types
- **Authentication**: Not required
- **Query Parameters**:
    - `page` (default: 0): Page number
    - `size` (default: 10): Page size
- **Response**:
  ```json
  {
    "message": "Get list of blood Components successfully",
    "status": "OK",
    "data": {
      "content": [
        {
          "typeName": "A_POS"
        },
        {
          "typeName": "A_NEG"
        },
        {
          "typeName": "B_POS"
        }
      ],
      "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "totalElements": 8,
        "totalPages": 1
      }
    }
  }
  ```

## 3. Health Checks

### 3.1 Create Health Check
- **Endpoint**: `POST /api/v1/health-check/appointment/{appointmentId}`
- **Description**: Creates a health check record for a user based on an appointment
- **Authentication**: Required (Bearer token)
- **Authorization**: Staff or Admin role required
- **Request Body**:
  ```json
  {
    "pulse": 75,
    "bloodPressure": "120/80",
    "resultSummary": "Patient is in good health",
    "isEligible": true,
    "ineligibleReason": null,
    "bloodTypeId": 1
  }
  ```
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Health check created successfully",
    "data": {
      "healthCheckId": 1,
      "pulse": 75,
      "bloodPressure": "120/80",
      "resultSummary": "Patient is in good health",
      "checkedAt": "2023-06-15T10:30:00",
      "isEligible": true,
      "ineligibleReason": null,
      "bloodTypeId": 1,
      "bloodTypeName": "A_POS"
    }
  }
  ```
- **Business Rules**:
    - The appointment must exist and be in SCHEDULED status
    - If bloodTypeId is provided, the user's blood type will be updated
    - Health check records the user's eligibility for donation

### 3.2 Get Health Checks by User ID
- **Endpoint**: `GET /api/v1/health-check/user/{userId}`
- **Description**: Retrieves health check records for a specific user
- **Authentication**: Required (Bearer token)
- **Authorization**: User can only view their own health checks unless they have admin/staff role
- **Query Parameters**:
    - `page` (default: 0): Page number
    - `size` (default: 10): Page size
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Health check records retrieved successfully",
    "data": {
      "content": [
        {
          "healthCheckId": 1,
          "pulse": 75,
          "bloodPressure": "120/80",
          "resultSummary": "Patient is in good health",
          "checkedAt": "2023-06-15T10:30:00",
          "isEligible": true,
          "ineligibleReason": null,
          "bloodTypeId": 1,
          "bloodTypeName": "A_POS"
        }
      ],
      "page": {
        "size": 10,
        "number": 0,
        "totalElements": 1,
        "totalPages": 1
      }
    }
  }
  ```

### 3.3 Get Health Check by Appointment ID
- **Endpoint**: `GET /api/v1/health-check/appointment/{appointmentId}`
- **Description**: Retrieves the health check record for a specific appointment
- **Authentication**: Required (Bearer token)
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Health check fetched successfully",
    "data": {
      "healthCheckId": 1,
      "pulse": 75,
      "bloodPressure": "120/80",
      "resultSummary": "Patient is in good health",
      "checkedAt": "2023-06-15T10:30:00",
      "isEligible": true,
      "ineligibleReason": null,
      "bloodTypeId": 1,
      "bloodTypeName": "A_POS"
    }
  }
  ```

## 4. Appointment Management

### 4.1 Create Appointment
- **Endpoint**: `POST /api/v1/appointment`
- **Description**: Creates a new appointment for blood donation
- **Authentication**: Required (Bearer token)
- **Request Body**:
  ```json
  {
    "userId": 1,
    "appointmentDate": "2023-07-01T10:00:00",
    "notes": "First time donor"
  }
  ```
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Appointment created successfully",
    "data": {
      "appointmentId": 1,
      "userId": 1,
      "userName": "John Doe",
      "appointmentDate": "2023-07-01T10:00:00",
      "status": "SCHEDULED",
      "notes": "First time donor",
      "createdAt": "2023-06-15T14:30:00"
    }
  }
  ```
- **Business Rules**:
    - Users can only create appointments for themselves unless they have admin/staff role
    - Appointment date must be in the future
    - Initial status is set to SCHEDULED

### 4.2 Update Appointment Status
- **Endpoint**: `PUT /api/v1/appointment/{appointmentId}/status`
- **Description**: Updates the status of an appointment
- **Authentication**: Required (Bearer token)
- **Authorization**: Staff or Admin role required
- **Request Body**:
  ```json
  {
    "status": "COMPLETED"
  }
  ```
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Appointment status updated successfully",
    "data": {
      "appointmentId": 1,
      "userId": 1,
      "userName": "John Doe",
      "appointmentDate": "2023-07-01T10:00:00",
      "status": "COMPLETED",
      "notes": "First time donor",
      "createdAt": "2023-06-15T14:30:00"
    }
  }
  ```
- **Business Rules**:
    - Valid status transitions must be followed (e.g., SCHEDULED → COMPLETED or SCHEDULED → CANCELLED)

### 4.3 Get User Appointments
- **Endpoint**: `GET /api/v1/appointment/user/{userId}`
- **Description**: Retrieves appointments for a specific user
- **Authentication**: Required (Bearer token)
- **Authorization**: User can only view their own appointments unless they have admin/staff role
- **Query Parameters**:
    - `page` (default: 0): Page number
    - `size` (default: 10): Page size
    - `status` (optional): Filter by appointment status
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Appointments retrieved successfully",
    "data": {
      "content": [
        {
          "appointmentId": 1,
          "userId": 1,
          "userName": "John Doe",
          "appointmentDate": "2023-07-01T10:00:00",
          "status": "SCHEDULED",
          "notes": "First time donor",
          "createdAt": "2023-06-15T14:30:00"
        }
      ],
      "page": {
        "size": 10,
        "number": 0,
        "totalElements": 1,
        "totalPages": 1
      }
    }
  }
  ```

## 5. Blood Donation Process

### 5.1 Record Blood Donation
- **Endpoint**: `POST /api/v1/blood-donation`
- **Description**: Records a blood donation after a successful health check
- **Authentication**: Required (Bearer token)
- **Authorization**: Staff or Admin role required
- **Request Body**:
  ```json
  {
    "appointmentId": 1,
    "amount": 450,
    "notes": "Successful donation"
  }
  ```
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Blood donation recorded successfully",
    "data": {
      "donationId": 1,
      "userId": 1,
      "userName": "John Doe",
      "appointmentId": 1,
      "donationDate": "2023-07-01T10:30:00",
      "amount": 450,
      "notes": "Successful donation",
      "bloodTypeId": 1,
      "bloodTypeName": "A_POS"
    }
  }
  ```
- **Business Rules**:
    - The appointment must exist and be in COMPLETED status
    - A health check must have been performed and the user must be eligible
    - After recording the donation, a reminder is created for the next eligible donation date (3 months later)

### 5.2 Get User Donation History
- **Endpoint**: `GET /api/v1/blood-donation/user/{userId}`
- **Description**: Retrieves donation history for a specific user
- **Authentication**: Required (Bearer token)
- **Authorization**: User can only view their own donation history unless they have admin/staff role
- **Query Parameters**:
    - `page` (default: 0): Page number
    - `size` (default: 10): Page size
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Donation history retrieved successfully",
    "data": {
      "content": [
        {
          "donationId": 1,
          "userId": 1,
          "userName": "John Doe",
          "appointmentId": 1,
          "donationDate": "2023-07-01T10:30:00",
          "amount": 450,
          "notes": "Successful donation",
          "bloodTypeId": 1,
          "bloodTypeName": "A_POS"
        }
      ],
      "page": {
        "size": 10,
        "number": 0,
        "totalElements": 1,
        "totalPages": 1
      }
    }
  }
  ```

## 6. Donor Search

### 6.1 Find Nearby Donors (Efficient Method)
- **Endpoint**: `GET /api/v1/user/nearby`
- **Description**: Finds donors near a specified location using database-level distance calculation
- **Authentication**: Required (Bearer token)
- **Query Parameters**:
    - `lat`: Latitude of the search location
    - `lon`: Longitude of the search location
    - `radiusKm` (default: 10): Search radius in kilometers
    - `page` (default: 0): Page number
    - `size` (default: 10): Page size
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Nearby donors found",
    "data": {
      "content": {
        "users": [
          {
            "id": 2,
            "fullName": "Jane Smith",
            "email": "jane@example.com",
            "phoneNumber": "9876543210",
            "role": "MEMBER"
          }
        ]
      },
      "page": {
        "size": 10,
        "number": 0,
        "totalElements": 1,
        "totalPages": 1
      }
    }
  }
  ```
- **Business Rules**:
    - Distance is calculated using the Haversine formula at the database level
    - Only returns active users

### 6.2 Search Nearby Donors by Blood Type
- **Endpoint**: `POST /api/v1/distance-search/api/distance-search`
- **Description**: Searches for nearby donors with a specific blood type
- **Authentication**: Required (Bearer token)
- **Request Body**:
  ```json
  {
    "userId": 1,
    "bloodTypeId": 1,
    "latitude": 10.123456,
    "longitude": 106.789012,
    "distanceKM": 10.0
  }
  ```
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Nearby donors found",
    "data": [
      {
        "searchId": 1,
        "userId": 1,
        "targetUserId": 2,
        "targetUsername": "jane@example.com",
        "bloodTypeId": 1,
        "bloodTypeName": "A_POS",
        "distanceKM": 5.2,
        "searchTime": "2023-06-15T15:30:00"
      }
    ]
  }
  ```
- **Business Rules**:
    - Distance is calculated in the application code using the Haversine formula
    - Search history is saved for future reference
    - Only returns users with the specified blood type

### 6.3 Get Search History
- **Endpoint**: `GET /api/v1/distance-search/api/users/{userId}/distance-search`
- **Description**: Retrieves search history for a specific user
- **Authentication**: Required (Bearer token)
- **Authorization**: User can only view their own search history unless they have admin/staff role
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Search history retrieved successfully",
    "data": [
      {
        "searchId": 1,
        "userId": 1,
        "targetUserId": 2,
        "targetUsername": "jane@example.com",
        "bloodTypeId": 1,
        "bloodTypeName": "A_POS",
        "distanceKM": 5.2,
        "searchTime": "2023-06-15T15:30:00"
      }
    ]
  }
  ```

## 7. Reminder System

### 7.1 Create Reminder
- **Endpoint**: `POST /api/v1/reminder`
- **Description**: Creates a reminder for a user
- **Authentication**: Required (Bearer token)
- **Authorization**: Staff or Admin role required, or user creating their own reminder
- **Request Body**:
  ```json
  {
    "userId": 1,
    "nextDate": "2023-10-01",
    "reminderType": "BLOOD_DONATION",
    "message": "You're eligible to donate blood again",
    "sent": false
  }
  ```
- **Response**:
  ```json
  {
    "status": "CREATED",
    "message": "Reminder created successfully",
    "data": {
      "reminderId": 1,
      "userId": 1,
      "nextDate": "2023-10-01",
      "reminderType": "BLOOD_DONATION",
      "message": "You're eligible to donate blood again",
      "sent": false
    }
  }
  ```
- **Business Rules**:
    - Reminders are automatically created after blood donations (3 months later)
    - Reminders can also be manually created by staff/admin or users themselves

### 7.2 Get User Reminders
- **Endpoint**: `GET /api/v1/reminder/user/{userId}`
- **Description**: Retrieves reminders for a specific user
- **Authentication**: Required (Bearer token)
- **Authorization**: User can only view their own reminders unless they have admin/staff role
- **Query Parameters**:
    - `page` (default: 0): Page number
    - `size` (default: 10): Page size
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Reminders retrieved successfully",
    "data": [
      {
        "reminderId": 1,
        "userId": 1,
        "nextDate": "2023-10-01",
        "reminderType": "BLOOD_DONATION",
        "message": "You're eligible to donate blood again",
        "sent": false
      }
    ]
  }
  ```

### 7.3 Get Reminders with Filters
- **Endpoint**: `GET /api/v1/reminder/filter`
- **Description**: Retrieves reminders based on various filters
- **Authentication**: Required (Bearer token)
- **Authorization**: Staff or Admin role required
- **Query Parameters**:
    - `userId` (optional): Filter by user ID
    - `sent` (optional): Filter by sent status (true/false)
    - `fromDate` (optional): Filter by date range (start)
    - `toDate` (optional): Filter by date range (end)
    - `reminderType` (optional): Filter by reminder type
    - `page` (default: 0): Page number
    - `size` (default: 10): Page size
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Reminders retrieved successfully",
    "data": {
      "content": [
        {
          "reminderId": 1,
          "userId": 1,
          "nextDate": "2023-10-01",
          "reminderType": "BLOOD_DONATION",
          "message": "You're eligible to donate blood again",
          "sent": false
        }
      ],
      "page": {
        "size": 10,
        "number": 0,
        "totalElements": 1,
        "totalPages": 1
      }
    }
  }
  ```

## 8. Blood Request Management

### 8.1 Create Blood Request
- **Endpoint**: `POST /api/v1/blood-request`
- **Description**: Creates a request for blood donation
- **Authentication**: Required (Bearer token)
- **Authorization**: Staff or Admin role required
- **Request Body**:
  ```json
  {
    "patientName": "Alice Johnson",
    "hospitalName": "City Hospital",
    "bloodTypeId": 1,
    "urgencyLevel": "HIGH",
    "requiredAmount": 450,
    "contactPhone": "0123456789",
    "notes": "Emergency surgery scheduled"
  }
  ```
- **Response**:
  ```json
  {
    "status": "CREATED",
    "message": "Blood request created successfully",
    "data": {
      "requestId": 1,
      "patientName": "Alice Johnson",
      "hospitalName": "City Hospital",
      "bloodTypeId": 1,
      "bloodTypeName": "A_POS",
      "urgencyLevel": "HIGH",
      "requiredAmount": 450,
      "contactPhone": "0123456789",
      "notes": "Emergency surgery scheduled",
      "status": "PENDING",
      "createdAt": "2023-06-15T16:30:00"
    }
  }
  ```
- **Business Rules**:
    - Only staff and admin can create blood requests
    - Initial status is set to PENDING

### 8.2 Update Blood Request Status
- **Endpoint**: `PUT /api/v1/blood-request/{requestId}/status`
- **Description**: Updates the status of a blood request
- **Authentication**: Required (Bearer token)
- **Authorization**: Staff or Admin role required
- **Request Body**:
  ```json
  {
    "status": "FULFILLED"
  }
  ```
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Blood request status updated successfully",
    "data": {
      "requestId": 1,
      "patientName": "Alice Johnson",
      "hospitalName": "City Hospital",
      "bloodTypeId": 1,
      "bloodTypeName": "A_POS",
      "urgencyLevel": "HIGH",
      "requiredAmount": 450,
      "contactPhone": "0123456789",
      "notes": "Emergency surgery scheduled",
      "status": "FULFILLED",
      "createdAt": "2023-06-15T16:30:00"
    }
  }
  ```

### 8.3 Get Blood Requests
- **Endpoint**: `GET /api/v1/blood-request`
- **Description**: Retrieves blood requests based on various filters
- **Authentication**: Required (Bearer token)
- **Authorization**: Staff or Admin role required
- **Query Parameters**:
    - `status` (optional): Filter by request status
    - `bloodTypeId` (optional): Filter by blood type
    - `urgencyLevel` (optional): Filter by urgency level
    - `page` (default: 0): Page number
    - `size` (default: 10): Page size
- **Response**:
  ```json
  {
    "status": "OK",
    "message": "Blood requests retrieved successfully",
    "data": {
      "content": [
        {
          "requestId": 1,
          "patientName": "Alice Johnson",
          "hospitalName": "City Hospital",
          "bloodTypeId": 1,
          "bloodTypeName": "A_POS",
          "urgencyLevel": "HIGH",
          "requiredAmount": 450,
          "contactPhone": "0123456789",
          "notes": "Emergency surgery scheduled",
          "status": "PENDING",
          "createdAt": "2023-06-15T16:30:00"
        }
      ],
      "page": {
        "size": 10,
        "number": 0,
        "totalElements": 1,
        "totalPages": 1
      }
    }
  }
  ```

## Authentication and Authorization

### Authentication
- All protected endpoints require a valid JWT token in the Authorization header
- Format: `Authorization: Bearer <token>`
- Tokens are obtained through the login endpoint
- Tokens expire after 7 days by default

### Authorization
- The system has three roles: MEMBER, STAFF, and ADMIN
- Each role has specific permissions:
    - MEMBER: Can manage their own profile, appointments, and view their own data
    - STAFF: Can manage users, appointments, health checks, and blood donations
    - ADMIN: Has full access to all system features

### Error Handling
- All API responses follow a consistent format:
  ```json
  {
    "status": "HTTP_STATUS_CODE",
    "message": "Error message",
    "data": null
  }
  ```
- Common error codes:
    - 400: Bad Request (invalid input)
    - 401: Unauthorized (missing or invalid token)
    - 403: Forbidden (insufficient permissions)
    - 404: Not Found (resource doesn't exist)
    - 500: Internal Server Error (server-side issue)
