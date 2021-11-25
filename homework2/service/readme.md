#RESTful CRUD - Users service

## Локальные вызовы
- Получить всех пользователей: http://localhost:8080/users
- Получить одного пользователя: http://localhost:8080/users/1

## H2 консоль (при использовании H2 Database): 
http://localhost:8080/h2-console
Параметры окружения:
- spring.datasource.url=jdbc:h2:mem:testdb
- spring.datasource.driverClassName=org.h2.Driver
- spring.datasource.username=sa
- spring.datasource.password=
- spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
- spring.h2.console.enabled=true
