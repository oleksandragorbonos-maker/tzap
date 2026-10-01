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
  sprint3/ … sprint5/     — додаються в наступних спринтах за структурою sprint1/sprint2
```

## Команда

| ПІБ | GitHub | Поточна роль (С2) |
|---|---|---|
| Горбонос Олександра | [@oleksandragorbonos-maker](https://github.com/oleksandragorbonos-maker) | СМ — Скрам-майстер / рецензент |
| Тимчак Анастасія | [@tymna](https://github.com/tymna) | ТА — Тест-аналітик |
| Дрєпіна Анастасія | [@anastasijdrepina-design](https://github.com/anastasijdrepina-design) | ІТ — Інженер з тестування |
| Коротка Ксенія | [@kseniia602](https://github.com/kseniia602) | КТ — Керівник тестування |

Повна таблиця ротації ролей на спринти 1–5 — у [docs/team/charter.md](docs/team/charter.md), розд. 1.2. На початку кожного спринту оновлюється лише стовпець «Поточна роль» вище.
