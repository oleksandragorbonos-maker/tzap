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
  sprint4/
    README.md              — огляд спринта 4: ролі, результат прогону Surefire, дефекти модуля
    coverage.xlsx          — аркуші «Coverage», «Review», «Зведення», «Питання»
    equivalence-boundary.md — класи еквівалентності і граничні значення BL-вимог
    surefire-reports/      — протокол запуску Surefire (юніт- і UI-тести), скріншоти
    task-board.md          — таблиця розподілу задач, години, Definition of Done
    retro.md               — протокол ретроспективи Start / Stop / Continue
  sprint5/
    regression-plan.md     — план регресії v2: домовленості Д1–Д9, розподіл виконавців, критерії go/no-go К1–К5
    regression-log.xlsx    — аркуші «Журнал» (91 рядок), «Підсумок», «Environment»
    traceability-matrix.xlsx — матриця простежуваності «вимога → кейс → дефект» і метрики C, E, PR, A
    surefire-v1/           — копія протоколу Surefire до прогону («до»: 142 запуски, 28 failures, 1 error)
    surefire-v2/           — протокол Surefire після прогону на v2 («після»), порівняння по запусках
    defects-regression.md  — звіти про регресійні дефекти версії 2
    quality-report.docx    — звіт про якість продукту (+ `quality-report.pdf` для здачі)
    presentation.pdf       — презентація до захисту досьє
    review-checklist.md    — рецензування артефактів спринта: зауваження → виконавець → статус і дата
    retro-semester.md      — ретроспектива спринта і семестру Start / Stop / Continue
    tasks.md               — таблиця розподілу задач, журнал standup, Definition of Done
qa-shopline/               — Maven-проєкт (JUnit 5 + Selenium) з модулем ShopLine v1 і автотестами
```

Звіти про дефекти спринта 3 — у [GitHub Issues з міткою `sprint:3`](https://github.com/oleksandragorbonos-maker/tzap/issues?q=label%3Asprint%3A3), дефекти модуля спринта 4 — з міткою [`sprint:4`](https://github.com/oleksandragorbonos-maker/tzap/issues?q=label%3Asprint%3A4). Регресійний дефект версії 2 — з міткою [`sprint:5`](https://github.com/oleksandragorbonos-maker/tzap/issues?q=label%3Asprint%3A5).

## Команда

| ПІБ | GitHub | Поточна роль (С5) |
|---|---|---|
| Горбонос Олександра | [@oleksandragorbonos-maker](https://github.com/oleksandragorbonos-maker) | СМ — Скрам-майстер / рецензент |
| Тимчак Анастасія | [@tymna](https://github.com/tymna) | КТ — Керівник тестування |
| Дрєпіна Анастасія | [@anastasijdrepina-design](https://github.com/anastasijdrepina-design) | ТА — Тест-аналітик |
| Коротка Ксенія | [@kseniia602](https://github.com/kseniia602) | ІТ — Інженер з тестування |

Повна таблиця ротації ролей на спринти 1–5 — у [docs/team/charter.md](docs/team/charter.md), розд. 1.2. На початку кожного спринту оновлюється лише стовпець «Поточна роль» вище.
