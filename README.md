# C — Досьє якості продукту «Шоплайн»

Дисципліна «Технології забезпечення якості програмних засобів», командний формат
(КПІ ім. Ігоря Сікорського). Повний контекст курсу, методології та домовленостей —
у [project-context.md](project-context.md).

Продукт: **№1 «ШопЛайн»** з Каталогу продуктів — інтернет-магазин ручного/електроінструменту
та садового обладнання (Angular SPA + Laravel REST API + MySQL).

## Структура репозиторію

```
docs/
  team/charter.md         — статут команди (оновлюється щоспринта)
  sprint1/
    product.md            — характеристика продукту і контексту
    bpmn/                 — BPMN-моделі (.drawio/.bpmn + експорт .pdf/.png) і review.md
    backlog.md            — беклог user stories
    tasks.md               — таблиця розподілу задач
    retro.md                — протокол ретроспективи
  sprint2/
    README.md              — огляд спринта 2, посилання на Jira-спринт
    task-board.xlsx         — експорт таблиці задач з Jira
    requirements-defects.xlsx — реєстр дефектів, аркуші «Спірні» / «Рецензування»
    retro-sprint2.md        — шаблон і протокол ретроспективи
  sprint3/
    README.md              — зміст досьє v3, посилання на трекер, статистика, покриття «вимога → TC»
    test-cases.xlsx        — аркуші «Test cases», «Coverage», «Review»
    execution-log.xlsx     — аркуші «Run 1», «Environment»
    attachments/           — знімки до звітів про дефекти (BUG-<номер>-1.png)
    task-board.md          — таблиця розподілу задач
    retro.md               — протокол ретроспективи Start / Stop / Continue
  sprint4/ … sprint5/     — додаються в наступних спринтах
```

Звіти про дефекти спринта 3 — у [GitHub Issues з міткою `sprint:3`](https://github.com/oleksandragorbonos-maker/tzap/issues?q=label%3Asprint%3A3).

## Команда

| ПІБ | GitHub | Поточна роль (С3) |
|---|---|---|
| Горбонос Олександра | [@oleksandragorbonos-maker](https://github.com/oleksandragorbonos-maker) | КТ — Керівник тестування |
| Тимчак Анастасія | [@tymna](https://github.com/tymna) | ІТ — Інженер з тестування |
| Дрєпіна Анастасія | [@anastasijdrepina-design](https://github.com/anastasijdrepina-design) | СМ — Скрам-майстер / рецензент |
| Коротка Ксенія | [@kseniia602](https://github.com/kseniia602) | ТА — Тест-аналітик |

Повна таблиця ротації ролей на спринти 1–5 — у [docs/team/charter.md](docs/team/charter.md), розд. 1.2. На початку кожного спринту оновлюється лише стовпець «Поточна роль» вище.
