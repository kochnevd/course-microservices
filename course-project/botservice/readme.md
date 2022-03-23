Сервис управления ботом.

Взаимодействует с Telegram API.
 * Ссылка на бот: http://t.me/the_cure_bot
 * Управление ботом: https://t.me/botfather
 * Описание Bot API: https://core.telegram.org/bots/api

В создании бота помогла статья: https://habr.com/ru/post/528694/

Мониторинг (при локальном запуске): 
- отдельные метрики: http://localhost:8080/actuator/prometheus?includedNames=Nlp_calls_total%2CNlp_misunderstands_total%2CTips_asks_total%2CTips_not_found_total
- все метрики: http://localhost:8080/actuator/prometheus
