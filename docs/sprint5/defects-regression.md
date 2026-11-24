# Регресійні дефекти v2 — ШопЛайн

Спринт 5 · задача SCRUM-173 · виявлено прогоном автонабору на модулі v2 24.11.2026 · Коротка К. (ІТ)

Регресія — перехід **Passed → Failed** на рівні запуску (клас + метод + індекс), план регресії, Д1. За порівнянням [`surefire-comparison.md`](surefire-comparison.md) такий перехід рівно один.

| Звіт | Вимога | Тест, що виявив | v1 | v2 | Severity / Priority |
|---|---|---|---|---|---|
| [BUG-#122](https://github.com/oleksandragorbonos-maker/tzap/issues/122) | BL-60 | `ShippingFeeTest.givenSubtotalAfterDiscount_whenShippingFee_returnsFeeByThreshold[4]` | Passed | Failed | Major / P2 |

---

# BUG-#122: регресія v2 — безкоштовна доставка не діє на межі 1500 грн (BL-60)

**Вимога:** BL-60 (SRS 1.1), High
**Джерело:** регресія v2, виявлена автотестом
**Тест, що виявив:** `ShippingFeeTest.givenSubtotalAfterDiscount_whenShippingFee_returnsFeeByThreshold[4]` ([`qa-shopline/src/test/java/ua/kpi/tzyapz/shopline/ShippingFeeTest.java`](../../qa-shopline/src/test/java/ua/kpi/tzyapz/shopline/ShippingFeeTest.java), рядок 31: `"1500.00, 0.00"`)
**Результат того ж тесту на v1:** Passed ([`surefire-v1/`](surefire-v1/), `ShippingFeeTest` — 11 з 11 зелені)
**Severity:** Major · **Priority:** P2
**Issue:** [#122](https://github.com/oleksandragorbonos-maker/tzap/issues/122), мітки `module`, `sprint:5`, `severity:major`, `priority:P2`, `status:new`
**Дата і виконавиця:** 24.11.2026, Коротка К. (ІТ)

## Кроки відтворення

1. Підключити тести спринта 4 до модуля `code/modules/shopline/v2` (зроблено в репозиторії: `code/modules/shopline/v2/src/test`).
2. У каталозі `code/modules/shopline/v2` виконати: `mvn -B test -Dtest=ShippingFeeTest`
3. Або безпосередньо: `new DiscountCalculator().shippingFee(new BigDecimal("1500.00"))`

## Фактичний результат

`120.00`

## Очікуваний результат

`0.00` — за BL-60 доставка безкоштовна, якщо сума після знижок **не менша** за 1500 грн (межа включно).

## Причина (з порівняння вихідників v1 і v2)

У `DiscountCalculator.shippingFee` умову порога змінено з `>= FREE_SHIPPING_FROM` на `> FREE_SHIPPING_FROM`, через що значення рівно 1500.00 трактується як нижче порога. Сусідні значення тесту поводяться правильно: 1499.99 → 120.00, 1500.01 → 0.00.

## Чому це регресія

У спринті 4 BL-60 була єдиною BL-вимогою без жодного дефекту: [`docs/sprint4/coverage.xlsx`](../sprint4/coverage.xlsx), аркуш «Зведення» — 4 тест-методи, 4 зелені, 0 червоних. Дефекту в цьому місці ніхто не заявляв, виправлення тут не замовлялось. На v1 той самий запуск проходив, на v2 падає.

## Severity і Priority

**Major:** функцію доставки не заблоковано і помилка лише на одній межі одного правила, але вона має прямий грошовий наслідок для покупця: замовлення рівно на 1500 грн отримує доставку за 120 грн замість безкоштовної.
**P2:** дефект блокує проходження автотесту BL-60 (`ShippingFeeTest`) і є регресійним, тобто безпосередньо впливає на критерій К4 («0 відкритих регресійних дефектів», безумовний). Остаточне рішення про priority — за КТ.

## Середовище

- Модуль: `shopline-module` 2.0.0 (`code/modules/shopline/v2`)
- Команда: `mvn -B clean test` у `code/modules/shopline/v2`
- JDK Eclipse Temurin 17.0.21, Apache Maven 3.9.16, Windows 11 Pro
- Обліковий запис: не застосовується (юніт-тест)

## Вивід Surefire (24.11.2026)

Повний лог: [`surefire-v2/unit-test-run.txt`](surefire-v2/unit-test-run.txt), XML: [`surefire-v2/TEST-ua.kpi.tzyapz.shopline.ShippingFeeTest.xml`](surefire-v2/TEST-ua.kpi.tzyapz.shopline.ShippingFeeTest.xml).

```
[ERROR] Tests run: 11, Failures: 1, Errors: 0, Skipped: 0 -- in ua.kpi.tzyapz.shopline.ShippingFeeTest
[ERROR]   ShippingFeeTest.givenSubtotalAfterDiscount_whenShippingFee_returnsFeeByThreshold:37 expected: <0.00> but was: <120.00>
```

## Передано

- Насті Д. (ТА) — для матриці простежуваності: дефект привʼязаний до BL-60.
- Тимчак А. (КТ) — для критерію К4.
