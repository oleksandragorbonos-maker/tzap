# Спринт 4 — Автоматизація: JUnit 5 + Selenium

Команда: С · Продукт: №1 «ШопЛайн» · Спринт: 4 «Автоматизація: JUnit 5 + Selenium» (02/05.11–12.11.2026) · Епік: [SCRUM-127](https://breakyshort.atlassian.net/browse/SCRUM-127) · Дата зведення: 12.11.2026

## Ролі

| Роль | Виконавиця | BL-вимоги |
|---|---|---|
| КТ (керівник тестування) | Дрєпіна Анастасія | BL-20, BL-60 |
| ТА (тест-аналітик) | Горбонос Олександра | BL-50, BL-70 |
| ІТ (інженер із тестування) | Коротка Ксенія | BL-10, BL-40 |
| СМ (скрам-майстер, рецензент) | Тимчак Анастасія | BL-80 |

BL-30 вилучена в SRS 1.1 (дубль BL-20). Розподіл і схема назв тестів — у [task-board.md](task-board.md).

## Що в репозиторії

| Артефакт | Де |
|---|---|
| Maven-проєкт (JUnit 5.10.2, Selenium 4.21.0, Surefire 3.2.5, Java 17) із модулем `ua.kpi.tzyapz.shopline` v1 | [`qa-shopline/`](../../qa-shopline) |
| Юніт-тести (7 класів) і UI-тести (`ui`, 4 класи) | `qa-shopline/src/test/java/ua/kpi/tzyapz/shopline/` |
| Протокол запуску Surefire (XML і TXT по кожному класу, лог UI-прогону, скріншот) | [`surefire-reports/`](surefire-reports) |
| Простежуваність BL → тест-метод → дефект, аркуш Review (рецензії за чек-листом 5.4), питання | [`coverage.xlsx`](coverage.xlsx) |
| Класи еквівалентності та граничні значення | [`equivalence-boundary.md`](equivalence-boundary.md) |
| Розподіл задач, години, Definition of Done | [`task-board.md`](task-board.md) |
| Протокол ретроспективи | [`retro.md`](retro.md) |
| Звіти про дефекти модуля | GitHub Issues з міткою `module` ([#72](https://github.com/oleksandragorbonos-maker/tzap/issues/72)–[#74](https://github.com/oleksandragorbonos-maker/tzap/issues/74), [#77](https://github.com/oleksandragorbonos-maker/tzap/issues/77), [#80](https://github.com/oleksandragorbonos-maker/tzap/issues/80), [#81](https://github.com/oleksandragorbonos-maker/tzap/issues/81), [#87](https://github.com/oleksandragorbonos-maker/tzap/issues/87)–[#89](https://github.com/oleksandragorbonos-maker/tzap/issues/89)) |

## Як запустити

З каталогу `qa-shopline/` (потрібні JDK 17 і Maven 3.9+):

```
mvn test                                      # юніт-тести (UI виключено: <excludedGroups>ui</excludedGroups>)
mvn test -DexcludedGroups=none                # юніт- і UI-тести (потрібен Chrome)
mvn test -DexcludedGroups=none -Dtest="*UiTest"   # лише UI-тести
```

## Результат прогону (Surefire)

### Юніт-тести модуля v1

`mvn test` → **`Tests run: 140, Failures: 28, Errors: 1, Skipped: 0`**, BUILD FAILURE.

Ця збірка «червона» навмисно: модуль v1 містить закладені дефекти, а тест, що їх фіксує, за методичкою (п. 4.9, чек-лист п. 8, розділ 7 DoD) лишається в наборі червоним до виправлення в v2. Очікувані значення беруться з SRS 1.1; їх не підганяють під код і тести не вимикають. Кожен із 29 червоних запусків має звіт про дефект, номер якого вказано в коментарі тесту.

| Тест-клас | BL | Запусків | Failures | Errors | Дефекти |
|---|---|---|---|---|---|
| `CartTest` | BL-10, BL-40 | 36 | 5 | 0 | #72 (qty 100 приймається), #73 (ціна 0.004 → 0.00), #74 (порожнє `name`) |
| `CartRepeatedAddTest` | BL-20 | 10 | 9 | 0 | #77 (повторне додавання замінює кількість) |
| `ShippingFeeTest` | BL-60 | 11 | 0 | 0 | — |
| `VolumeDiscountTest` | BL-50 | 35 | 6 | 0 | #87 (поріг 5000: `>` замість `≥`), #88 (округлення DOWN замість HALF_UP) |
| `PromoCodeValidationTest` | BL-70 | 35 | 7 | 0 | #80, #81, #89 (ліміт FIXED без знижки BL-50) |
| `PromoCodeValidatorTest` | BL-80 | 11 | 1 | 1 | #80 (Error: день закінчення відхиляється), #81 (Failure: використаний одноразовий код) |
| `ExampleTest` | — | 2 | 0 | 0 | — |
| **Разом** | | **140** | **28** | **1** | **9 дефектів** |

Норматив для команди з 4 осіб: юніт-тестів ≥16 (є 46 тест-методів, 140 запусків), дефектів ≥4 (є 9).

### UI-тести (Selenium WebDriver, `@Tag("ui")`)

`mvn test -DexcludedGroups=none -Dtest="*UiTest"` → **`Tests run: 6, Failures: 0, Errors: 0, Skipped: 0`**, BUILD SUCCESS (норматив ≥6). Протокол: [`surefire-reports/ui-test-run.txt`](surefire-reports/ui-test-run.txt), скріншот [`ui-tests-6-run.png`](surefire-reports/ui-tests-6-run.png).

| Клас | Тест-кейс | Запусків |
|---|---|---|
| `SearchUiTest` | TC-001, TC-022 | 2 |
| `LoginUiTest` | TC-004 | 1 |
| `CartUiTest` | TC-028, TC-014 | 2 |
| `ContactUiTest` | TC-009 | 1 |

## Відкриті питання

Питання Q1, Q2, Q4 і Q5 до замовника (причина відмови — код чи текст; формат символів промокоду; використаний багаторазовий код; поведінка при `null`) — аркуш «Питання» у [coverage.xlsx](coverage.xlsx). Написані тести від відповідей не залежать.

## Підсумок

Модуль v1 не відповідає SRS 1.1 за вимогами BL-10, BL-20, BL-40, BL-50, BL-70 і BL-80; BL-60 (доставка) дефектів не має. Червоні тести буде повторно запущено на v2 у спринті 5.
