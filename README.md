# Тестовое задание 3 на курс по Backend в Clevertec

## Инструкция по запуску

1. **Создайте базу данных PostgreSQL и выполните SQL скрипты для создания
   таблиц:**

    ```sql
    CREATE TABLE public.product (
        id BIGSERIAL PRIMARY KEY,
        description VARCHAR(50) NOT NULL,
        price DECIMAL(10, 2) NOT NULL,
        quantity_in_stock INTEGER NOT NULL,
        wholesale_product BOOLEAN NOT NULL
    );

    CREATE TABLE public.discount_card (
        id BIGSERIAL PRIMARY KEY,
        number INTEGER NOT NULL,
        amount SMALLINT CHECK (amount BETWEEN 0 AND 100) NOT NULL
    );
    ```

2. **Добавьте данные в таблицы:**

    ```sql
   INSERT INTO public.product (description, price, quantity_in_stock,
                            wholesale_product)
   VALUES ('Milk', 1.07, 10, TRUE),
   ('Cream 400g', 2.71, 20, TRUE),
   ('Yogurt 400g', 2.10, 7, TRUE),
   ('Packed potatoes 1kg', 1.47, 30, FALSE),
   ('Packed cabbage 1kg', 1.19, 15, FALSE),
   ('Packed tomatoes 350g', 1.60, 50, FALSE),
   ('Packed apples 1kg', 2.78, 18, FALSE),
   ('Packed oranges 1kg', 3.20, 12, FALSE),
   ('Packed bananas 1kg', 1.10, 25, TRUE),
   ('Packed beef fillet 1kg', 12.80, 7, FALSE),
   ('Packed pork fillet 1kg', 8.52, 14, FALSE),
   ('Packed chicken breasts 1kg', 10.75, 18, FALSE),
   ('Baguette 360g', 1.30, 10, TRUE),
   ('Drinking water 1.5l', 0.80, 100, FALSE),
   ('Olive oil 500ml', 5.30, 16, FALSE),
   ('Sunflower oil 1l', 1.20, 12, FALSE),
   ('Chocolate Ritter sport 100g', 1.10, 50, TRUE),
   ('Paulaner 0.5l', 1.10, 100, FALSE),
   ('Whiskey Jim Beam 1l', 13.99, 30, FALSE),
   ('Whiskey Jack Daniels 1l', 17.19, 20, FALSE);

   INSERT INTO public.discount_card (number, amount)
   VALUES (1111, 3),
   (2222, 3),
   (3333, 4),
   (4444, 5),
   (5555, 2);
    ```

3. **Соберите проект:**

    ```bash
    ./gradlew build
    ```

4. **Перейдите в директорию с JAR файлом:**

    ```bash
    cd build/libs
    ```

5. **Запустите приложение:**

    ```bash
    java -jar clevertec-check.jar 3-1 2-5 5-1 discountCard=1111 balanceDebitCard=100 saveToFile=./result.csv datasource.url=jdbc:postgresql://localhost:5432/check datasource.username=postgres datasource.password=root
    ```

## Тестирование

Для запуска тестов используйте команду:

```bash
./gradlew test
```

Тесты находятся в папке `src/test/java` и включают в себя юнит-тесты для всех
основных компонентов приложения.