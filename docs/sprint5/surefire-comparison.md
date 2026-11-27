# Порівняння Surefire v1 і v2 по кожному запуску

Задача SCRUM-168 · Коротка К. (ІТ) · 24.11.2026 · модуль ШопЛайн v2 (`shopline-module` 2.0.0)

Джерела: [`surefire-v1/`](surefire-v1/) (прогін спринта 4 на v1) і [`surefire-v2/`](surefire-v2/) (прогін 24.11.2026 на v2). Зіставлено кожен `testcase` у `TEST-*.xml` за ключем **клас + метод + індекс параметра** (план регресії, Д1). Набори запусків v1 і v2 однакові: тести спринта 4 скопійовано без змін.

## Підсумок Surefire

| Прогін | Команда | v1 | v2 |
|---|---|---|---|
| Юніти | `mvn -B clean test` (у `code/modules/shopline/v2`) | Tests run: 142, Failures: 28, Errors: 1 | Tests run: 142, Failures: 13, Errors: 1 |
| UI (WebDriver) | `mvn -B test -DexcludedGroups=none -Dtest="*UiTest"` | Tests run: 6, Failures: 0 | Tests run: 6, Failures: 0 |

Червоних запусків юнітів: **29 → 14**. За підсумком це виглядає як чисте покращення, але по запусках видно одну регресію (нижче).

Звірка: `142 = 140` (48 тест-методів команди) `+ 2` (`ExampleTest`, шаблон курсу, у журнал не входить).

## Переходи по класах (у запусках)

| Тест-клас | Запусків | Червоних v1 | Червоних v2 | Passed → Passed | Failed → Passed | Failed → Failed | **Passed → Failed** |
|---|---|---|---|---|---|---|---|
| `CartRepeatedAddTest` | 10 | 9 | 0 | 1 | 9 | 0 | 0 |
| `CartTest` | 36 | 5 | 3 | 31 | 2 | 3 | 0 |
| `PromoCodeValidationTest` | 37 | 7 | 5 | 30 | 2 | 5 | 0 |
| `PromoCodeValidatorTest` | 11 | 2 | 1 | 9 | 1 | 1 | 0 |
| `ShippingFeeTest` | 11 | 0 | 1 | 10 | 0 | 0 | **1** |
| `VolumeDiscountTest` | 35 | 6 | 4 | 29 | 2 | 4 | 0 |
| `ExampleTest` | 2 | 0 | 0 | 2 | 0 | 0 | 0 |
| `CartUiTest` | 2 | 0 | 0 | 2 | 0 | 0 | 0 |
| `ContactUiTest` | 1 | 0 | 0 | 1 | 0 | 0 | 0 |
| `LoginUiTest` | 1 | 0 | 0 | 1 | 0 | 0 | 0 |
| `SearchUiTest` | 2 | 0 | 0 | 2 | 0 | 0 | 0 |
| **Разом** | **148** | **29** | **14** | **118** | **16** | **13** | **1** |

Баланс юнітів: `113 (зелених на v1) + 16 (Failed → Passed) − 1 (Passed → Failed) = 128 (зелених на v2)`.

## Passed → Failed — регресія

