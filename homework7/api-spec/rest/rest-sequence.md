```mermaid
sequenceDiagram

actor User as Пользователь
participant OrderService as Сервис заказа
participant BillingService as Сервис биллинга
participant NotificationService as Сервис нотификаций

User->>+OrderService: POST /users
    Note right of User: создание пользователя
    
    OrderService->>+BillingService: POST /accounts
        Note right of OrderService: создание аккаунта
    BillingService-->>-OrderService: 201 CREATED {accountId}
OrderService-->>-User: 201 CREATED

User->>+OrderService: POST /order {cost}
    Note right of User: создание заказа
    
    OrderService->>+BillingService: POST /debit {sum}
    BillingService-->>-OrderService: 202 ACCEPTED
    
    OrderService->>+NotificationService: POST /send_email {orderResult}
    NotificationService-->>OrderService: 202 ACCEPTED
OrderService-->>-User: 201 CREATED
    NotificationService->>-NotificationService: sending email
```