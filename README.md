# 🏆 Customer Rewards Application
This is a Spring Boot-based REST API application designed to manage customer transactions and calculate customer rewards points earned by the transaction history.

## 📌 Features
- Fetch transactions for a customer between two dates

- Calculate reward points monthly and in total

- Handle validation (e.g., invalid date ranges or customer IDs)

- Return meaningful error messages from backend in case of failures


---

## ⚙️ Technologies Used



### Backend

- Java 8

- Spring Boot

- Spring MVC

- REST API

---


## 🛠️ How to Run



### Prerequisites



- Java 8

- Maven

- Any IDE (e.g., IntelliJ, Eclipse)


 ### Steps

1. Clone the repository:

   ```bash

   git clone https://github.com/Kameshraj05/RewardsApplication.git

  ## Build and Run

    'mvn spring-boot:run'
    
## System Architecture
The application follows a layered architecture, with the following components:
- **Controller Layer:** Handles incoming HTTP requests and routes them to the appropriate service.
- **Service Layer:** Contains the business logic for calculating rewards points and retrieving transaction data.
- **Repository Layer:** Interacts with the database to persist and retrieve data.
- **Database:** Stores customer transaction data.

## Database Design
### Entities
**transaction:**
- id (Primary Key)
-	customer_id
-	transaction_id
-	amount
-	transaction_date

  **customer:**
- id (primary key)
- customer_id
- customer_name

## Table Schema
| Table       | Column          | Data Type             | Constraints             | Description                                                              |
| :---------- | :-------------- | :-------------------- | :---------------------- | :----------------------------------------------------------------------- |
| transaction | id              | VARCHAR(255)          | PRIMARY KEY             | Unique identifier for the transaction.                                   |
|             | customer_id     | VARCHAR(255)          | NOT NULL                | ID of the customer who made the transaction.                             |
|             | transaction_id  | VARCHAR(255)          | NOT NULL                | ID of the transaction used for customer reference                        |
|             | amount          | DECIMAL(10, 2)        | NOT NULL                | Amount of the purchase.                                                  |
|             | transaction_date| TIMESTAMP             | NOT NULL                | Date and time of the transaction.                                        |
| customer    | id              | VARCHAR(255)          | PRIMARY KEY             | Unique identifier for the customer.                                      |
|             | customer_id     | VARCHAR(255)          | NOT NULL                | ID of the customer who made the transaction.                             |
|             | customer_name   | VARCHAR(255)          | NOT NULL                | Name of the customer who made the transaction.                           |

## API Specifications
### Customer Purchase API
This API endpoint is responsible for recording customer purchase details, specifically transaction amounts and dates, into the database.
### API Description
1. Method:POST
2. Endpoint: /transaction
3. Content Type: application/json
### Request Parameters
| Parameter Name    | Data Type       | Occurrence | Description                                                                 |
| :---------------- | :-------------- | :--------- | :-------------------------------------------------------------------------- |
| customerId        | String          | 1..1       | Customer Id is the key for customer identification.                         |
| customerName      | String          | 1..1       | Name of the customer                                                        |
| transactionId     | String          | 1..1       | This parameter is the key for transaction identification.                   |
| amount            | Double          | 1..1       | This value defines the amount spent by the customer on the purchase.        |
| transactionDate   | LocalDateTime   | 1..1       | This parameter denotes the date on which the customer made the purchase.    |
### Request Sample Data
'{ 
"customerId": "CUST001", 
"customerName: "Alice Smith", 
"transactionId": "TXN001", 
"amount": 125.50,
"transactionDate": "2025-04-01T10:00:00"
 }
'
### Response Data
In the response of this API, the client will receive the HTTP status code along with its respective message in the response body as given below.
| Status Code | Status Description    | Message                                                                     |
| :---------- | :-------------------- | :-------------------------------------------------------------------------- |
| 201         | Created               | Transaction recorded successfully                                           |
| 400         | Bad Request           | Invalid transaction data: [Error message]                                   |
| 500         | Internal Server Error | An error occurred while recording transaction.                              |

### Customer Rewards Calculation API
This API allows for retrieving customer transaction history and calculating rewards points based on that history. It is designed to be used by front-end applications and other systems that need to access and display customer rewards data.
### API Description
1. Method:GET
2. Endpoint: /customers/{customerId}?fromDate={fromDate}&toDate={toDate}
3. Response Content Type: application/json
### Request Parameters
| Parameter Name    | Data Type       | Occurrence | Description                                                                 |
| :---------------- | :-------------- | :--------- | :-------------------------------------------------------------------------- |
| customerId        | String          | 1..1       | Customer Id is the key for customer identification.                         |
| fromDate          | LocalDate       | 0..1       | The user input to provide the start date to fetch customer transactions.    |
| toDate            | LocalDate       | 0..1       | The user input to provide the end date to fetch customer transactions.      |
### Request Sample Data
'Sample url: /customers/CUST001?fromDate=2025-02-01&toDate=2025-04-30'
### Response Parameters
| Parameter Name    | Data Type       | Occurrence | Description                                                                 |
| :---------------- | :-------------- | :--------- | :-------------------------------------------------------------------------- |
| customerId        | String          | 1..1       | Customer Id is the key for customer identification.                         |
| customerName      | String          | 1..1       | Name of the customer.                                                       |
| transaction       | List            | 1..1       | List of transactions done by the customer within the given date range.      |
| monthlyPoints     | List            | 1..1       | The rewards points calculated and structured monthly wise.                  |
| totalPoints       | int             | 1..1       | Total reward points earned by the customer from the transactions within the given date range.      |
### Response Sample Data
'{
"customerId":"CUST001",
"customerName":"Alice Smith",
"transaction":[
{
"customerId":"CUST001",
"transactionId":"TXN1002", 
"amount":75.5,
"transactionDate":"2025-02-15"
},
{
"customerId":"CUST001",
"transactionId":"TXN1003",
"amount":120.0,
"transactionDate":"2025-03-01"
},
{
"customerId":"CUST001",
"transactionId":"TXN1016",
"amount":105.5,
"transactionDate":"2025-02-27"
},
{
"customerId":"CUST001",
"transactionId":"TXN1019",
"amount":149.0,
"transactionDate":"2025-03-12"
}
],
"monthlyPoints":[
{
"year":2025,
"month":"FEBRUARY",
"points":86
},
{
"year":2025,
"month":"MARCH",
"points":238
}
],
"totalPoints":324
}
'
| Status Code | Status Description    | Message                                                                                                 |
| :---------- | :-------------------- | :------------------------------------------------------------------------------------------------------ |
| 200         | OK                    | The request was successful.                                                                             |
| 400         | Bad Request           | Validation error: Invalid Customer Id/ Invalid Date range. From Date cannot be after to-date.           |
| 404         | Not Found             | Customer not found: No transaction found for the given inputs                                           |
| 500         | Internal Server Error | Something went wrong. Please try agin later.                                            |