| Запуск | Вхід | v1 | v2 | Вимога |
|---|---|---|---|---|
| `ShippingFeeTest.givenSubtotalAfterDiscount_whenShippingFee_returnsFeeByThreshold[4]` | `subtotalAfterDiscount = 1500.00`, очікувано `0.00` | Passed | Failed: expected: <0.00> but was: <120.00> | BL-60, [#122](https://github.com/oleksandragorbonos-maker/tzap/issues/122) |

Причина в коді v2: у `DiscountCalculator.shippingFee` умову порога змінено з `>= FREE_SHIPPING_FROM` на `> FREE_SHIPPING_FROM`. На v1 клас `ShippingFeeTest` був повністю зелений (11 з 11), дефекту BL-60 ніхто не заявляв. Звіт — [#122](https://github.com/oleksandragorbonos-maker/tzap/issues/122), [defects-regression.md](defects-regression.md) (SCRUM-173).

## Failed → Passed — виправлено (кандидати на Verified → Closed)

| Дефект | Запуски |
|---|---|
| #72 | `CartTest.givenQtyOutOfRange_whenAddItem_throwsIllegalArgument[3]`<br>`CartTest.givenQtyOutOfRange_whenChangeQty_throwsIllegalArgument[3]` |
| #77 | `CartRepeatedAddTest.givenTotalAbove99_whenAddItemTwice_throwsIllegalArgumentAndCartUnchanged[1]`<br>`CartRepeatedAddTest.givenTotalAbove99_whenAddItemTwice_throwsIllegalArgumentAndCartUnchanged[2]`<br>`CartRepeatedAddTest.givenTotalAbove99_whenAddItemTwice_throwsIllegalArgumentAndCartUnchanged[3]`<br>`CartRepeatedAddTest.givenTotalAbove99_whenAddItemTwice_throwsIllegalArgumentAndCartUnchanged[4]`<br>`CartRepeatedAddTest.givenSameSku_whenAddItemTwice_quantityIsSummedInSingleLine`<br>`CartRepeatedAddTest.givenTotalUpTo99_whenAddItemTwice_quantitiesAreSummed[1]`<br>`CartRepeatedAddTest.givenTotalUpTo99_whenAddItemTwice_quantitiesAreSummed[2]`<br>`CartRepeatedAddTest.givenTotalUpTo99_whenAddItemTwice_quantitiesAreSummed[3]`<br>`CartRepeatedAddTest.givenTotalUpTo99_whenAddItemTwice_quantitiesAreSummed[4]` |
| #81 | `PromoCodeValidationTest.givenSingleUseCode_whenValidate_usedCodeIsRejected[1]`<br>`PromoCodeValidationTest.givenUsedSingleUseAndBadPercent_whenValidate_returnsUsedReason`<br>`PromoCodeValidatorTest.givenUsedSingleUsePromo_whenApply_throwsIllegalArgument` |
| #87 | `VolumeDiscountTest.givenSubtotalAtTierBoundaries_whenVolumeDiscountPercent_returnsTierPercent[6]`<br>`VolumeDiscountTest.givenSubtotalAtTierBoundaries_whenVolumeDiscount_returnsDiscountAmount[6]` |

Разом **16** запусків.

## Failed → Failed — не виправлено (кандидати на Reopened)

| Дефект | Запуски | Результат v2 |
|---|---|---|
| #73 | `CartTest.givenPriceRoundingToZero_whenAddItem_throwsIllegalArgument[1]`<br>`CartTest.givenPriceRoundingToZero_whenAddItem_throwsIllegalArgument[2]` | Failure |
| #74 | `CartTest.givenEmptyName_whenAddItem_throwsIllegalArgument[1]` | Failure |
| #80 | `PromoCodeValidationTest.givenExpiryAroundToday_whenValidate_expiryDayIsInclusive[2]`<br>`PromoCodeValidatorTest.givenPromoExpiresOnToday_whenApply_returnsDiscount` | Error, Failure |
| #88 | `VolumeDiscountTest.givenSubtotalAtTierBoundaries_whenVolumeDiscount_returnsDiscountAmount[5]`<br>`VolumeDiscountTest.givenSubtotalAtTierBoundaries_whenVolumeDiscount_returnsDiscountAmount[8]`<br>`VolumeDiscountTest.givenAmountWithHalfCent_whenVolumeDiscount_roundsHalfUp[1]`<br>`VolumeDiscountTest.givenAmountWithHalfCent_whenVolumeDiscount_roundsHalfUp[2]` | Failure |
| #89 | `PromoCodeValidationTest.givenFixedValueAndVolumeDiscount_whenValidate_limitIsSubtotalMinusDiscount[4]`<br>`PromoCodeValidationTest.givenFixedValueAndVolumeDiscount_whenValidate_limitIsSubtotalMinusDiscount[6]`<br>`PromoCodeValidationTest.givenFixedValueAndVolumeDiscount_whenValidate_limitIsSubtotalMinusDiscount[8]`<br>`PromoCodeValidationTest.givenFixedValueAndVolumeDiscount_whenValidate_limitIsSubtotalMinusDiscount[10]` | Failure |

Разом **13** запусків. Error лишився один — `PromoCodeValidatorTest.givenPromoExpiresOnToday_whenApply_returnsDiscount` (#80); у журналі він Failed із приміткою «Error, не Failure».

## Що змінили розробники (діф `qa-shopline/src/main` → `code/modules/shopline/v2/src/main`)

| Файл | Зміна | Ефект |
|---|---|---|
| `Cart.java` | `MAX_QTY`: 100 → 99 | виправляє #72 |
| `Cart.java` | `addItem`: повторний sku сумує кількість, перевірка ліміту 99 | виправляє #77 |
| `DiscountCalculator.java` | `volumeDiscountPercent`: `compareTo(TIER_10) > 0` → `>= 0` | виправляє #87 |
| `PromoCodeValidator.java` | `validate`: додано перевірку `singleUse && used` | виправляє #81 |
| `DiscountCalculator.java` | `shippingFee`: `>= 1500` → `> 1500` | **регресія BL-60** |

`PromoCode.java` без змін. У місцях дефектів #73, #74, #80, #88, #89 код у v2 не змінювався.

## UI (WebDriver)

Усі 6 сценаріїв Passed → Passed на demo «v2.5 | Built 2026-10-11» (та сама збірка, що в спринті 3), Chrome 158.0.8127.37, headless 1280×800.
