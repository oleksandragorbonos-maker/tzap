# «ШопЛайн» — модуль бізнес-логіки, версія 1.0.0

Java-модуль продукту П1 «ШопЛайн» (інтернет-магазин інструментів і садового обладнання) для
спринта 4 «Автоматизація». Реалізує правила розділу 5 «Вимоги до бізнес-логіки модуля»
специфікації `SRS_01_ШопЛайн.html`.

## Як зібрати і запустити тести

Потрібні JDK 17 і Maven 3.9 (або новіший).

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)   # macOS; на Windows/Linux укажіть шлях до JDK 17
mvn -q -B test
```

Звіт Surefire з'явиться у `target/surefire-reports/`. UI-тести (тег `ui`) у цьому модулі відсутні,
тому властивість `excludedGroups` із `pom.xml` на результат не впливає.

## Що всередині

Пакет `ua.kpi.tzyapz.shopline`:

| Клас | Призначення | Правила SRS |
|---|---|---|
| `Cart` | Кошик покупця: `addItem(sku, name, unitPrice, qty)`, `removeItem(sku)`, `changeQty(sku, qty)`, `subtotal()`, `itemCount()`, `qtyOf(sku)`, `items()` | BL-10, BL-20, BL-40 |
| `DiscountCalculator` | Знижка за обсяг `volumeDiscountPercent(subtotal)`, `volumeDiscount(subtotal)`; вартість доставки `shippingFee(subtotalAfterDiscount)` | BL-50, BL-60 |
| `PromoCodeValidator` | Перевірка промокоду `validate(promo, today, subtotal)` і сума знижки `apply(promo, today, subtotal)` | BL-70, BL-80 |
| `PromoCode` (record) | Дані промокоду: код, тип (`PERCENT`/`FIXED`), значення, дата закінчення, ознаки одноразовості й використання | BL-70 |

`src/test/java/.../ExampleTest.java` — два тести-зразки стилю (JUnit 5, `@DisplayName` з посиланням на BL-xx).

## Реалізовані правила (коротко)

- **BL-10.** Кількість в одному рядку 1..99; ціна > 0; артикул непорожній; інакше `IllegalArgumentException`, кошик не змінюється.
- **BL-20.** Повторне додавання того самого артикула збільшує кількість наявного рядка (сумарно не більше 99), а не створює новий рядок.
- **BL-40.** `removeItem` і `changeQty` працюють лише з наявними рядками (`NoSuchElementException`); `changeQty` приймає 1..99; `subtotal()` — сума ціна × кількість з округленням до копійок (HALF_UP); `itemCount()` — сума кількостей.
- **BL-50.** Знижка за обсяг: ≥ 2000 грн — 5 %, ≥ 5000 грн — 10 %, ≥ 10000 грн — 15 %; лише один, найвищий рівень; округлення до копійок HALF_UP.
- **BL-60.** Доставка: 0 грн, якщо сума після знижки ≥ 1500 грн, інакше 120 грн.
- **BL-70.** Промокод: 6–10 символів A–Z0–9; дата закінчення включно; одноразовий код не використаний; `PERCENT` 1..50; `FIXED` > 0 і ≤ проміжного підсумку.
- **BL-80.** `apply` повертає суму знижки: для `PERCENT` — підсумок × відсоток / 100 (HALF_UP до копійок), для `FIXED` — суму коду; для непридатного коду — `IllegalArgumentException` із причиною.

Усі суми — `BigDecimal` у гривнях, округлення до 2 знаків за правилом `RoundingMode.HALF_UP`.
