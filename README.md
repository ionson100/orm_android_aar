### Еще одна ORM для андроида
[Быстрый старт](#start)

| Маппинг                                                       | ISession                                                |
|---------------------------------------------------------------|---------------------------------------------------------|
| [@MapTable @MapTableName](#@MapTable)                         | [insert](#insert)                                       |
| [@MapAppendCommandCreateTable](#@MapAppendCommandCreateTable) | [update](#update)                                       |
| [@MapTableWhere](#@MapTableWhere)                             | [delete](#delete)                                       |
| [@MapPrimaryKey @MapPrimaryKeyName](#@MapPrimaryKey)          | [deleteRows](#deleteRows)                               |
| [@MapColumn @MapColumnName](#@MapColumn)                      | [updateRows](#updateRows)                               |
| [@MapColumnJson](#@MapColumnJson)                             | [insertBulk](#insertBulk)                               |
| [@MapColumnType](#@MapColumnType)                             | [getList](#getList)                                     |
| [@MapColumnIndex](#@MapColumnIndex)                           | [getListSelect](#getListSelect)                         |
| [@MapForeignKey](#@MapForeignKey)                             | [getListFree](#getListFree)                             |
| [@MapColumnReadOnly](#@MapColumnReadOnly)                     | [firstOrDefault](#firstOrDefault)                       |
| [@MapTableReadOnly](#@MapTableReadOnly)                       | [first](#first)                                         |
|                                                               | [singleOrDefault](#singleOrDefault)                     |
| [class Persistent](#@Persistent)                              | [distinctBy](#distinctBy)                               |
| [Interface IEventOrm](#IEventOrm)                             | [groupBy](#groupBy)                                     |
| [Interface IUserType](#IUserType)                             | [executeScalar](#executeScalar)                         |
| [Получение не полной записи из таблицы](#312)                 | [executeSQL](#executeSQL)                               |
| [Как подключить в проект](#312312)                            | [any](#any)                                             |
|                                                               | [tableExists](#tableExists)                             |
|                                                               | [getTableName](#getTableName)                           |
|                                                               | [createTable](#createTable)                             |
|                                                               | [createTableIfNotExists](#createTableIfNotExists)       |
|                                                               | [dropTableIfExists](#dropTableIfExists)                 |
|                                                               | [getPath](#getPath)                                     |
|                                                               | [IsAlive](#IsAlive)                                     |
|                                                               | [SqLiteDatabaseForWritable](#SqLiteDatabaseForWritable) |
|                                                               | [SqLiteDatabaseForReadable](#SqLiteDatabaseForReadable) |
|                                                               | [getContentValues](#getContentValues)                   |
|                                                               | [getContentValuesForUpdate](#getContentValuesForUpdate) |
|                                                               | [save](#save)                                           |
|                                                               | [objectFiller](#objectFiller)                           |



Написана java 11.\
minSdk = 24\
compileSdk = 36\
namespace = "com.bitnic.bitnicorm"\
Исполнена в стиле [Hibernate](https://www.geeksforgeeks.org/java/hibernate-tutorial/).\
Инициализация конфигурации -> инициализация скрытой фабрики сессии на SQLiteOpenHelper -> получение сессии\
кеширования нет.\
Сессия: единица работы с базой данных, реализует [Closable](https://www.geeksforgeeks.org/java/closeable-interface-in-java/)
#### Условия и соглашения по использованию:
Поле первичного ключа обязательно. Оно только одно.\
Класс должен иметь открытый конструктор без параметров. \

##### Партикулярно воспринимаются следующие типы:
long, short, byte, int, Byte, Long, Short. Integer как INTEGER, \
float, double, Float, Double как REAL, \
boolean, Boolean как BOOL, \
Date как DATE, хранится строкой, \
String, UUID, BigDecimal как TEXT, \
остальные типы воспринимаются как массив байтов BLOB \
Массивы, списки словари, все сериализуется - как массив байтов по правилам интерфейса:
[Serializable](https://www.geeksforgeeks.org/java/serialization-and-deserialization-in-java/)
и
[Externalizable](https://www.geeksforgeeks.org/java/externalizable-interface-java/) \
Если вы будете использовать в таблице свой тип, то этот тип должен реализовывать вышесказанные интерфейсы.\
Не стоит забывать, что это негативно сказывается на быстродействии работы с базой, (получение вставка обновление). \
Есть возможность хранить объекты в виде JSON, маркируя поле аннотацией ```@MapColumnJson```, но тут могут возникнуть проблемы с приведением типа. \
Хотя этот тип сериализация расширяет возможности работы с базой:
[тынц](https://www.sqlitetutorial.net/sqlite-json/)

> [!NOTE]\
> Внимание: Если вы хотите пользоваться атрибутом ```@MapColumnJson```, у вас должна быть подключенная зависимость:
> ```implementation("com.google.code.gson:gson:2.13.2")```, версию можете выбрать сами.

###  Быстрый старт <a name="start"></a>

```java
@MapTable
static class MyTable{
    @MapPrimaryKey
    public long id;
    @MapColumn
    public String name;
    @MapColumn
    public int age;
    @MapColumn
    public String email;
}

new Configure("myfile.sqlite",3,this,true); //start app

try (ISession session = Configure.getSession()) {
    session.beginTransaction();
    try {
        if (session.tableExists(MyTable.class) == false) {
            session.createTable(MyTable.class);
        }
        session.commitTransaction();
    } catch (Exception e) {
        throw new Exception(e);
    } finally {
        session.endTransaction();
    }
    List<MyTable> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        list.add(new MyTable());
    }

    session.insertBulk(list);
    List<MyTable> result = session.getList(MyTable.class);
} catch (Exception e) {
    throw new RuntimeException(e);
}
```

Пример сериализации c использованием [Serializable](https://www.geeksforgeeks.org/java/serialization-and-deserialization-in-java/)
и
[Externalizable](https://www.geeksforgeeks.org/java/externalizable-interface-java/)

```java
    class Children implements Serializable {
        public String name = "Leo";
        public int age = 3;
    }
    class ExternalizableDemo implements Externalizable {
        public String firstName="ion";
        public String lastName="Ionow";
        public int age=18;

        public ExternalizableDemo() {
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            if (in.readBoolean()) {
                firstName = in.readUTF();
            }
            if (in.readBoolean()) {
                lastName = in.readUTF();
            }
            age = in.readInt();
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            if (firstName == null) {
                out.writeBoolean(false);
            } else {
                out.writeBoolean(true);
                out.writeUTF(firstName);
            }
            if (lastName == null) {
                out.writeBoolean(false);
            } else {
                out.writeBoolean(true);
                out.writeUTF(lastName);
            }
            out.writeInt(age);
        }
    }

    @MapTableName("my_user")
    static class MyUser {
    
        @MapPrimaryKey
        public long id;
        
        @MapColumn
        public String name = "simple";
        
        @MapColumn
        public int age = 15;
        
        @MapColumn
        public String email = "ion@df.com";
        
        @MapColumn
        public List<Children> childrenList = new ArrayList<>();

        @MapColumn
        public ExternalizableDemo  externalizable;
    }

```
```sql
 CREATE TABLE IF NOT EXISTS "my_user" (
 "id"  INTEGER  PRIMARY KEY,
 "age" INTEGER DEFAULT 0 ,
 "childrenList" BLOB,
 "email" TEXT,
 "externalizable" BLOB,
 "name" TEXT);
```
```javascript
ISession session = Configure.getSession();
session.dropTableIfExists(MyUser.class);
try {
    session.createTableIfNotExists(MyUser.class);
} catch (Exception e) {
    throw new RuntimeException(e);
}
var table=new MyUser();
table.childrenList.add(new Children());
table.externalizable=new ExternalizableDemo();

session.insert(table);
var list = session.getList(MyUser.class);
list.forEach(myTable1 -> {
    myTable1.childrenList.forEach(children -> {
        Log.i("------------", children.name + "@" + children.age);
    });
    Log.i("------------",myTable1.externalizable.firstName+"@"+myTable1.externalizable.lastName+"@"+myTable1.externalizable.age);
});
assertTrue(list.size() == 1);
assertTrue(list.get(0).id == 1L);
```









Пример возможных реализации:

```java
@MapTable //or @MapTableName("simple_table")
@MapAppendCommandCreateTable("CREATE INDEX IF NOT EXISTS test_name ON 'SimpleTable' ('name');")
@MapTableWhere("name not null and age > 20")
class SimpleTable{
    
    @MapPrimaryKey //or @MapPrimaryKeName("id")
    public int id=-1;

    @MapColumnIndex
    @MapColumn // @MapColumnName("name")
    public String name;

    @MapColumn
    public int age;


    @MapColumn
    @MapColumnType("TEXT UNIQUE")
    @MapForeignKey("FOREIGN KEY (email) REFERENCES SimpleTable (email)")
    public String email;

    @MapColumn
    @MapColumnReadOnly
    @MapColumnType("TEXT DEFAULT 12-434")
    public String index;
    
    @MapColumn
    @MapColumnJson
    public MyClass myClass= new MyClass()
}
```

### Mapping Annotation

#### @MapTable <a name="@MapTable"></a>
Уровень класса. Обязательная. Устанавливает связь класса с таблицей, имя таблицы берется такое же, как имя класса.
#### @MapTableName(name table)
Уровень класса. Обязательная. Устанавливает связь класса с таблицей, имя таблицы вводится как строка в аннотацию.


> [!NOTE]\
> Внимание: Без аннотаций MapTable и MapTableName никакой связи не произойдет, \
а при использовании этого класса, получится ошибка.


#### @MapTableReadOnly <a name="@MapTableReadOnly"></a>
Классы типов помеченные этой аннотацией предназначены только для просмотра содержимого таблицы.\
Нельзя создавать таблицы на основе типов, объекты нельзя: вставлять, обновлять, удалять из таблицы.

```java
@MapTable
@MapTableReadOnly
public class Part  {
    @MapPrimaryKey
    public int id;
    @MapColumn
    public String name1;
}
static public class Parent extends Part{
    @MapColumn
    String name2;
}
```

#### @MapAppendCommandCreateTable(string) <a name="@MapAppendCommandCreateTable"></a>
Позволяет указать скрипт, который выполнится при создании таблицы.
> [!NOTE]\
> Внимание: Эту аннотацию не стоит применять при создании таблицы через создание Configure (new Configure), \
> а так же через команду ```createTableIfNotExists``` в чистом виде, без проверки на существовании таблицы,
> дело в том что скрипт будет вызываться всегда, даже если таблица уже создана.

Где ее стоит применять.(создание таблиц под контролем):

```java

 ISession session = Configure.getSession();
 session.beginTransaction();
 try {
     if (!session.tableExists(SimpleTable.class)) {
         session.createTable(SimpleTable.class);
         //session.execSQLRaw("script",null);
         List<SimpleTable>  list=new ArrayList<>();
         for (int i = 0; i < 10; i++) {
             list.add(new SimpleTable());
         }
         session.insertBulk(list); //  adding 10 rows
     }
     session.commitTransaction();
 } catch (Exception e) {
     throw new RuntimeException(e);
 } finally {
     session.endTransaction();
     session.close();
 }
```
#### @Table(line condition without the word where) <a name="@MapTableWhere"></a>
Уровень класса. В этом аттрибуте указывается условие которое будет автоматически подставляться\
в условия всех выборок из базы данных (при использовании сессии), даже если вы не укажете их при запросе.\
А так же, в ```session.count```
####  @MapPrimaryKey <a name="@MapPrimaryKey"></a>
Уровень поля класса. Обязательный. Устанавливает связь с первичным ключом таблицы, имя поля таблицы\
устанавливается как имя поля класса.
####  @MapPrimaryKeyName(name column)
Уровень поля класса. Обязательный. Устанавливает связь с первичным ключом таблицы, имя поля таблицы\
прописывается в аннотации.
#### @MapColumn <a name="@MapColumn"></a>
Уровень поля класса. Обязательный если вы хотите проецировать поле в таблицу.\
Название поля таблицы будет таким же как поле класса.

#### @MapColumnName(name column)
Уровень поля класса. Обязательный если вы хотите проецировать поле в таблицу.\
Название поля таблицы указывается в аннотации.

#### @MapColumnJson <a name="@MapColumnJson"></a>
Поля класса помеченные этой аннотацией, будут отображаться в безе данных как текст в формате json.

> [!NOTE]\
> Внимание: Если вы хотите пользоваться атрибутом ```@MapColumnJson```, у вас должна быть подключенная зависимость:
> ```implementation("com.google.code.gson:gson:2.13.2")```, версию можете выбрать сами.




####  @MapColumnType("TEXT UNIQUE") <a name="@MapColumnType"></a>
Если вам не нравится как орм подбирает тип поля таблицы и значение по умолчанию, вы можете определить свое значение.
#### @MapColumnIndex <a name="@MapColumnIndex"></a>
При попытке или создании таблицы будет выполнен скрипт создания индекса по полю с условием если его нет.
Если вы хотите создать индекс по вум или более полям, вам стоит воспользоваться: ```MapAppendCommandCreateTable```\
или ``` session.executeSQL```
#### @MapForeignKey("FOREIGN KEY (email) REFERENCES SimpleTable (email)") <a name="@MapForeignKey"></a>
Будет вставлена строка создания ForeignKey при формировании скрипта запроса на создание таблицы.

#### @MapColumnReadOnly <a name="@MapColumnReadOnly"></a>
Поля класса помеченные этой аннотацией, не будут участвовать в запросе на вставку и модификацию записи. \
Пример: Таблица, поле у которой dateCreate, указывает на дату создания записи, ее нельзя модифицировать, и она заполняется базой данных.

```java
    @MapTableName("t_23_1"
    static class Table22{
        @MapPrimaryKey
        UUID uuid=UUID.randomUUID();
        
        @MapColumn
        int count=3;

        @MapColumn
        @MapColumnType("DATE DEFAULT CURRENT_TIMESTAMP")
        @MapColumnReadOnly
        public Date dateCreate;
    }

```

### Использование
При старте приложения нужно создать конфигурацию, где указать: имя файла базы или полный путь к нему, версию базы, и контекст приложения,
а так же опционально, писать ли в лог запросы к базе.\
Три конструктора:

```java
Configure(String dataBaseName, int version, Context context);
Configure(String dataBaseName, int version, Context context, boolean isWriteLog);
Configure(String dataBaseName, int version, Context context, List<Class> classList, boolean isWriteLog);

```
```List<Class> classList``` - это список типов классов на основе которых будут созданы таблицы в базе автоматически.\
Таблицы создаются в контексте единой трансакции, и если будет ошибка, всё создание откатится.\
Как правило, создание происходить при старте приложения.\
Пример со списком:
```java
 List<Class> classList=new ArrayList<>();
 classList.add(MyTable.class);
 new Configure("db.sqlite",3,appContext,classList,true);
```
Пример без списка:
```java
 new Configure("db.sqlite",3,appContext,true);
 //or new Configure("db.sqlite",3,appContext);
```
Теперь в любом месте приложения можно получить сессию и работать с ней. \
Пример точечной работы:
```java
  try (ISession session = Configure.getSession()) {
          
          //do work
            
   } catch (IOException e) {
      throw new RuntimeException(e);
   }

```
При создании в activities, сессию создаем в ```onCreate ``` закрываем в ```onDestroy``` \
Объект сессии: ```ISession``` - можно передавать в метод как параметр.

Создание конфигурации через конструктор со списком типов на создание таблиц, я наверное буду считать устаревшим, \
я рекомендую создать класс с закрытым конструктором Starter, в статическом методе run, производить создание таблиц
под контролем, через проверку таблиц на существование. В контексте единой трансакции.

### Давайте поговорим что реализовано в ```ISession```

#### < T > void insert(@NonNull T item) <a name="insert"></a>

Этот метод позволяет вставить объект как запись в базу данных. \
Он ничего не возвращаете, в случае ошибки, при вставке, возникнет исключение.
> [!NOTE]\
> Внимание: Если вы используете поле первичного ключа, как цифровой типы, орм считает что это авто инкрементные поля,
> и эти данные в ставке не участвую. \
> Но при вставке, (если у дачно) объект получит реальное значение ключа из базы данных. \
> По существу, будет сделано два запроса, (Insert and SELECT last_insert_rowid()) в контексте внутренней трансакции. \
> Если у вас будет своя трансакция, она заменит внутреннею.

Пример:
```java
 ISession session = Configure.getSession;
 MyTable table = new MyTable();
 session.insert(table);
```
#### < T > int update(@NonNull T item)<a name="update"></a>
Метод обновляет запись в базе данных, обновление происходит по значению первичного ключа. \
Обновление происходит по все полям. \
В случае успеха вернется 1, 0 запись не обновлена. \
Из-за чего может вернуться 0, скорее всего не будет найдена запись с первичным ключом. \
Используйте по возможности UUID.
Пример:
```java
 ISession session = Configure.getSession;
 MyTable table = new MyTable();
 session.insert(table);
 table = session.firstOrDefault(MyTable.class,"id = ?",table.id);
 var res = session.update(table);
```
#### < T > int update(@NonNull T item,String appendWhere, Object... parameters);
Метод обновляет запись в базе данных, обновление происходит по значению первичного ключа, и добавочным ограничением, с возможностью использовать параметры. \
Оптимистическое обновление. \
Обновление происходит по все полям. \
В случае успеха вернется 1, 0 запись не обновлена. \
Из-за чего может вернуться 0, скорее всего не будет найдена запись с первичным ключом и дополнительными условиями. \
```java
@MapTable
class TableAppend{ 
    @MapPrimaryKey
    public long id;
    @MapColumn
    public String name;
    @MapColumn
    public Date date= new Date();
    @MapColumn
    public UUID uuid= UUID.randomUUID();
    @MapColumn
    public BigDecimal bigDecimal= new BigDecimal("1111111");
}
ISession session=Configure.getSession();
session.insert(new TableAppend());
var o=session.firstOrDefault(TableAppend.class,null);
o.name="11";
var res=session.update(o," date = ? and uuid=? and bigDecimal = ?",o.date,o.uuid,o.bigDecimal);
assertTrue(res==1);

```
#### < T > int delete(@NonNull T item);<a name="delete"></a>
Метод удаляет запись в базе данных, удаление происходит по значению первичного ключа. \
В случае успеха вернется 1, 0 запись не удалена. \
Из-за чего может вернуться 0, скорее всего не будет найдена запись с первичным ключом на удаление. \
```java
 ISession session = Configure.getSession;
 MyTable table = new MyTable();
 session.insert(table);
 table = session.firstOrDefault(MyTable.class,"id = ?",table.id);
 var res = session.delete(table);
```
#### < T > int deleteRows(@NonNull Class<T> aClass); <a name="deleteRows"></a>
Удаляет все записи в таблице, возвращает количество удаленных записей

```java
 ISession session = Configure.getSession;
 MyTable table = new MyTable();
 session.insert(table);
 var res = session.deleteRows(MyTable.class);
```
####  < T >int deleteRows(@NonNull Class<T> aClass, String where, Object... parameters)
Удаляет записи из таблицы по условию (где возраст меньше 10), возвращает количество удаленных записей

```java
 ISession session = Configure.getSession;
 var res = session.deleteRows(MyTable.class,"age < 10");
```
#### < T > int updateRows(@NonNull Class<T> aClass, @NonNull PairColumnValue columnValues, String where, Object... parameters) <a name="updateRows"></a>
Обновляет записи в таблице, где возраст меньше 10, делает у них новое поле name, и меняет возраст на 22, звучит конечно абсурдно,
но для примера сгодится, возвращает количество обновлённых записей.
```java
 ISession session = Configure.getSession;
 var res = session.updateRows(MyTable.class,new PairColumnValue()
                .put("name","name_new")
                .put("age",22),"age < ?",10);
```
Что бы обновить все записи, без условия:
```java
 ISession session = Configure.getSession;
 var res = session.updateRows(MyTable.class,new PairColumnValue()
                .put("name","name_new")
                .put("age",22),null);
```
####  < T > void insertBulk(@NonNull List<T> tList) <a name="insertBulk"></a>
Позволяет производить пакетную вставку.
При ошибке возникнет исключение.
> [!NOTE]\
> Внимание: Если ваши типы имеют авто инкрементные первичные ключи, то после пакетной вставки, поля эти не обновляются,
> в отличии единичного ```insert```. Пустые значения в списке не допускаются

```java
ISession session = Configure.getSession;
List<MyTable> list=new ArrayList<>();
 for (int i = 0; i < 10 ; i++) {
       MyTable myTable=new MyTable();
       list.add(myTable);
 }
 session.insertBulk(list);
```
####  < T > void insertBulk(@NonNull T... object);
Позволяет производить пакетную вставку.
При ошибке возникнет исключение.
> [!NOTE]\
> Внимание: Если ваши типы имеют авто инкрементные первичные ключи, то после пакетной вставки, поля эти не обновляются,
> в отличии единичного ```insert```. Пустые значения в коллекции не допускаются

```java
ISession session = Configure.getSession;
session.insertBulk(new MyTable(),new MyTable(),new MyTable());
```
####   < T > List<T> getList(@NonNull Class<T> aClass) <a name="getList"></a>
Позволяет получить полный типизированный список объектов, ассоциированный с записями таблицы.
При отсутствии результат: пустой список.
```java
ISession session = Configure.getSession;
List<MyTable> list= session.getList(MyTable.class);
int count = list.size();
```
####  < T > List<T> getList(@NonNull Class<T> aClass, String where, Object... parameters);

Позволяет получить типизированный список объектов по условию выборки, ассоциированный с записями таблицы.
При отсутствии результат: пустой список.\
Пример:Дай мне все записи, где возраст больше 18, но меньше 64, (я их отправлю на убой), и отсортируй по имени.
```java
ISession session = Configure.getSession;
List<MyTable> list= session.getList(MyTable.class,"age > 18 and age < 64 order by name" );
int count = list.size();
```
Или равнозначно через параметры:

```java
ISession session = Configure.getSession;
List<MyTable> list= session.getList(MyTable.class,"age > ? and age < ? order by name",18,64 );
int count = list.size();
```
Пример: Дай мне все записи из таблицы, отсортировав по полю name.

```java
ISession session = Configure.getSession;
List<MyTable> list= session.getList(MyTable.class,"1 order by name");
int count = list.size();
```
Пример: Дай мне все записи из таблицы.

```java
ISession session = Configure.getSession;
List<MyTable> list= session.getList(MyTable.class,null);
int count = list.size();
```

Пример: Дай мне 10 записи из таблицы.

```java
ISession session = Configure.getSession;
List<MyTable> list= session.getList(MyTable.class,"1 LIMIT 10");
int count = list.size();
```
> [!NOTE]\
> Внимание: Если вы не хотите пользоваться параметром where, поставьте null, \
> если все же нужно, но не надо учитывать where, поставьте 1 и пишите условие дальше.

#### < T > List<T> getListFree(@NonNull Class<T> aClass,String sql, Object... parameters) <a name="getListFree"></a>
Позволяет получить типизированный список объектов из таблицы на основе пользовательского запроса.
Класс типа может содержать аннотации маппинга, может и не содержать.
Например, вы можете получить только часть полей из таблицы, или получить результат джойна, сформировав сами целевой класс типа. \
Единственное ограничение, трансляция не работает с таблицами, у которых есть поля помеченные аннотацией: ```@MapGolumnJson```, хотя и это можно
обойти, поставив в целевом типе эти поля как строку. \
Требование: Поля название полей в целевом классе типа, должно совпадать с названием колонок в запросе на извлечение, а так жк ожидаемый тип этих полей. \
Пример, как можно вытащить из таблицы только часть полей:
```java
@MapTable
class TableUser{
    @MapPrimaryKey
    public int id;
    @MapColumn
    public String name;
    @MapColumn
    public int age;
}

class TableUserPartial{
    public String name;
    public int age;
}
ISession session = Configure.getSession();
       
for (int i = 0; i <  10 ; i++) {
    TableUser user=new TableUser();
    session.insert(user);
}

String sql="select name, age FROM "+session.getTableName(TableUser.class);

List<TableUserPartial> list= session.getListFree(TableUserPartial.class,sql);

```



#### < T, D extends Object > List<D> getListSelect(@NonNull Class<T> aClass,@NonNull String columnName, String where, Object... parameters); <a name="getListSelect"></a>

Позволяет получить список одиночных значений по определенному полю. \
Пример: Дай мне список электронных адресов, где адрес не равен null, я отправлю им всем сообщения.

```java
ISession session = Configure.getSession;
List<String> list= session.getLisSelect(MyTable.class,"email","email not null");
int count = list.size();
```
####  < T > T firstOrDefault(@NonNull Class<T> aClass, String where, Object... parameters) <a name="firstOrDefault"></a>
Иногда нужно получить один объект по условию, п при его отсутствии получить null.
Этим тут и займемся. \
Пример: Дай мне только первую запись, где возраст больше 149 при сортировке по имени.

```java
ISession session = Configure.getSession;
MyTable poz = session.firstOrDefault(MyTable.class,"age > ? order by name",149);
```
Тут понятно, может быть только один поц.

####   < T > T first(@NonNull Class<T> aClass, String where, Object... parameters) throws Exception <a name="first"></a>
Пытается получить первую запись, по условию, если такой записи не существует, выкидывается исключение.
Пример: Дай мне только первую запись, где возраст больше 149 при сортировке по имени.

```java
ISession session = Configure.getSession;
MyTable poz = session.firstOrDefault(MyTable.class,"age > ? and email not null order by name",200);
```
Тут все понятно, будет исключение, столько живут только черепахи, но у них нет электронного адреса.

####  < T > T singleOrDefault(@NonNull Class<T> aClass, String where, Object... parameters) <a name="singleOrDefault"></a>
Иногда возникает желание получить уникальны объект по условию, то есть, он существует в таблице в количестве одного или вообще не существует. \
Вернет уникальный объект или null;
Мы спешим к вам:
```java
ISession session = Configure.getSession;
MyTable poz = session.singleOrDefault(MyTable.class,"age > ? and email not null order by name",200);
```
####  < T > T single(@NonNull Class<T> aClass, String where, Object... parameters) throws Exception <a name="single"></a>
Возвращает уникальны объект по условию. Вернет уникальный объект или выкинет исключение;


####  < T > List < Object > distinctBy(@NonNull Class<T> aClass, @NonNull String columnName, String where, Object... parameters) <a name="distinctBy"></a>
Возвращает distinct значения по одному полю таблицы базы данных. \
Пример: Дай мне distinct возраста, что встречаются в таблице, где возраст больше 18 и отсортируй результат по возрастанию

```java
ISession session = Configure.getSession;
List<Integer> list = session.distinctBy(MyTable.class,"age","age > ?  order by age",18);
```
#### < T > Map < Object, List< T > > groupBy(@NonNull Class<T> aClass, @NonNull String columnName, String where, Object... parameters) <a name="groupBy"></a>

Получает группированный результат по одному полю таблицы, по условию. \
Возвращает словарь, где ключ: уникальное значение поля, а value: список строк, которые содержат в себе это уникальное значение.

```java
ISession session = Configure.getSession;
Map<Integer>,List<MyTable>>   result = session.groupBy(MyTable.class,"age",null);
```
#### Object executeScalar(@NonNull String sql, Object... parameters) <a name="executeScalar"></a>
#### Object executeScalar(@NonNull String sql);
Это типовые функции, которые есть в любой ОРМ, возвращают одиночное значение запакованное в Object. \
Кто в теме, это первая строка курсора с индексом колонки 0.
```java
ISession session = Configure.getSession();
String sql="Select count (*)  from "+session.getTableName(MyTable.class);
int count= (int) session.executeScalar(sql);
```
ISession session = Configure.getSession;

#### void executeSQL(@NonNull String sql, Object... parameters) <a name="executeSQL"></a>

Это типовая функция, которая есть в любой ОРМ, просто выполняет запрос и не возвращает результат, можно применять параметры. \
как правило применяется при старте приложения, после инициализации конфигурации, или после создания таблицы.
```java
ISession session = Configure.getSession();
session.executeSQL("CREATE INDEX IF NOT EXISTS test_name ON 'MyTable' ('name');",null);

```

#### < T > boolean any(@NonNull Class<T> aClass, String where, Object... parameters) <a name="any"></a>
#### < T > boolean any(@NonNull Class<T> aClass)

Это типовые функции, которые есть в любой ОРМ, позволяют проверить существуют ли записи в таблице, без условия и с условием.
```java
ISession session = Configure.getSession();
boolean  b=session.any(MyTable.class," name is null");
assertFalse(b);
```

#### boolean tableExists(@NonNull Class<T> aClass) <a name="tableExists"></a>
#### boolean tableExists(@NonNull String tableName)
Проверяет базу данных на существование таблицы. Если таблица найдена возвращает true, если нет - false.
getTableName
```java
Isession session = Configure.getSesion();
boolean exist = session.tableExists(MyTable.class);
exist = session.tableExists(session.getTableName(MyTable.class));
```
#### < T > String getTableName(@NonNull Class<T> aClass) <a name="getTableName"></a>
Возвращает название таблицы, ассоциированное с типом класса.
```java
Isession session = Configure.getSesion();
String sql="SELCT * FROM "+session.getTableName(MyTable.class);
```

#### < T > void  createTable(@NonNull Class<T> aClass) throws Exception <a name="createTable"></a>
Создает таблицу в базе данных, если таблица существует или нарушена логика построения - выбрасывает ошибку.
```java
Isession session = Configure.getSesion();
session.createTable(MyTable.class);
```
#### < T > void createTableIfNotExists(@NonNull Class<T> aClass) throws Exception <a name="createTableIfNotExists"></a>
Создает таблицу в базе данных, если нарушена логика построения - выбрасывает ошибку.

```java
Isession session = Configure.getSesion();
session.createTableIfNotExists(MyTable.class);
```
#### < T > void dropTableIfExists(@NonNull Class<T> aClass) <a name="dropTableIfExists"></a>
#### void dropTableIfExists(@NonNull String tableName)
Удаляет таблицу если она существует.
```java
Iession session = Configure.getSesion();
session.dropTableIfExists(MyTable.class);
String tableName=session.getTableName(MyTable.class);
session.dropTableIfExists(fableName);
```
####  String getPath() <a name="getPath"></a>
Возвращает полный путь к файлу базы данных.

#### boolean IsAlive() <a name="IsAlive"></a>
Проверяет, зарыта ли сессия?
#### SQLiteDatabase SqLiteDatabaseForWritable() <a name="SqLiteDatabaseForWritable"></a>
Получает объект SQLiteDatabase в контексте сессии, через него можно управлять базой данных минуя орм.\
Пример вставки строки:
```java
 @MapTable
static class TableMain {
    @MapPrimaryKey
    public int anInt;
    @MapColumnIndex
    @MapColumn
    public double aDouble;
    @MapColumn
    public List<String> stringList1=new ArrayList<>();
    @MapColumn
    //@MapColumnJson
    public List<String> stringList2=new ArrayList<>();
}
ISession session = Configure.getSession();

TableMain t = new TableMain();
t.aDouble=0.67D;
t.stringList1.add("simple");
t.stringList2.add("simple");

ContentValues contentValues=session.getContentValues(t);
String tableName=session.getTableName(TableMain.class);

SQLiteDatabase sql=session.getSqLiteDatabaseForWritable();

sql.insert(tableName,null,contentValues);
List<TableMain> list=session.getList(TableMain.class);
assertTrue(list.size()==1);
list.forEach(tableCustom -> {
    assertTrue(tableCustom.aDouble==0.670D);
    assertTrue(tableCustom.stringList1.get(0).equals("simple"));
    assertTrue(tableCustom.stringList2.get(0).equals("simple"));
});
```

#### SQLiteDatabase SqLiteDatabaseForReadable() <a name="SqLiteDatabaseForReadable"></a>
Получает объект SQLiteDatabase в контексте сессии, через него можно управлять базой данных минуя орм.\
Пример получения курсора на выборку с условием:
```java
    @MapTable
    static class TableUser {
        @MapPrimaryKey
        public int id;
        @MapColumn
        String name="name";
        @MapColumn
        int age=18;
        @MapColumn
        String email="ion@qw.com";
    }

    @Test
    public void TestReadable(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        new Configure("myfile.sqlite",3,appContext);
        ISession session = Configure.getSession();
        try {
            session.dropTableIfExists(TableUser.class);
            session.createTableIfNotExists(TableUser.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < 5; i++) {
            session.insert(new TableUser());
        }
        String tableName=session.getTableName(TableUser.class);

        List<TableUser> list = new ArrayList<>();
        SQLiteDatabase sql=session.getSqLiteDatabaseForReadable();
        Cursor cursor=sql.query(tableName,new String[]{"name","age","email","id"},"name not null",null,null,null,null);

        try {

            if (cursor.moveToFirst()) {
                do {
                    TableUser instance = new TableUser();
                    instance.name=cursor.getString(0);
                    instance.age=cursor.getInt(1);
                    instance.email=cursor.getString(2);
                    instance.id=cursor.getInt(3);
                    list.add(instance);
                } while (cursor.moveToNext());
            }

        }finally {
            cursor.close();
        }
        assertTrue(list.size()==5);
    }
```
####  < T > ContentValues getContentValues(@NonNull T item)  <a name="getContentValues"></a>
Получение объекта ```ContentValues``` . Тип ```item``` должен реализовывать аннотации маппинга. \
Объект ```ContentValues``` получает данные по всем полям ассоциированных с таблицей.
```java
ISession session=Configure.getSession();
ContentValues contentValues=sesssion.getContentValues(new MyTable);
```


#### < T > ContentValues getContentValuesForUpdate(@NonNull Class<T> aClass,PairColumnValue columnValues) <a name="getContentValuesForUpdate"></a>
Получение объекта ```ContentValues``` . Тип ```item``` должен реализовывать аннотации маппинга. \
Объект ```ContentValues``` получает данные по всем полям введенных пользователем в PairColumnValue, как правило, может использоваться 
при обновлении записи в таблице по условию равенства первичного ключа.
```java
ISession session=Configure.getSession();
ContentValues  contentValues =session.getContentValuesForUpdate(TableUser.class,new PairColumnValue()
                .put("name","newName")
                .put("age",20)
                .put("email","ion100@df.com"));
```

####  < T > int save(@NonNull T item) <a name="save"></a>
Этот метод может применяться для вставки или обновления объекта ассоциированного со строкой таблицы, класс типа этого объекта
должен реализовывать класс ```Prsistent```, орм сама решает, вставлять объект или обновлять. \
Поле: ```boolean isPersistent;``` орм заполняет сама(вставка, обновление, извлечение)

```java


@MapTable
static class TableUser extends Persistent {
    @MapPrimaryKey
    public int id;
    @MapColumn
    String name = "name";
    @MapColumn
    int age = 18;
    @MapColumn
    String email = "ion@qw.com";
}
// ...

ISession session = Configure.getSession();
TableUser tableUser = new TableUser();

session.save(tableUser)//insert

tableUser =sessiom.firstOrDefault(TableUser .class);
tableUser,age=30;

session.save(tableUser);//update
```
#### < T > T objectFiller(Class<T> aClass, Cursor cursor) throws Exception <a name="objectFiller"></a>
Вспомогательный метод, применяется при обходе курсора, возвращает заполненный из курсора объект. \
Класс типа объекта может быть произвольным типом, или типом ассоциированным с таблицей, условие: название полей типа, или колонок таблицы (аннотации) \
должны совпадать с названием колонок в строке Sql запроса на выборку. \
Тип должен иметь открытый конструктор без параметров. Возможна ошибка при создании типа и при приведении полей. \

Пример:
```java 
@MapTable
class TableUser { 
    @MapPrimaryKey
    public int id;
    @MapColumn
    String name="name";
    @MapColumn
    int age=18;
    @MapColumn
    String email="ion@qw.com";
}
class TestFillingPart {
    public String name;
    public int age;
}

ISession session = Configure.getSession();
for (int i = 0; i < 5; i++) {
    session.insert(new TableUser());
}
List<TestFillingPart> list=new ArrayList<>();
var sql="select name, age from "+session.getTableName(TableUser.class)+";";
      
try (Cursor cursor = session.execSQLRaw(sql)) {
    
    if (cursor.moveToFirst()) {
        do {
                   TestFillingPart userPart=  session.objectFiller(TestFillingPart.class, cursor);
                    list.add(userPart);
        } while (cursor.moveToNext());
    }
}catch (Exception e){
    throw new RuntimeException(e);
}
```

#### < T > void objectFiller(Cursor cursor, T instance) throws Exception
Вспомогательный метод, применяется при обходе курсора, заполняет из курсора ранее созданный объект. \
Класс типа объекта может быть произвольным типом или типом, ассоциированным с таблицей, единственное условие: название полей типа, или колонок таблицы (аннотации)
должны совпадать с названием колонок в строке Sql запроса на выборку. 
Возможна ошибка при приведении типа полей с полученным типом из курсора.\

Пример:
```java 
@MapTable
class TableUser { 
    @MapPrimaryKey
    public int id;
    @MapColumn
    String name="name";
    @MapColumn
    int age=18;
    @MapColumn
    String email="ion@qw.com";
}
class TestFillingPart {
    public String name;
    public int age;
}

ISession session = Configure.getSession();
for (int i = 0; i < 5; i++) {
    session.insert(new TableUser());
}
List<TestFillingPart> list=new ArrayList<>();
var sql="select name, age from "+session.getTableName(TableUser.class)+";";
      
try (Cursor cursor = session.execSQLRaw(sql)) {
    
    if (cursor.moveToFirst()) {
        do {
                   TestFillingPart testFillingPart=new TestFillingPart();
                    session.objectFiller(cursor,testFillingPart);
                    list.add(userPart);
        } while (cursor.moveToNext());
    }
}catch (Exception e){
    throw new RuntimeException(e);
}
```
### interface IEventOrm <a name="IEventOrm"></a>
Классы типов, которые реализуют этот интерфейс, могут получать вызовы при манипуляции данными таблицы на клиенте. \
Через этот вызов, можно осуществлять контроль за действием и состоянием объекта в контексте модификации таблицы.
```java
class TableActionOrm implements IEventOrm {
    @MapPrimaryKey
    public UUID id=UUID.randomUUID();
    @MapColumn
    public String name;
    public int action;
    
    @Override
    public void beforeUpdate() {
    }

    @Override
    public void afterUpdate() {
    }

    @Override
    public void beforeInsert() {
    }

    @Override
    public void afterInsert() {
    }

    @Override
    public void beforeDelete() {
    }

    @Override
    public void afterDelete() {
    }
}
```

### interface IUserType <a name="IUserType"></a>

Если поля класса являются типом, который реализует этот интерфейс, то это поле размещается  в таблице  как  строковое поле. \
Контроль за формирования строки и заполнения тела объекта из строки, реализует пользователь.

```java
public class UserClass implements IUserType
{
    public String name;
    public int age;
    @Override
    public void initBody(String str) {
        Gson gson=new Gson();
        UserClass inner= gson.fromJson(str, UserClass.class);
        name=inner.name;
        age=inner.age;
    }
    @Override
    public String getString() {
        Gson gson=new Gson();
       return gson.toJson(this);
    }
}



@MapTableName("user_23")
public class TableUser {
    @MapPrimaryKeyName("_id")
    public int id;

    @MapColumnName("user")
    public UserClass userClass;

    @MapColumn
    @MapColumnType("TEXT NOT NULL UNIQUE")
    public String address;
}
```

### interface IUserType
### Маркировка объектов через наследование class Persistent <a name="Persistent"></a>
Одна из проблем при создании орм, сохранять сведения об объекте, в нашем случае это сведения
откуда получен объект, из базы или нет, в разных орм - разный подход, например создание прокси объекта на основе данного типа (java, C#), маркировка объекта специальным атрибутом(C#)и т.д.
В нашем случае, это наследования объекта, описывающего табличную сущность, от class Persistent.
В этом классе всего одно булево поле ``` boolean isPersistent;```, которое характеризует происхождение объекта (true-получен из базы false-создан на клиенте и в базе не сохранен)
на основе этого можно принимать решение, что делать с объектом при помещении его в метод ```save```, вставлять или обновлять, в то же время, это поле решает: 
выкинуть ли исключение при вставке в базу объекта полученного ранее из базы, удаление или обновление локально созданного объекта.\
Применение этого наследования не догма, вы можете отказаться, и сами следить откуда получен объект, в этом случае - вы не сможете применять метод ```save```.

#### Получение типизированных списков по не полной записи из таблицы <a name="312"></a>
Наверное самый простой способ решить эту проблему, через использование суб классов. \
Давайте разложим целевой класс на суб классы:
```java

@MapTableName("main")
@MapTableReadOnly
class BaseMain{
    @MapPrimaryKeyName("_id")
    int id;

}
@MapTableReadOnly
class SubMain extends BaseMain {
    @MapColumn
    public String name;
    @MapColumn
    public int age;
}
class TableMain extends SubMain {
    @MapColumn
    public String email;
}

ISession session=Configure.getSession();
try {
    session.dropTableIfExists(TableMain.class);
    session.createTableIfNotExists(TableMain.class);
} catch (Exception e) {
    throw new RuntimeException(e);
}
for (int i = 0; i < 5; i++) {
    TableMain main=new TableMain();
    main.age=10;
    main.name="Leo"+i;
    main.email="leo123@.leo.com";
    session.insert(main);
}
var list1= session.getList(TableMain.class,"1 order by _id");
var list2= session.getList(SubMain.class,"1 order by _id");
var list3= session.getList(BaseMain.class,"1 order by _id");
assertTrue((list1.size()+list2.size()+list3.size())==5*3);
for (int i = 0; i <5; i++) {
    assertEquals(list1.get(i).id,list2.get(i).id);
    assertEquals(list2.get(i).id,list3.get(i).id);
    assertEquals(list1.get(i).age,list2.get(i).age);
    assertEquals(list1.get(i).name,list2.get(i).name);
}
/*When attempting to modify, an error occurs because the class is closed with the annotation:  @MapTableReadOnly */
//session.insert(list2.get(1)); //error table read only
//session.update(list2.get(1)); //error table read only
//session.delete(list2.get(1)); //error table read only
//session.deleteRows(SubMain.class);//error table read only
//session.updateRows(SubMain.class,new PairColumnValue().put("name","newName"),null);//error table read only
```
> [!NOTE]\
> Обратите внимание, все суб классы я пометил аннотацией: ```@MapTableReadOnly```. 
> Это предохраняет мою таблицу,если я буду модифицировать таблицу через объекты этих суб классов или указывая тип суб классов. \
> При попытке модификации таблицы - я получу ошибку. 

Еше один способ, получение типизированного списка через метод [getListFree](#getListFree), нужно подготовить запрос на выборку,
это может быть JOIN или UNION SELECT, основное требование, что бы целевой тип имел поля, с названием,  совпадающими с полями запроса,
или поля помеченные аннотацией "@MapColumnName". \
Ну и как последний вариант, получить Cursor, и обходить его самому.
При обходе курсора можно использовать методом заполнения: [objectFiller](#objectFiller)
Стоит остановиться на применений в функции параметра запроса (where) и параметра - Object... parameters.
В параметре where писать слово  ```Where``` не нужно, в случае - если where не нужен, можно поставить:1 или 1=1. \
Пример: "id= 2", "id=2 and name not null order by name LIMIT 10", "1 LIMIT 10", "1 order by name" и т.д. \
параметр parameters, транслируется в массив строк, очередность записи,
должна соответствовать очередность применения параметра (?) в строке условия запроса.

### Как подключить в проект. <a name="312312"></a>





