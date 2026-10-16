# Аудит `bitnicorm` (ORM для Android)

Дата: 2026-10-16. Версия в репозитории: **1.2.6** (`minSdk 24`, `com.android.library`, Java 11).

Цель: оценить безопасность и надёжность ORM и выполнимость шифрования локальной БД
(точечное шифрование полей / SQLCipher).

---

## Резюме

- ORM небольшой и аккуратный: значения в запросах **параметризуются** (`?` + bind-args в
  `Utils.parametrize`), транзакции используются, сессия `Closeable`.
- SQLite-поверхность маленькая — **6 файлов, ~11 импортов** `android.database.*`
  (`DataBaseHelper`, `Configure`, `ISession`, `Logger`, `Utils`, `UtilsCompound`).
  Это делает перевод на SQLCipher **реалистичным** (не «на неделю»).
- Главные риски связаны не с самим ORM, а с (1) отсутствием шифрования БД и
  (2) Java-десериализацией BLOB-полей.

---

## Находки

### 🟠 HIGH — Java-десериализация BLOB-полей
`UtilsHelper.deserializeByte` (`UtilsHelper.java:92`) читает BLOB через `ObjectInputStream.readObject()`.
Вызывается при чтении «сложных» полей: `UtilsCompound.java:265,288,394`.
Если файл БД окажется подконтролен атакующему (adb на debuggable-сборке, восстановление
подменённого бэкапа, root), появляется **deserialization gadget** — потенциальный RCE в контексте приложения.
- **Фикс:** не хранить Java-сериализацию (перейти на Gson/`Map`/PARCELABLE-подобный формат), либо
  хотя бы шифровать/подписывать блоб (тогда подмена невалидна), либо валидировать класс перед чтением
  (`ObjectInputFilter`). `ObjectInputFilter` — самое быстровнедряемое.

### 🟠 HIGH — БД не зашифрована
Локальная SQLite (`DataBaseHelper` → стандартный `android.database.sqlite`) без шифрования.
При root/debuggable-сборке/выгрузке читаются координаты и контент приватных маршрутов,
облачные ссылки, позиции воспроизведения.
- **Фикс:** SQLCipher (цельная БД) **или** шифрование сериализованных полей в
  `serializeByte/deserializeByte`. Подробнее — раздел «Шифрование».

### 🟡 MEDIUM — Значения чувствительных данных в логах
`Configure.IsWriteLog` (статический флаг, включается конструктором) включает логирование,
которое печатает **значения**: `Utils.getStringInsert`/`getStringUpdate` (`Utils.java:88,99`),
`Logger.printSql` (`Logger.java:27`).
- **Фикс:** не включать `isWriteLog` в релизе; не логировать значения (только имена колонок/параметры);
  сделать флаг `final`/build-config.

### 🟡 MEDIUM — Сырые SQL-фрагменты в API
`where(where, ...)`, `orderBy(columnName)`, `selectExpression(expression)`, `rawSqlSelect(sql)`,
`executeSQL(sql)`, `execSQLRaw(sql)`, `getListFree(sql)`, `getListSelect(sql)` принимают готовые
SQL-строки и подставляют их как есть (значения — через `?`, но **идентификаторы/выражения — нет**:
`ScopedValue.java:63-80,237-242`).
- **Риск:** injection возможен, если вызывающий код кладёт в эти строки пользовательский ввод.
- **Фикс:** документировать «никогда не подставлять пользовательский ввод»; при желании —
  валидировать имена колонок по метаданным таблицы (`CacheMetaData`), а `expression` — ограничить.

### 🟡 MEDIUM — Идентификаторы оборачиваются «вручную»
`Utils.clearStringTrim/clearStringTrimRaw` (`Utils.java:414-422`) тримят скобки/кавычки и оборачивают
в `"`. Для имён из аннотаций это безопасно, но как публичный хелпер — хрупко.
- **Фикс:** использовать эти методы только для доверенных имён (аннотации), не для ввода.

### ⚪ LOW — Логирование через private API
`Logger.printSql` (`Logger.java:30`) лезет в приватное поле `Cursor.mQuery`. Под SQLCipher класс
курсора другой → рефлексия молча падает (уже обёрнуто в try) — просто «немой» лог.
- **Фикс:** логировать переданный SQL, а не `mQuery`.

