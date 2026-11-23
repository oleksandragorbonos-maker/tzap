# План регресійного тестування — ШопЛайн, версія 2

Команда: С · Продукт: №1 «ШопЛайн» · Спринт: 5 · Дата: 22.11.2026 · Автор: Тимчак Анастасія (КТ)
Задачі: SCRUM-153 (вихідні дані), SCRUM-154 (план), SCRUM-155 (критерії go/no-go) · Епік: SCRUM-152

> **Статус документа:** зафіксовано **до першого прогону на v2**. Пороги критеріїв go/no-go (розд. 6) після прогону не змінюються; якщо зміна все ж знадобиться — лише записом у розд. 9 з датою і причиною, і висновок у звіті подається за обома варіантами.

---

## 1. Вихідні дані (інвентаризація досьє)

| Що | Кількість | Звідки взято |
|---|---|---|
| Ручні тест-кейси спринта 3 | **37** (поле «вимога» заповнене у всіх 37) | [`docs/sprint3/test-cases.xlsx`](../sprint3/test-cases.xlsx), аркуш «Test cases» |
| Результат ручних кейсів на v1 (demo v2.5 від 11.10.2026) | 21 passed · 14 failed · 2 blocked | [`docs/sprint3/execution-log.xlsx`](../sprint3/execution-log.xlsx), аркуш «Run 1» (останній запис по кожному кейсу) |
| JUnit тест-методи спринта 4 | **48** (16 червоних на v1) | [`docs/sprint4/coverage.xlsx`](../sprint4/coverage.xlsx), аркуші «Coverage», «Зведення» |
| Запусків Surefire на v1 (юніти) | **142**: 140 — запуски 48 тест-методів, 2 — `ExampleTest` (шаблон курсу, не входить у 48) | [`docs/sprint4/surefire-reports/`](../sprint4/surefire-reports/), 11 `TEST-*.xml` + `.txt` |
| Підсумок Surefire v1 (юніти) | Tests run 142 · Failures 28 · Errors 1 · Skipped 0 | там само; [`docs/sprint4/README.md`](../sprint4/README.md) |
| UI-сценарії WebDriver | **6** (усі зелені на v1) | [`docs/sprint4/surefire-reports/`](../sprint4/surefire-reports/) — `*UiTest`, [`ui-test-run.txt`](../sprint4/surefire-reports/ui-test-run.txt) |
| Дефекти у трекері | **25**: 16 demo (`sprint:3`) + 9 модуля (`sprint:4`) | [GitHub Issues](https://github.com/oleksandragorbonos-maker/tzap/issues) |
| Вимоги SRS 1.1 | **41** (F 14 · GUI 11 · BL 7 · NFR 9), High — **24** (F 9 · GUI 5 · BL 7 · NFR 3) | [`docs/sprint2/SRS-1.1.md`](../sprint2/SRS-1.1.md) |

Запуски Surefire v1 по класах (для звірки з v2 — саме по класах і назвах, не за підсумком):

| Тест-клас | Запусків | Failures | Errors |
|---|---|---|---|
| `CartRepeatedAddTest` | 10 | 9 | 0 |
| `CartTest` | 36 | 5 | 0 |
| `PromoCodeValidationTest` | 37 | 7 | 0 |
| `PromoCodeValidatorTest` | 11 | 1 | 1 |
| `ShippingFeeTest` | 11 | 0 | 0 |
| `VolumeDiscountTest` | 35 | 6 | 0 |
| `ExampleTest` | 2 | 0 | 0 |
| **Разом** | **142** | **28** | **1** |

**Чого бракує / що виявлено на інвентаризації:**

1. ~~Модуль v2 у репозиторії відсутній.~~ **Знято 22.11.2026:** v2 (`shopline-module` 2.0.0, з архіву курсу, без змін) закомічено в [`code/modules/shopline/v2`](../../code/modules/shopline/v2) (PR #118, Коротка К.). Діф з v1 перевірено КТ 23.11: рівно 5 змін у `Cart`, `DiscountCalculator`, `PromoCodeValidator`.
2. Окремого поля «вимога» у звітах про дефекти немає — ID вимоги у заголовку в дужках. Привʼязку «дефект → одна найконкретніша вимога» веде ТА у матриці (див. Д8).
3. ~~Surefire v1 скопіювати~~ **Зроблено 22.11.2026:** [`docs/sprint5/surefire-v1/`](surefire-v1/), 25 файлів, вміст збігається з `docs/sprint4/surefire-reports/` (різниця лише в закінченнях рядків CRLF → LF), інкремент спринта 4 не змінено.

---

## 2. Склад регресійного набору

| Тип | Кількість рядків у журналі | Середовище |
|---|---|---|
| Ручні кейси спринта 3 | 37 | demo https://practicesoftwaretesting.com, поточна збірка (фіксується в аркуші «Environment» у день першого ручного прогону) |
| JUnit тест-методи спринта 4 | 48 | модуль ШопЛайн v2 (`shopline-module` 2.0.0), JDK 17, Maven |
| UI-сценарії WebDriver | 6 | demo, той самий день і браузер, що й ручні кейси |
| **Разом** | **91** | |
| Повторна перевірка дефектів | 25 (+ нові, зокрема регресійні) | за кроками відтворення з Issue |

Набір — **100 %** кейсів спринта 3 і **100 %** автонабору спринта 4, без вибірки.

---

## 3. Домовленості, зафіксовані до прогону

**Д1. Одиниця обліку — рядок журналу = кейс / тест-метод / UI-сценарій (91 рядок).**
- JUnit тест-метод має результат **Passed**, лише якщо пройшли **всі** його параметризовані запуски; будь-який Failure або Error хоча б в одному запуску → **Failed**. У коментарі — індекси запусків, що впали (`[4]`, `[5][8]`), і примітка «Error, не Failure», якщо так у Surefire.
- Метрики E, PR, A рахуються на 91 рядку журналу.
- Підсумки Surefire (запуски) подаються **окремим рядком** у розд. 7 звіту як доказова база, зі звіркою: `142 = 140 (48 тест-методів) + 2 (ExampleTest)`. `ExampleTest` — шаблон курсу, у 48 не входить і в журнал не заноситься; його результат показується лише у звірці Surefire.
- **Регресія визначається на рівні запуску** (клас + метод + індекс), не тест-методу: метод, що був Failed на v1, може приховати новий червоний індекс. Порівняння v1/v2 — по кожному `testcase` у `TEST-*.xml`.

**Д2. «Покрито частково» вважається покриттям** у метриці C і критерії К2 — формула `C = вимог із ≥1 кейсом / усіх вимог` рахує наявність перевірки. Але в матриці такі рядки позначаються «частково» з переліком неперевіреного, а звіт **додатково** показує суворий варіант покриття High (частково = не покрито). Стосується S-10 (TC-036) і S-20 (TC-032).

**Д3. BL-30** — рядок у матриці лишається з позначкою «вилучена після аудиту (SRS 1.1, дубль BL-20)», у знаменник 41 не входить.

**Д4. Severity для К1 — як у трекері на дату цього плану.** Дефект **#50 лишається `severity:critical`, `priority:P3`**. Це не неузгодженість, а дві різні шкали: severity — наслідок для продукту (порушено вимогу безпеки S-20, High: неавторизований відвідувач потрапляє на крок 3 «Billing Address» через «Continue as Guest»); priority за таблицею 4.13 — чи блокує дефект виконання кейсів (не блокує: TC-032 виконано до кінця). Рішення КТ спринта 3 від 28.10.2026 записане в коментарі до #50; переглядати severity, щоб пройти К1, не будемо — це була б підміна висновку.

**Д5. Статуси дефектів.** Ставить ІТ (або виконавиця кейса, до якого привʼязано дефект), затверджує КТ протягом доби. Статус змінюється лише після реального перевиконання кроків, із датою, виконавцем і фактичним результатом у коментарі. Дефекти модуля: перед прогоном → `Fixed`; після прогону → `Closed (verified)` або `Reopened`. Дефекти demo: `Open` + «підтверджено на v2, дата» або `Closed (not reproducible)` + причина.

**Д6. Результати в журналі:** лише Passed / Failed / Blocked. Skipped не допускається. Кожен Failed — з посиланням на дефект (наявний або новий), кожен Blocked — з причиною і датою спроби.

**Д7. Тестові облікові записи.** Кейси, що потребують входу (зокрема TC-017 і TC-026, заблоковані у спринті 3 через заблокований спільний `customer@practicesoftwaretesting.com`), виконуються на **власних** облікових записах виконавиць із вигаданими адресами `@example.com` — рішення ретро спринта 3 (Start-1). Нові спроби входу в спільний акаунт не робимо. Якщо товар із передумови («Combination Pliers») знову «Out of stock» — Blocked із причиною і повтор наступного дня, кейс не переписуємо.

**Д8. #80 і #81 привʼязуються до BL-80** (як у заголовках Issues, тілі звітів і [`docs/sprint4/README.md`](../sprint4/README.md), де вони записані «BL-80 (BL-70)»). Обидва дефекти виявлено тестом методу `apply` (`PromoCodeValidatorTest`), а BL-80 — вимога саме до `apply`: для #81 вона прямо вимагає відхиляти використаний одноразовий код, для #80 — повертати знижку для придатного коду. BL-70 у матриці отримує примітку «той самий дефект на рівні `validate` — #80, #81 (привʼязано до BL-80)», без повторного підрахунку. На щільність за розділами вибір не впливає: обидві вимоги в розділі BL.

**Д9. D-20 у спринті 5 лишається непокритою: 36 / 41.** README модуля тепер є ([`code/modules/shopline/v2/README.md`](../../code/modules/shopline/v2/README.md)), але кейса для D-20 у наборі спринтів 3–4 немає, а регресія нових перевірок не проєктує — вона перевиконує наявний набір (91 рядок). Причину в матриці оновити: «README модуля надано з v2; кейс не проєктувався, регресія — лише наявний набір». У рекомендаціях звіту — додати кейс-рецензію README за D-20. На К2 рішення не впливає: і 36/41 = 87,8 %, і 37/41 = 90,2 % вище порога 85 %, а D-20 має пріоритет Medium.

---

## 4. Розподіл виконавців

Ручні кейси поділено між двома людьми **за правилом «ніхто не виконує кейси власного авторства»** (ефект автора). Автори — з поля «Автор» у `test-cases.xlsx`:

| Виконавиця v2 | Кейси (за автором спринта 3) | Кількість |
|---|---|---|
| Коротка К. (ІТ) | автор Горбонос О. — TC-009…014, 027, 028, 031, 032, 037; автор Тимчак А. — TC-015…019, 025, 026 | 18 |
| Горбонос О. (СМ) | автор Коротка К. — TC-001…008, 029, 030, 034, 035; автор Дрєпіна А. — TC-020…024, 033, 036 | 19 |

> Відрізняється від попереднього поділу «TC-001…019 / TC-020…037»: за ним Коротка К. виконувала б 8 власних кейсів (TC-001…008), а Горбонос О. — 5 власних (TC-027, 028, 031, 032, 037).

Автонабір (48 JUnit + 6 WebDriver), Surefire v1/v2 і порівняння — **Коротка К. (ІТ)**. Затвердження статусів дефектів — **Тимчак А. (КТ)**.

### 4.1. Ручні кейси

Колонки «v1» і «дефект» — останній запис по кейсу в «Run 1» спринта 3.

| ID | Вимога | Пріоритет | Автор (С3) | v1 | Дефект (С3) | Виконавиця v2 | Дата прогону v2 |
|---|---|---|---|---|---|---|---|
| TC-001 | F-60 | High | Коротка К. | passed | — | **Горбонос О. (СМ)** | 24.11.2026 |
| TC-002 | GUI-30 | High | Коротка К. | passed | — | **Горбонос О. (СМ)** | 24.11.2026 |
| TC-003 | GUI-30 | High | Коротка К. | failed | #54 | **Горбонос О. (СМ)** | 24.11.2026 |
| TC-004 | F-20 | High | Коротка К. | passed | — | **Горбонос О. (СМ)** | 24.11.2026 |
| TC-005 | F-10, GUI-65 | High | Коротка К. | failed | #55 | **Горбонос О. (СМ)** | 24.11.2026 |
| TC-006 | F-10, GUI-50 | High | Коротка К. | passed | — | **Горбонос О. (СМ)** | 24.11.2026 |
| TC-007 | F-10 | High | Коротка К. | failed | #65 | **Горбонос О. (СМ)** | 24.11.2026 |
| TC-008 | F-10 | High | Коротка К. | passed | — | **Горбонос О. (СМ)** | 24.11.2026 |
| TC-009 | F-140, GUI-50 | High | Горбонос О. | passed | — | **Коротка К. (ІТ)** | 25.11.2026 |
| TC-010 | F-140, GUI-90 | Medium | Горбонос О. | failed | #57 | **Коротка К. (ІТ)** | 25.11.2026 |
| TC-011 | F-140 | Medium | Горбонос О. | failed | #57 | **Коротка К. (ІТ)** | 25.11.2026 |
| TC-012 | F-140, GUI-50 | High | Горбонос О. | failed | #53 | **Коротка К. (ІТ)** | 25.11.2026 |
| TC-013 | F-140, GUI-50 | High | Горбонос О. | passed | — | **Коротка К. (ІТ)** | 25.11.2026 |
| TC-014 | F-100, GUI-70 | High | Горбонос О. | passed | — | **Коротка К. (ІТ)** | 25.11.2026 |
| TC-015 | F-20, GUI-60, GUI-10 | High | Тимчак А. | passed | — | **Коротка К. (ІТ)** | 26.11.2026 |
| TC-016 | F-20 | High | Тимчак А. | passed | — | **Коротка К. (ІТ)** | 26.11.2026 |
| TC-017 | F-110 | High | Тимчак А. | blocked | — | **Коротка К. (ІТ)** | 26.11.2026 |
| TC-018 | GUI-40, F-120 | Medium | Тимчак А. | passed | — | **Коротка К. (ІТ)** | 26.11.2026 |
| TC-019 | F-30 | Medium | Тимчак А. | failed | #68 | **Коротка К. (ІТ)** | 26.11.2026 |
| TC-020 | F-40, GUI-20 | High | Дрєпіна А. | passed | — | **Горбонос О. (СМ)** | 25.11.2026 |
| TC-021 | F-50 | High | Дрєпіна А. | passed | — | **Горбонос О. (СМ)** | 25.11.2026 |
| TC-022 | F-60 | High | Дрєпіна А. | passed | — | **Горбонос О. (СМ)** | 25.11.2026 |
| TC-023 | F-70 | Medium | Дрєпіна А. | passed | — | **Горбонос О. (СМ)** | 25.11.2026 |
| TC-024 | F-80, F-90 | High | Дрєпіна А. | passed | — | **Горбонос О. (СМ)** | 25.11.2026 |
| TC-025 | F-30, GUI-50 | Medium | Тимчак А. | passed | — | **Коротка К. (ІТ)** | 26.11.2026 |
| TC-026 | F-120 | Medium | Тимчак А. | blocked | — | **Коротка К. (ІТ)** | 26.11.2026 |
| TC-027 | GUI-30 | High | Горбонос О. | failed | #52 | **Коротка К. (ІТ)** | 25.11.2026 |
| TC-028 | GUI-30 | High | Горбонос О. | passed | — | **Коротка К. (ІТ)** | 25.11.2026 |
| TC-029 | GUI-80 | Low | Коротка К. | failed | #56 | **Горбонос О. (СМ)** | 25.11.2026 |
| TC-030 | GUI-100 | Low | Коротка К. | passed | — | **Горбонос О. (СМ)** | 25.11.2026 |
| TC-031 | F-130 | Medium | Горбонос О. | failed | #67 | **Коротка К. (ІТ)** | 25.11.2026 |
| TC-032 | S-20 | High | Горбонос О. | failed | #50 | **Коротка К. (ІТ)** | 25.11.2026 |
| TC-033 | F-60 | Medium | Дрєпіна А. | passed | — | **Горбонос О. (СМ)** | 25.11.2026 |
| TC-034 | U-30 | Medium | Коротка К. | failed | #63, #66 | **Горбонос О. (СМ)** | 25.11.2026 |
| TC-035 | U-20 | Medium | Коротка К. | passed | — | **Горбонос О. (СМ)** | 25.11.2026 |
| TC-036 | S-10 | High | Дрєпіна А. | failed | #59 | **Горбонос О. (СМ)** | 25.11.2026 |
| TC-037 | F-140 | High | Горбонос О. | failed | #51 | **Коротка К. (ІТ)** | 25.11.2026 |

### 4.2. Автонабір

Виконавиця всіх 54 рядків — **Коротка К. (ІТ)**, дата прогону — **24.11.2026**. Результат v1 — з аркуша «Coverage» `coverage.xlsx` і Surefire v1.

| № | Тест-метод | Вимога | Кейс-джерело (UI) | v1 | Тип |
|---|---|---|---|---|---|
| J-01 | `CartTest.givenQtyWithinRange_whenAddItem_itemIsAdded` | BL-10 | — | Passed | JUnit |
| J-02 | `CartTest.givenQtyOutOfRange_whenAddItem_throwsIllegalArgument` | BL-10 | — | Failed | JUnit |
| J-03 | `CartTest.givenNonPositivePrice_whenAddItem_throwsIllegalArgument` | BL-10 | — | Passed | JUnit |
| J-04 | `CartTest.givenPriceRoundingToZero_whenAddItem_throwsIllegalArgument` | BL-10 | — | Failed | JUnit |
| J-05 | `CartTest.givenEmptySku_whenAddItem_throwsIllegalArgument` | BL-10 | — | Passed | JUnit |
| J-06 | `CartTest.givenEmptyName_whenAddItem_throwsIllegalArgument` | BL-10 | — | Failed | JUnit |
| J-07 | `CartTest.givenRejectedAdd_whenCheckingCart_cartIsUnchanged` | BL-10 | — | Passed | JUnit |
| J-08 | `CartTest.givenPriceWithThreeDecimals_whenAddItem_priceIsRoundedHalfUp` | BL-10 | — | Passed | JUnit |
| J-09 | `CartTest.givenExistingItem_whenRemoveItem_itemIsRemoved` | BL-40 | — | Passed | JUnit |
| J-10 | `CartTest.givenAbsentSku_whenRemoveItem_throwsNoSuchElement` | BL-40 | — | Passed | JUnit |
| J-11 | `CartTest.givenAbsentSku_whenChangeQty_throwsNoSuchElement` | BL-40 | — | Passed | JUnit |
| J-12 | `CartTest.givenQtyWithinRange_whenChangeQty_qtyIsUpdated` | BL-40 | — | Passed | JUnit |
| J-13 | `CartTest.givenQtyOutOfRange_whenChangeQty_throwsIllegalArgument` | BL-40 | — | Failed | JUnit |
| J-14 | `CartTest.givenChangedQty_whenSubtotalAndItemCount_reflectNewQty` | BL-40 | — | Passed | JUnit |
| J-15 | `CartTest.givenTwoItems_whenSubtotal_returnsSumOfLineTotals` | BL-40 | — | Passed | JUnit |
| J-16 | `CartTest.givenEmptyCart_whenSubtotalAndItemCount_returnZero` | BL-40 | — | Passed | JUnit |
| J-17 | `CartTest.givenSeveralItems_whenItemCount_returnsSumOfQuantities` | BL-40 | — | Passed | JUnit |
| J-18 | `CartRepeatedAddTest.givenSameSku_whenAddItemTwice_quantityIsSummedInSingleLine` | BL-20 | — | Failed | JUnit |
| J-19 | `CartRepeatedAddTest.givenTotalUpTo99_whenAddItemTwice_quantitiesAreSummed` | BL-20 | — | Failed | JUnit |
| J-20 | `CartRepeatedAddTest.givenTotalAbove99_whenAddItemTwice_throwsIllegalArgumentAndCartUnchanged` | BL-20 | — | Failed | JUnit |
| J-21 | `CartRepeatedAddTest.givenDifferentSkus_whenAddItem_separateLinesAreCreated` | BL-20 | — | Passed | JUnit |
| J-22 | `ShippingFeeTest.givenSubtotalAfterDiscount_whenShippingFee_returnsFeeByThreshold` | BL-60 | — | Passed | JUnit |
| J-23 | `ShippingFeeTest.givenNegativeAmount_whenShippingFee_throwsIllegalArgument` | BL-60 | — | Passed | JUnit |
| J-24 | `ShippingFeeTest.givenSubtotalAbove1500_whenDiscountDropsItBelow_shippingIsCharged` | BL-60 | — | Passed | JUnit |
| J-25 | `ShippingFeeTest.givenVolumeDiscountApplied_whenShippingFee_isFree` | BL-60 | — | Passed | JUnit |
| J-26 | `PromoCodeValidatorTest.givenPercentPromo_whenApply_returnsRoundedPercent` | BL-80 | — | Passed | JUnit |
| J-27 | `PromoCodeValidatorTest.givenValidFixedPromo_whenApply_returnsFixedAmount` | BL-80 | — | Passed | JUnit |
| J-28 | `PromoCodeValidatorTest.givenExpiredPromo_whenApply_throwsIllegalArgument` | BL-80 | — | Passed | JUnit |
| J-29 | `PromoCodeValidatorTest.givenPromoExpiresOnToday_whenApply_returnsDiscount` | BL-80 | — | Failed | JUnit |
| J-30 | `PromoCodeValidatorTest.givenUsedSingleUsePromo_whenApply_throwsIllegalArgument` | BL-80 | — | Failed | JUnit |
| J-31 | `VolumeDiscountTest.givenSubtotalAtTierBoundaries_whenVolumeDiscountPercent_returnsTierPercent` | BL-50 | — | Failed | JUnit |
| J-32 | `VolumeDiscountTest.givenSubtotalAtTierBoundaries_whenVolumeDiscount_returnsDiscountAmount` | BL-50 | — | Failed | JUnit |
| J-33 | `VolumeDiscountTest.givenAmountWithHalfCent_whenVolumeDiscount_roundsHalfUp` | BL-50 | — | Failed | JUnit |
| J-34 | `VolumeDiscountTest.givenSubtotalBelow2000_whenVolumeDiscount_returnsZero` | BL-50 | — | Passed | JUnit |
| J-35 | `VolumeDiscountTest.givenSubtotalInHigherTier_whenVolumeDiscount_appliesOnlyHighestTier` | BL-50 | — | Passed | JUnit |
| J-36 | `VolumeDiscountTest.givenNegativeSubtotal_whenVolumeDiscountPercent_throwsIllegalArgument` | BL-50 | — | Passed | JUnit |
| J-37 | `VolumeDiscountTest.givenNegativeSubtotal_whenVolumeDiscount_throwsIllegalArgument` | BL-50 | — | Passed | JUnit |
| J-38 | `PromoCodeValidationTest.givenCodeLengthWithin6To10_whenValidate_isValid` | BL-70 | — | Passed | JUnit |
| J-39 | `PromoCodeValidationTest.givenCodeLengthOutOfRange_whenValidate_isInvalid` | BL-70 | — | Passed | JUnit |
| J-40 | `PromoCodeValidationTest.givenExpiryAroundToday_whenValidate_expiryDayIsInclusive` | BL-70 | — | Failed | JUnit |
| J-41 | `PromoCodeValidationTest.givenSingleUseCode_whenValidate_usedCodeIsRejected` | BL-70 | — | Failed | JUnit |
| J-42 | `PromoCodeValidationTest.givenPercentValueAroundBounds_whenValidate_acceptsOnly1To50` | BL-70 | — | Passed | JUnit |
| J-43 | `PromoCodeValidationTest.givenFixedValueAroundBounds_whenValidate_acceptsPositiveUpToSubtotal` | BL-70 | — | Passed | JUnit |
| J-44 | `PromoCodeValidationTest.givenFixedValueAndVolumeDiscount_whenValidate_limitIsSubtotalMinusDiscount` | BL-70 | — | Failed | JUnit |
| J-45 | `PromoCodeValidationTest.givenBadLengthAndExpiry_whenValidate_returnsLengthReason` | BL-70 | — | Passed | JUnit |
| J-46 | `PromoCodeValidationTest.givenExpiryAndUsedSingleUse_whenValidate_returnsExpiryReason` | BL-70 | — | Passed | JUnit |
| J-47 | `PromoCodeValidationTest.givenUsedSingleUseAndBadPercent_whenValidate_returnsUsedReason` | BL-70 | — | Failed | JUnit |
| J-48 | `PromoCodeValidationTest.givenValidCode_whenValidate_reasonIsEmpty` | BL-70 | — | Passed | JUnit |
| U-01 | `SearchUiTest.givenLowercaseQuery_whenSearch_allProductNamesContainQuery` | F-60 | TC-001 | Passed | WebDriver |
| U-02 | `SearchUiTest.givenSearchQuery_whenResetSearch_moreProductsAppear` | F-60 | TC-022 | Passed | WebDriver |
| U-03 | `LoginUiTest.givenUnregisteredEmail_whenLogin_errorMessageIsShown` | F-20 | TC-004 | Passed | WebDriver |
| U-04 | `CartUiTest.givenProductPage_whenAddToCart_cartQuantityIsOne` | GUI-30 | TC-028 | Passed | WebDriver |
| U-05 | `CartUiTest.givenItemInCart_whenQuantityChanged_totalIsUpdated` | F-100, GUI-70 | TC-014 | Passed | WebDriver |
| U-06 | `ContactUiTest.givenMessage49Chars_whenSubmit_validationErrorIsShown` | F-140, GUI-50 | TC-009 | Passed | WebDriver |

Команди (як у спринті 4 — юніти й UI окремо, інакше UI у прогін не потраплять):

```bash
mvn -B test                                          # 48 тест-методів + ExampleTest → surefire-v2/
mvn -B test -DexcludedGroups=none -Dtest="*UiTest"   # 6 UI-сценаріїв на demo
```

### 4.3. Повторна перевірка дефектів

| Дефекти | Як перевіряється | Хто | Дата |
|---|---|---|---|
| Модуля: #72, #73, #74, #77, #80, #81, #87, #88, #89 | тест-методи, що їх виявили, на v2 + кроки з Issue | Коротка К. | 24.11.2026 |
| Demo: #50, #51, #52, #53, #57, #67, #68 | під час кейсів TC-032, 037, 027, 012, 010/011, 031, 019 + кроки з Issue | Коротка К. | 25–26.11.2026 |
| Demo: #54, #55, #56, #58, #59, #63, #64, #65, #66 | під час кейсів TC-003, 005, 029, 023, 036, 034, 006, 007 + кроки з Issue | Горбонос О. | 24–25.11.2026 |
| Нові (зокрема регресійні) | окремий звіт у `docs/sprint5/defects-regression.md` | хто виявив | у день виявлення |

---

## 5. Графік

| Дата | Що | Хто |
|---|---|---|
| 22.11.2026 | План регресії і критерії go/no-go закомічено | Тимчак А. (КТ) |
| 23.11.2026 | Копія Surefire v1 → `docs/sprint5/surefire-v1/`; 9 дефектів модуля → `Fixed` (затверджує КТ); отримано v2 | Коротка К. / Тимчак А. |
| 24.11.2026 | Прогін JUnit і WebDriver на v2; Surefire v2 → `docs/sprint5/surefire-v2/`; порівняння по запусках; статуси дефектів модуля | Коротка К. |
| 24–25.11.2026 | Ручні кейси і перевірка дефектів demo (19 кейсів) | Горбонос О. |
| 25–26.11.2026 | Ручні кейси і перевірка дефектів demo (18 кейсів) | Коротка К. |
| 26.11.2026 | Повторне виконання всіх Blocked; затвердження статусів | обидві виконавиці / Тимчак А. |
| **27.11.2026** | **Журнал регресії закрито** (аркуш «Підсумок»), регресійні дефекти задокументовано | Коротка К. |
| 27–28.11.2026 | Матриця простежуваності і метрики → факти по К1–К5 | Дрєпіна А. (ТА) |
| 01.12.2026 | Висновок go/no-go у розд. 9 звіту | Тимчак А. (КТ) |

Якщо збірка demo змінилась між днями прогону — новий рядок в «Environment» і примітка в коментарях кейсів, виконаних на новій збірці.

---

## 6. Критерії go/no-go

| № | Критерій | Тип | Поріг | Обґрунтування порога | Джерело факту | Виконано? | Факт |
|---|---|---|---|---|---|---|---|
| К1 | Немає відкритих дефектів Blocker і Critical | безумовний | **0** | Blocker/Critical за шкалою спринта 3 — відмова ключової функції або порушення безпеки; з таким дефектом продукт не випускається за жодних інших показників. «Відкритий» = `Open`, `New`, `Reopened` після перевірки на v2. Severity — за Д4 | трекер | | |
| К2 | Покриття вимог тестами | умовний | **100 % вимог High, перевірних наявними засобами** (23 з 24; P-10 виключено з обґрунтуванням) **і ≥ 85 % загалом**, причому непокритими можуть бути **лише NFR** із причиною | Методичка дає «100 % High». P-10 (High) вимагає 20 замірів Lighthouse проти публічного demo — масові запити поза обсягом курсу; причина зафіксована в аркуші «Coverage» спринта 3. Тому 100 % ставимо на High, які курс дозволяє перевірити, а P-10 переносимо в ризики розд. 9. Загальний поріг 85 %: усі функціональні розділи (F, GUI, BL — 32 вимоги) мають бути покриті повністю, а з 9 NFR непокритими можуть лишитись ті, що потребують інструментів, респондентів або документації, яких немає (P-10, P-20, U-10, D-10, D-20): 36/41 = 87,8 % ≥ 85 %. Будь-яка непокрита функціональна вимога — К2 не виконано | матриця | | |
| К3 | Pass rate `PR = passed / (passed + failed) · 100 %` | умовний | **≥ 90 %** і **жоден Failed не привʼязаний до дефекту Major і вище** | Для випуску допустимі лише косметичні відхилення (Minor/Trivial), що не заважають покупцю завершити сценарій. 90 % на 91 рядку — це не більше ~9 Failed; більше падінь означає, що проблеми системні, навіть якщо кожна окремо дрібна. Blocked у знаменник не входить (звітується в К5) | журнал регресії | | |
| К4 | Усі регресійні дефекти закрито | безумовний | **0 відкритих** | Нова версія не може бути гіршою за попередню: тест (запуск) або кейс, що проходив на v1 і падає на v2, — це регресія, і випуск із нею погіршує те, що вже працювало. Визначення регресії — за Д1 (на рівні запуску) | журнал + Surefire v1/v2 | | |
| К5 | Усі Blocked перевиконано або обґрунтовано виключено; Skipped немає | умовний | **0 непояснених Blocked, 0 Skipped** | Blocked без причини — це невідомий результат, а не відсутність дефекту; про якість такої частини продукту нічого сказати не можна | журнал | | |

### Правило рішення

- **go** — виконано всі К1–К5;
- **no-go** — не виконано К1 або К4 (безумовні), **або** не виконано два чи більше умовних (К2, К3, К5);
- **умовний go** — безумовні виконано і не виконано рівно один умовний; у висновку — перелік дій із виконавцями і дата повторної перевірки.

### Як висновок залежить від порогів (для розд. 9 і питань на захисті)

- Якщо читати К2 буквально («100 % High» з P-10 у знаменнику) — К2 не виконується за будь-якого результату прогону (23/24 = 95,8 %); при суворому рахунку часткового покриття — 21/24 = 87,5 %. Саме тому поріг переформульовано **зараз**, а не після прогону.
- Безумовні К1 і К4 від порогів не залежать: якщо після прогону хоч один із них не виконано, жоден інший поріг рішення не змінить.

---

## 7. Звідки беруться факти

| Число | Таблиця-джерело |
|---|---|
| passed / failed / blocked | `docs/sprint5/regression-log.xlsx`, аркуш «Підсумок» |
| запуски Surefire v1 / v2 | `docs/sprint5/surefire-v1/`, `docs/sprint5/surefire-v2/` |
| відкриті дефекти за severity, регресійні | GitHub Issues + `docs/sprint5/defects-regression.md` |
| покриття (загальне і High) | `docs/sprint5/traceability-matrix.xlsx`, аркуш «Непокриті» |

Кожне число у звіті має бути в одній із цих таблиць.

---

## 8. Перегляд severity і priority дефектів (SCRUM-156)

Рішення КТ від 22.11.2026, до прогону. Переноситься в розд. 5 звіту.

**1. #50 — `critical` / `P3` лишається без змін.** Обґрунтування — Д4: severity відображає наслідок (порушено вимогу безпеки S-20 з пріоритетом High), priority за таблицею 4.13 — вплив на виконання кейсів (TC-032 виконано до кінця, інші кейси не заблоковано). Обидві оцінки узгоджені кожна зі своєю шкалою. Наслідок для К1: якщо #50 відтворюється на поточному demo, К1 не виконано.

**2. `P3` у всіх 25 дефектів — підтверджено, змін немає.** Перевірено кожен Blocked спринта 3 (`execution-log.xlsx`, «Run 1»): жоден не спричинений дефектом продукту.

| Кейс | Причина Blocked у спринті 3 | Дефект? |
|---|---|---|
| TC-017, TC-026 | спільний обліковий запис demo заблоковано («Account locked, too many failed attempts») | ні — стан середовища (ретро С3, Start-1) |
| TC-002, TC-014, TC-024, TC-028 | «Combination Pliers» — «Out of stock», «Add to cart» неактивна; після повтору — passed | ні — тестові дані demo |
| TC-036 | Chrome 156 сам підвищує http → https, серверне перенаправлення неможливо перевірити в браузері; перевірено `curl`, звіт #59 | ні — обмеження інструмента; #59 фіксує сам дефект |

Отже, жоден дефект не блокував виконання кейсів, і `P3` («усе інше» за таблицею 4.13) відповідає шкалі. P1/P2 будуть доречні, якщо на v2 дефект справді заблокує кейс — тоді priority переглядається в день виявлення.

**3. Статуси.** 9 дефектів модуля (#72, #73, #74, #77, #80, #81, #87, #88, #89) переводяться в `Fixed` 23.11.2026 до прогону (ставить ІТ, затверджує КТ коментарем в Issue); далі — `Closed (verified)` або `Reopened` за результатом прогону 24.11. 16 дефектів demo лишаються `New`/`Open` до повторної перевірки.

**4. Розбіжність у досьє спринта 3:** у [`docs/sprint3/README.md`](../sprint3/README.md) дефект #51 привʼязано до TC-013, а в журналі «Run 1» — до TC-037 (TC-013 — passed; #51 про вкладення .txt — це саме TC-037). Правильно — TC-037; у звіті і матриці використовуємо журнал.

---

## 9. Журнал змін плану

| Дата | Що змінено | Чому | Хто |
|---|---|---|---|
| 22.11.2026 | Перша версія | — | Тимчак А. (КТ) |
| 22.11.2026 | Д7 (власні облікові записи), розд. 8 — перегляд severity/priority (SCRUM-156) | до прогону, пороги не змінювались | Тимчак А. (КТ) |
| 23.11.2026 | Д8 (#80, #81 → BL-80), Д9 (D-20 не покрито); розд. 1 — v2 і Surefire v1 у репозиторії | питання ТА до матриці; до прогону, пороги не змінювались | Тимчак А. (КТ) |
