# 📱 Shopping & Notes List App

# 🛒📝 Списки покупок и заметок в одном приложении

## 🧩 О приложении

Shopping & Notes List App — современное Android-приложение, объединяющее списки покупок и заметки в
одном минималистичном интерфейсе.

Поддерживает динамические темы, кастомные цветовые палитры, Material You и гибкие настройки внешнего
вида через экран Settings.

Приложение написано на Kotlin с использованием архитектуры MVVM и базы данных Room.

---

## 💡 Зачем был создан проект

Этот проект был создан для практики:

- масштабируемой Android-архитектуры
- сложных взаимодействий с RecyclerView
- UI-тестирования с использованием Espresso
- динамического переключения тем
- современных принципов Material Design

---

## ✨ Возможности

- 📝 Создание, редактирование и управление заметками
- 🛒 Создание и управление списками покупок
- 🎨 Выбор цвета заметок
- 🗂 Разные режимы отображения (Grid / Linear)
- 🌗 Светлая / тёмная / системная тема
- 🎨 Кастомные цветовые палитры + Material You
- ⚙️ Экран настроек (PreferenceFragmentCompat)
- 🔄 Мгновенное применение темы (без перезапуска)
- 💳 In-App Billing (отключение рекламы)
- 💾 Локальная база данных (Room + Coroutines)

---

## 🖼 Скриншоты

- Скриншоты опубликованы в английской версии

---

## 🏗 Архитектура

Приложение использует упрощённую многослойную архитектуру:

### Слои

- **UI слой** — Activity и Fragment
- **ViewModel** — только UI-логика (без доступа к БД)
- **Repository** — единый источник данных и бизнес-логика
- **Room (DAO)** — слой данных

### Зачем нужен Repository?

- Разделение ответственности
- Масштабируемость
- Удобство тестирования
- Готовность к добавлению API

### Основные принципы:

- Разделение ответственности (Separation of Concerns)
- Единый источник данных (Repository)
- Минимум логики в UI

---

## ⚙️ Технологии

- **Kotlin**
- **Android Jetpack**
    - ViewModel
    - LiveData / Flow
    - Room
    - Navigation Component
    - PreferenceFragmentCompat
- **Material Design 3 (Material You)**
- **RecyclerView + DiffUtil**
- **Coroutines**
- **Google Play Billing**

---

## 🎨 Система тем

- Несколько предустановленных цветовых палитр
- Поддержка Material You (Dynamic Colors)
- Централизованное управление темой через:
    - `BaseActivity`
    - `ThemeManager`

---

## 📁 Структура проекта

```text
com.example.shoppingAndNotesListApp
├── core
│   ├── billing
│   ├── preferences
│   └── utils
│
├── data
│   ├── db
│   │   ├── dao
│   │   └── database
│   ├── model
│   └── repository
│
├── ui
│   ├── activities
│   ├── fragments
│   ├── adapters
│   ├── dialogs
│   └── viewmodel
│
└── settings
```

---

## 🧭 Навигация

- **MainActivity**
    - Списки покупок → `ShopListFragment`
    - Заметки → `NoteFragment`
    - Настройки → `SettingsActivity`

Экран настроек реализован как отдельная Activity для упрощения работы с темами.

---

## 🧪 Тестирование

Проект содержит end-to-end UI тесты на Espresso:

- RecyclerViewActions
- навигация между экранами
- создание и редактирование заметок
- создание shopping lists
- удаление элементов
- переключение тем и настроек
- проверка стабильности пользовательских сценариев

Основные тесты:

- `FullUserFlowNotesTest`
- `FullUserFlowSettingsTest`
- `FullUserFlowTestShopList`

---

## 🚀 Запуск проекта

### Требования

- Android Studio (Giraffe или новее)
- Gradle 8+
- minSdk 26
- targetSdk 34+

### Установка

git clone https://github.com/nolikstolikxxx/shopping-notes-app
Открой проект в Android Studio и запусти Run ▶

## 🔜 Планы на будущее

* ☁️ Синхронизация (Firebase / Google Drive)
* 🔍 Поиск по заметкам
* 📦 Группировка списков
* 🖼 Улучшенный редактор заметок
* 📱 Виджеты

## 📄 MIT Лицензия
