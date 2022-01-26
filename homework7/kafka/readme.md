# Порядок установки
1. Создать namespace
```Shell
kubectl apply -f namespace.yaml
```

2. Создать persistent volume
```Shell
kubectl apply -f pv.yaml
```
   
3. Добавить репозиторий Helm
```Shell
helm repo add incubator https://charts.helm.sh/incubator
```

4. Установить хельм чарт
```Shell
helm install kafka --namespace kafka incubator/kafka -f values.yaml --debug
```

5. Проверка
- Тестовый клиент для проверки:
```Shell
kubectl apply -f testclient.yaml
```
- Создание топика:
```Shell
kubectl -n kafka exec -ti testclient -- ./bin/kafka-topics.sh --zookeeper kafka-zookeeper:2181 --topic messages --create --partitions 1 --replication-factor 1
```

- Проверка топика:
```Shell
kubectl -n kafka exec -ti testclient -- ./bin/kafka-topics.sh --zookeeper kafka-zookeeper:2181 --list
```

- Создание консьюмера (в отдельной консоли):
```Shell
kubectl -n kafka exec -ti testclient -- ./bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic messages --from-beginning
```

- Создание продюсера:
```Shell
kubectl -n kafka exec -ti testclient -- ./bin/kafka-console-producer.sh --broker-list kafka:9092 --topic messages
```

Сообщения из окна продюсера отображаются в окне консьюмера

# Порядок удаления
1. Удалить неймспейс
```Shell
kubectl delete -f namespace.yaml
```

2. Удалить persistent volume
```Shell
kubectl delete -f pv.yaml
```
