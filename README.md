
# Тестовое задание 2 на курс по Backend в Clevertec

Основные аспекты проекта описаны [здесь](https://github.com/Pavel123451/Clevertec_backend_test/tree/feature/entry-core?tab=readme-ov-file).

## Изменения в проекте

В этом задании внесены следующие изменения в проект:

1. Добавлены параметры `pathToFile` и `saveToFile`:
   - `pathToFile` указывает на путь к файлу с продуктами.
   - `saveToFile` указывает на путь к файлу для сохранения чека.
2. При отсутствии параметров `pathToFile` и/или `saveToFile` выбрасывается `BadRequestException`. Также оно будет выброшено если pathToFile указан, но файла по заданному пути не найдено.
3. Если отсутствует только pathToFile, то ошибка записывается в созданный файл по пути saveToFile.
4. Если отсутствует saveToFile, то ошибка записывается в созданный result.csv в корне проекта.

### Инструкция по запуску

Для компиляции и запуска проекта выполните следующие команды:

1. Компиляция:
   ```sh
   javac -d out -sourcepath src src/main/java/ru/clevertec/check/CheckRunner.java
   ```

2. Запуск:
   ```sh
   java -cp out main.java.ru.clevertec.check.CheckRunner 3-1 2-5 5-1 discountCard=1111 balanceDebitCard=100 pathToFile=./src/main/resources/products.csv saveToFile=./output_check.csv
   ```