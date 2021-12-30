```mermaid
sequenceDiagram

participant Пользователь
participant Сервис заказа
participant Сервис биллинга
participant Сервис нотификаций

Пользователь->>Сервис заказа: POST /users
activate Сервис заказа
    Note right of Пользователь: создание пользователя
    Сервис заказа->>Сервис биллинга: POST /accounts
    activate Сервис биллинга
        Note right of Сервис заказа: создание аккаунта
        Сервис биллинга-->>Сервис заказа: 201 CREATED {accountId}
    deactivate Сервис биллинга
    Сервис заказа-->>Пользователь: 201 CREATED
deactivate Сервис заказа

Пользователь->>Сервис заказа: POST /order {cost}
activate Сервис заказа
    Note right of Пользователь: создание заказа
    Сервис заказа->>Сервис биллинга: POST /debit {sum}
    activate Сервис биллинга
        Сервис биллинга-->>Сервис заказа: 202 ACCEPTED
    deactivate Сервис биллинга
    Сервис заказа->>Сервис нотификаций: POST /send_email
    activate Сервис нотификаций
        Сервис нотификаций-->>Сервис заказа: 202 ACCEPTED
    Сервис заказа-->>Пользователь: 201 CREATED
deactivate Сервис заказа
    Сервис нотификаций->>Сервис нотификаций: sending email
deactivate Сервис нотификаций
```