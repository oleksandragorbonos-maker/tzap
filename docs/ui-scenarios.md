# UI-сценарії автоматизації (Selenium WebDriver)

Перелік шести UI-тестів `qa-shopline/src/test/java/ua/kpi/tzyapz/shopline/ui/`: тест-кейс спринта 3 → кроки, локатори, очікування, тест-метод. Склала Горбонос О. (ТА) за кодом тестів і [`docs/sprint3/test-cases.xlsx`](sprint3/test-cases.xlsx). Станом на 16.11.2026.

## Як читати

- **TC** — тест-кейс спринта 3 (аркуш «Test cases»); кроки автоматизованого тесту — це підмножина кроків TC. Відхилення від TC записано в підрозділі «Що відрізняється від TC».
- **Локатори** — атрибути `data-test` (CSS `[data-test='…']`), інше позначено окремо. Усі очікування явні (`WebDriverWait`, 15 с), `Thread.sleep` немає.
- Усі тести мають `@Tag("ui")`, запускаються headless Chrome 1280×800 на https://practicesoftwaretesting.com/ командою  
  `mvn test -DexcludedGroups=none -Dtest="*UiTest"`.
- Простежуваність: TC → тест-метод тут, у `@DisplayName` тесту і в аркуші «Coverage» ([coverage.xlsx](sprint4/coverage.xlsx), рядки з UI).

## Зведення

| TC | Вимога | Сценарій | Клас · метод |
|---|---|---|---|
| TC-001 | F-60 | Пошук у нижньому регістрі | `SearchUiTest` · `givenLowercaseQuery_whenSearch_allProductNamesContainQuery` |
| TC-022 | F-60 | Пошук «Hammer» і скидання кнопкою «X» | `SearchUiTest` · `givenSearchQuery_whenResetSearch_moreProductsAppear` |
| TC-004 | F-20 | Вхід із незареєстрованою адресою | `LoginUiTest` · `givenUnregisteredEmail_whenLogin_errorMessageIsShown` ¹ |
| TC-028 | GUI-30 | Додавання товару в кошик, кількість 1 | `CartUiTest` · `givenProductPage_whenAddToCart_cartQuantityIsOne` |
| TC-014 | F-100, GUI-70 | Зміна кількості перераховує Total | `CartUiTest` · `givenItemInCart_whenQuantityChanged_totalIsUpdated` |
| TC-009 | F-140, GUI-50 | Контактна форма, 49 символів | `ContactUiTest` · `givenMessage49Chars_whenSubmit_validationErrorIsShown` |

¹ Ім'я методу — після задачі № 11 рев'ю (`LoginUiTest`). До її злиття в коді ще `givenRegisteredEmailWrongPassword_whenLogin_errorMessageIsShown`; дані тесту вже відповідають TC-004.

---

## TC-001 · Пошук товарів за частиною назви в нижньому регістрі, без входу

**Вимога:** F-60. **Тест:** `SearchUiTest.givenLowercaseQuery_whenSearch_allProductNamesContainQuery`.

| Крок | Дія | Локатор |
|---|---|---|
| 1 | Відкрити головну сторінку `/` | — |
| 2 | Дочекатися поля пошуку, ввести `pliers` | `[data-test='search-query']` |
| 3 | Натиснути «Search» | `[data-test='search-submit']` |
| 4 | Дочекатися оновлення списку, зчитати назви карток | `[data-test='product-name']` |

**Очікування:** список не порожній, і назва кожної картки містить `pliers` без урахування регістру (перевірка в очікуванні й повторно в `assertAll`).

**Що відрізняється від TC:** заголовок «Searched for: pliers» і текст у полі пошуку не перевіряються; нова сесія замість вікна інкогніто забезпечується окремим екземпляром драйвера для кожного тесту.

## TC-022 · Пошук «Hammer» і очищення запиту кнопкою «X»

**Вимога:** F-60. **Тест:** `SearchUiTest.givenSearchQuery_whenResetSearch_moreProductsAppear`.

| Крок | Дія | Локатор |
|---|---|---|
| 1 | Відкрити `/`, записати назву першої картки | `[data-test='product-name']` (перший) |
| 2 | Ввести `Hammer` у поле пошуку | `[data-test='search-query']` |
| 3 | Натиснути «Search» | `[data-test='search-submit']` |
| 4 | Дочекатися результатів лише з «hammer» у назві, запам'ятати їх кількість | `[data-test='product-name']` |
| 5 | Натиснути «X» | `[data-test='search-reset']` |
| 6 | Дочекатися, поки карток стане більше, ніж було в результатах | `[data-test='product-name']` |

**Очікування:** після «X» список не порожній, карток більше, ніж при пошуку, а назва першої картки з кроку 1 є серед них.