### ⚪ LOW — `stringToDate` бросает `RuntimeException`
`UtilsHelper.stringToDate` (`UtilsHelper.java:59`) при ошибке парсинга кидает `RuntimeException`.
Не критично, но стоит вернуть `null`/`Optional`.

### ⚪ LOW — Хрупкость по типам
`Utils.getTypeName`/`pizdaticusField` (`Utils.java:214,427`) завязаны на строковые имена типов
(`"java.util.List"`, дескрипторы вида `[SB`). Любое новое поле-коллекция требует правки switch.

### ⚪ LOW — `serialVersionUID` у сериализуемых моделей
Сериализация в BLOB чувствительна к изменению класса модели (на практике уже ловили
`InvalidClassException` в приложении). После перехода с Java-сериализации (см. HIGH) риск уходит.

### ✅ Что сделано правильно
- Значения всегда параметризуются (`?`), а не склеиваются — базовой SQL-инъекции нет.
- Транзакции (`beginTransaction/setTransactionSuccessful/endTransaction`), `insertBulk` чанками по 500.
- `ISession` — `Closeable`, есть `IsAlive`.
- `serializeJson` (Gson) как альтернатива Java-сериализации уже присутствует (`@MapColumnJson`).

---

## Шифрование: оценка выполнимости

### Вариант A — точечное шифрование сериализованных полей (легко)
Всё «сложное» (коллекции, объекты, `@MapColumnJson`) проходит через **две** функции:
`UtilsHelper.serializeByte` / `deserializeByte` (+ `serializeJson`/`deserializeJson`) — используется в
`UtilsContentValues.java:166,176` (запись), `InnerInsertBulk.java:194,212` (bulk), `UtilsCompound.java:265,276,288,389,394` (чтение).
- **Идея:** добавить в ORM точку расширения (интерфейс `ICrypto`/`ISerializer`), которую приложение
  подключает через `Configure`; при `null` — поведение как сейчас.
- Плюсы: минимальные изменения, без нативных либ, закрывает основной приватный payload
  (`MRoute.features`: координаты/названия/облачные ссылки) и снимает gadget-риск (блоб станет нечитаемым без ключа).
- Минусы: колонки-скаляры (UUID/String) остаются плейнтекстом.

### Вариант B — SQLCipher, цельная БД (средне)
Поверхность маленькая — **6 файлов**:
1. `DataBaseHelper` (`SQLiteOpenHelper`/`SQLiteDatabase`/`SQLException`) — перевести на
   `net.zetetic.database.sqlcipher.*`; `SQLiteDatabase.loadLibs(context)`; открывать с passphrase.
2. `Configure`, `ISession`, `Logger`, `Utils`, `UtilsCompound` — заменить импорты `android.database.*`
   (`Cursor`, `SQLiteDatabase`, `SQLiteQuery`) на соответствующие типы SQLCipher.
3. `ISession` отдаёт наружу `SQLiteDatabase` (`getSqLiteDatabaseForReadable/Writable`) — **изменение
   публичного API** (у приложения проверить, используется ли).
4. Ключ: 32 случайных байта → шифровать ключом Android Keystore → хранить в SharedPreferences;
   при старте расшифровывать и передавать SQLCipher.
5. Миграция: существующую плейнтекст-БД либо пересоздать, либо `sqlcipher_export(...)`.
6. Переиздать AAR, поднять версию, ограничить ABI (нативные `.so`, +3–7 МБ к APK).
- Минусы: изменение публичного API, размер APK, тесты на устройстве; и SQLCipher **не шифрует файлы** кэша.

### Рекомендация
1. Сначала **Вариант A** (точечное шифрование blob-полей) — быстро и закрывает приватный payload,
   плюс снижает gadget-риск.
2. Затем, при необходимости, **Вариант B** (SQLCipher) поверх A — как «цельная» шифрованная БД.
3. Параллельно — не логировать значения и не хранить Java-сериализацию без фильтра.

---

## Мелкие улучшения (по желанию)
- `Configure.IsWriteLog` → `final`, включать только в debug.
- Заменить `Logger.printSql` на логирование исходного SQL.
- Обернуть BLOB-чтение в `ObjectInputFilter` (быстрый барьер против gadget).
- Задокументировать в README_RU, что `where/orderBy/expression` — это raw SQL (только доверенные данные).
