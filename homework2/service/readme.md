#RESTful CRUD - Users service

## Локальные вызовы
- Получить всех пользователей: http://localhost:8080/users (через Ingress: http://arch.homework/users)
- Получить одного пользователя: http://localhost:8080/users/1 (через Ingress: http://arch.homework/users/1)

## H2 консоль (при использовании H2 Database): 
http://localhost:8080/h2-console (через Ingress: http://arch.homework/h2-console)
Параметры окружения:
- spring.datasource.url=jdbc:h2:mem:testdb
- spring.datasource.driverClassName=org.h2.Driver
- spring.datasource.username=sa
- spring.datasource.password=
- spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
- spring.h2.console.enabled=true

Переключение профиля (DEV - используем список в памяти (по умолчанию), PROM - ходим в БД)
- spring.profiles.active=DEV

Параметры актуатора.
- Управление эндпойнтами (примеры эндпойнтов: health, info, env, metrics; см. [список эндпойнтов](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints))
  - management.endpoint._NAME_.enabled=true
  - management.endpoints.web.exposure.include=_NAME_,_NAME_
- Readiness probes (доступны на эндпойнте `/actuator/health`)
  - management.endpoint.health.show-details=always  _(always или never)_
  - management.endpoint.health.show-components=always  _(always или never)_

