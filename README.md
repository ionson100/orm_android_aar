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
| [@MapForeignKey](#@MapForeignKey)                             |                                                         |
| [@MapColumnReadOnly](#@MapColumnReadOnly)                     | [firstOrDefault](#firstOrDefault)                       |
| [@MapTableReadOnly](#@MapTableReadOnly)                       | [first](#first)                                         |
|                                                               | [singleOrDefault](#singleOrDefault)                     |
| [class Persistent](#@Persistent)                              | [distinctBy](#distinctBy)                               |
| [Interface IEventOrm](#IEventOrm)                             | [groupBy](#groupBy)                                     |
| [Interface IUserType](#IUserType)                             | [executeScalar](#executeScalar)                         |
|                                                               | [executeSQL](#executeSQL)                               |
|                                                               | [any](#any)                                             |
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
|                                                               | [](#)                                                   |
|                                                               | [](#)                                                   |


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
####  < T >int deleteRows(@NonNull Class<T> aClass, String where, Object... objects)
Удаляет записи из таблицы по условию (где возраст меньше 10), возвращает количество удаленных записей

```java
 ISession session = Configure.getSession;
 var res = session.deleteRows(MyTable.class,"age < 10");
```
#### < T > int updateRows(@NonNull Class<T> aClass, @NonNull PairColumnValue columnValues, String where, Object... objects) <a name="updateRows"></a>
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
####  < T > List<T> getList(@NonNull Class<T> aClass, String where, Object... objects);

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

####  < T,D > List< D > getList(@NonNull Class<T> aClassFrom,@NonNull Class<D> aClassTo, String where, Object... objects)
Иногда нужно получить типизированный список не всего объекта, где например 100 полей, а только часть его, конечно
можно выкрутиться с наследованием, а можно и такой перегрузкой, где тип класса aClassTo, это простой тип без аннотаций,
основное требование, поля этого типа должны соответствовать названиям колонок в таблице описанной типом: aClassFrom. \
Пример:
```javas
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
static class TableCustom{
    public double aDouble;
    public List<String> stringList1;
    public List<String> stringList2;
}
@Test
public void TestList() {
    initConfig();
    ISession session = Configure.getSession();
    try {
        session.dropTableIfExists(TableMain.class);
        session.createTableIfNotExists(TableMain.class);

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    for (int i = 0; i < 5; i++) {
        TableMain t=new TableMain();
        t.aDouble=0.67D;
        t.stringList1.add("simple");
        t.stringList2.add("simple");
        session.insert(t);
    }
    List<TableCustom> list=session.getList(TableMain.class,TableCustom.class," aDouble > 0" );
    assertTrue(list.size()==5);
    list.forEach(tableCustom -> {
        assertTrue(tableCustom.aDouble==0.670D);
        assertTrue(tableCustom.stringList1.get(0).equals("simple"));
        assertTrue(tableCustom.stringList2.get(0).equals("simple"));
    });
}
```
```sql
CREATE TABLE IF NOT EXISTS "TestListCustom$TableMain" (
"anInt"  INTEGER  PRIMARY KEY, 
"aDouble" REAL DEFAULT 0, 
"stringList1" BLOB, 
"stringList2" BLOB); 
CREATE INDEX IF NOT EXISTS TestListCustom$TableMain_aDouble ON "TestListCustom$TableMain" ("aDouble");

SELECT "aDouble","stringList1","stringList2" FROM "TestListCustom$TableMain" WHERE  aDouble > 0;
```
> [!NOTE]\
> Внимание:Поля помеченные в главной таблице как ```@MapColumnJson``` не должны участвовать в целевой, конвертация произойдет
> с ошибкой, если вам очень надо это поле, сделайте его строкой, и сами конвертируйте в объект.


#### < T, D extends Object > List<D> getListSelect(@NonNull Class<T> aClass,@NonNull String columnName, String where, Object... objects); <a name="getListSelect"></a>

Позволяет получить список одиночных значений по определенному полю. \
Пример: Дай мне список электронных адресов, где адрес не равен null, я отправлю им всем сообщения.

```java
ISession session = Configure.getSession;
List<String> list= session.getLisSelect(MyTable.class,"email","email not null");
int count = list.size();
```
####  < T > T firstOrDefault(@NonNull Class<T> aClass, String where, Object... objects) <a name="firstOrDefault"></a>
Иногда нужно получить один объект по условию, п при его отсутствии получить null.
Этим тут и займемся. \
Пример: Дай мне только первую запись, где возраст больше 149 при сортировке по имени.

```java
ISession session = Configure.getSession;
MyTable poz = session.firstOrDefault(MyTable.class,"age > ? order by name",149);
```
Тут понятно, может быть только один поц.

####   < T > T first(@NonNull Class<T> aClass, String where, Object... objects) throws Exception <a name="first"></a>
Пытается получить первую запись, по условию, если такой записи не существует, выкидывается исключение.
Пример: Дай мне только первую запись, где возраст больше 149 при сортировке по имени.

```java
ISession session = Configure.getSession;
MyTable poz = session.firstOrDefault(MyTable.class,"age > ? and email not null order by name",200);
```
Тут все понятно, будет исключение, столько живут только черепахи, но у них нет электронного адреса.

####  < T > T singleOrDefault(@NonNull Class<T> aClass, String where, Object... objects) <a name="singleOrDefault"></a>
Иногда возникает желание получить уникальны объект по условию, то есть, он существует в таблице в количестве одного или вообще не существует. \
Вернет уникальный объект или null;
Мы спешим к вам:
```java
ISession session = Configure.getSession;
MyTable poz = session.singleOrDefault(MyTable.class,"age > ? and email not null order by name",200);
```
####  < T > T single(@NonNull Class<T> aClass, String where, Object... objects) throws Exception <a name="single"></a>
Возвращает уникальны объект по условию. Вернет уникальный объект или выкинет исключение;


####  < T > List < Object > distinctBy(@NonNull Class<T> aClass, @NonNull String columnName, String where, Object... objects) <a name="distinctBy"></a>
Возвращает distinct значения по одному полю таблицы базы данных. \
Пример: Дай мне distinct возраста, что встречаются в таблице, где возраст больше 18 и отсортируй результат по возрастанию

```java
ISession session = Configure.getSession;
List<Integer> list = session.distinctBy(MyTable.class,"age","age > ?  order by age",18);
```
#### < T > Map < Object, List< T > > groupBy(@NonNull Class<T> aClass, @NonNull String columnName, String where, Object... objects) <a name="groupBy"></a>

Получает группированный результат по одному полю таблицы, по условию. \
Возвращает словарь, где ключ: уникальное значение поля, а value: список строк, которые содержат в себе это уникальное значение.

```java
ISession session = Configure.getSession;
Map<Integer>,List<MyTable>>   result = session.groupBy(MyTable.class,"age",null);
```
#### Object executeScalar(@NonNull String sql, Object... objects) <a name="executeScalar"></a>
#### Object executeScalar(@NonNull String sql);
Это типовые функции, которые есть в любой ОРМ, возвращают одиночное значение запакованное в Object. \
Кто в теме, это первая строка курсора с индексом колонки 0.
```java
ISession session = Configure.getSession();
String sql="Select count (*)  from "+session.getTableName(MyTable.class);
int count= (int) session.executeScalar(sql);
```
ISession session = Configure.getSession;

#### void executeSQL(@NonNull String sql, Object... objects) <a name="executeSQL"></a>

Это типовая функция, которая есть в любой ОРМ, просто выполняет запрос и не возвращает результат, можно применять параметры. \
как правило применяется при старте приложения, после инициализации конфигурации, или после создания таблицы.
```java
ISession session = Configure.getSession();
session.executeSQL("CREATE INDEX IF NOT EXISTS test_name ON 'MyTable' ('name');",null);

```

#### < T > boolean any(@NonNull Class<T> aClass, String where, Object... objects) <a name="any"></a>
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

####  <T> int save(@NonNull T item) <a name="save"></a>
Этот метод может применяться для вставки или обновления объекта ассоциированного со строкой таблицы, класс типа этого объекта
должен реализовывать класс ```Prsistent```, орм сама решает, вставлять объект или обновлять.

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
#### Маркировка объектов через наследование class Persistent <a name="Persistent"></a>
Одна из проблем при создании орм, сохранять сведения об объекте, в нашем случае это сведения
откуда получен объект, из базы или нет, в разных орм - разный подход, например создание прокси объекта на основе данного типа (java, C#), маркировка объекта специальным атрибутом(C#)и т.д.
В нашем случае, это наследования объекта, описывающего табличную сущность, от class Persistent.
В этом классе всего одно булево поле ``` boolean isPersistent;```, которое характеризует происхождение объекта (true-получен из базы false-создан на клиенте и в базе не сохранен)
на основе этого можно принимать решение, что делать с объектом при помещении его в метод ```save```, вставлять или обновлять, в то же время, это поле решает: 
выкинуть ли исключение при вставке в базу объекта полученного ранее из базы, удаление или обновление локально созданного объекта.\
Применение этого наследования не догма, вы можете отказаться, и сами следить откуда получен объект, в этом случае - вы не сможете применять метод ```save```.