**Що відрізняється від TC:** TC вимагає, щоб перша картка після скидання збігалася з початковою; тест перевіряє, що початкова назва *є* в списку (не обов'язково перша). Порожнє поле пошуку й відсутність заголовка «Searched for:» не перевіряються.

## TC-004 · Вхід із незареєстрованою адресою електронної пошти

**Вимога:** F-20. **Тест:** `LoginUiTest.givenUnregisteredEmail_whenLogin_errorMessageIsShown` (див. примітку ¹).

| Крок | Дія | Локатор |
|---|---|---|
| 1 | Відкрити `/auth/login` | — |
| 2 | Ввести `shopline.ta.test@example.com` у «Email address» | `[data-test='email']` |
| 3 | Ввести `Test-pass1` у «Password» | `[data-test='password']` |
| 4 | Натиснути «Login» | `[data-test='login-submit']` |
| 5 | Дочекатися повідомлення про помилку | `[data-test='login-error']` |

**Очікування:** текст повідомлення дорівнює `Invalid email or password`.

**Що відрізняється від TC:** сторінку входу відкрито прямим посиланням, а не через «Sign in» у меню; не перевіряються незмінність адреси `/auth/login` і те, що в меню лишився «Sign in». Адреса вигадана й не належить спільному обліковому запису demo, тому спроба нікому не шкодить.

## TC-028 · Додавання товару в кошик, кількість 1 (нижня межа GUI-30)

**Вимога:** GUI-30. **Тест:** `CartUiTest.givenProductPage_whenAddToCart_cartQuantityIsOne`.

| Крок | Дія | Локатор |
|---|---|---|
| 1 | Відкрити `/` | — |
| 2 | Відкрити першу картку каталогу | `[data-test^='product-01']` |
| 3 | Натиснути «Add to cart» | `[data-test='add-to-cart']` |
| 4 | Дочекатися лічильника кошика | `[data-test='cart-quantity']` |

**Очікування:** лічильник кошика показує `1`.

**Що відрізняється від TC:** TC називає товар «Combination Pliers», тест бере першу картку (`product-01…`); поле «Quantity» не змінюється (за замовчуванням 1); повідомлення «Product added to shopping cart.» і крок «Cart» з видаленням рядка не автоматизовано.

## TC-014 · Зміна кількості в кошику перераховує «Total»

**Вимоги:** F-100, GUI-70. **Тест:** `CartUiTest.givenItemInCart_whenQuantityChanged_totalIsUpdated`.

| Крок | Дія | Локатор |
|---|---|---|
| 1 | Відкрити `/`, першу картку каталогу | `[data-test^='product-01']` |
| 2 | Зчитати одиничну ціну на сторінці товару | `[data-test='unit-price']` |
| 3 | Додати в кошик, дочекатися лічильника `1` | `[data-test='add-to-cart']`, `[data-test='cart-quantity']` |
| 4 | Відкрити `/checkout`, записати початковий Total | `[data-test='cart-total']` |
| 5 | У полі кількості замінити 1 на 2, натиснути Tab | `[data-test='product-quantity']` |
| 6 | Дочекатися зміни Total | `[data-test='cart-total']` |

**Очікування:** Total змінився й дорівнює `$` + (одинична ціна × 2), округлено HALF_UP до двох знаків.

**Що відрізняється від TC:** кількість 2 замість 3; кошик відкрито прямим переходом на `/checkout`, а не піктограмою; видалення рядка й повідомлення про порожній кошик (крок 7 TC) не автоматизовано. Очікувана сума, як і в TC, виводиться з ціни товару, а не з константи.

## TC-009 · Контактна форма: повідомлення з 49 символів не надсилається

**Вимоги:** F-140, GUI-50. **Тест:** `ContactUiTest.givenMessage49Chars_whenSubmit_validationErrorIsShown`.

| Крок | Дія | Локатор |
|---|---|---|
| 1 | Відкрити `/contact` | — |
| 2 | Ввести ім'я `Test` | `[data-test='first-name']` |
| 3 | Ввести прізвище `User` | `[data-test='last-name']` |
| 4 | Ввести email `test@example.com` | `[data-test='email']` |
| 5 | Вибрати тему зі значенням `warranty` | `[data-test='subject']` (`Select`) |
| 6 | Ввести 49 символів `A` | `[data-test='message']` |
| 7 | Натиснути «Send» | `[data-test='contact-submit']` |
| 8 | Дочекатися повідомлення під полем «Message» | `[data-test='message-error']` |

**Очікування:** текст помилки містить `minimal 50 characters`; елемента `.alert-success` на сторінці немає (форму не надіслано).

**Що відрізняється від TC:** дані інші (TC: `Tester`, `tester.tc009@example.com`, тема «Customer service», рядок `abcdefghij…`). Перевірка тексту `minimal 50 characters` суворіша за TC: після рецензії TC вимагає лише «текстове повідомлення про помилку під полем» без прив'язки до формулювання. Якщо demo змінить текст, тест упаде без дефекту, тому в майбутньому її варто послабити до перевірки наявності повідомлення. `.alert-success` — єдиний локатор не за `data-test`.

---

## Не автоматизовано

З 44 тест-кейсів спринта 3 автоматизовано шість; їх вибрано за нормативом «≥6 UI-сценаріїв» і за стабільністю demo без входу (крім TC-004, який перевіряє відмову на вигаданій адресі). Решту, зокрема негативну пару пошуку TC-033, лишено на ручне виконання.
