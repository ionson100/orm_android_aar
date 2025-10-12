

### Еще одна ORM для андроида
Написана java 11.\
minSdk = 24\
compileSdk = 36\
namespace = "com.bsr.bitnicorm"\
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
Не стоит забывать, что это негативно сказывается на быстродействии работы с базой, (вставка обновление). \
Есть возможность хранить объекты в виде JSON, маркируя поле аннотацией ```@MapJsonColumn```, но тут могут возникнуть проблемы с приведением типа. \
Хотя этот тип сериализации расширяет возможности работы с базой:
[тынц](https://www.sqlitetutorial.net/sqlite-json/)

> [!NOTE]\
> Внимание: Если вы хотите пользоваться атрибутом ```@MapJsonColumn```, у вас должна быть подключенная зависимость:
> ```implementation("com.google.code.gson:gson:2.13.2")```, версию можете выбрать сами.

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
@MapWhere("name not null and age > 20")
class SimpleTable{
    
    @MapPrimaryKey //or @MapPrimaryKeName("id")
    public int id=-1;

    @MapIndex
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
    @MapJsonColumn
    public MyClass myClass= new MyClass()


}

```

### Mapping Annotation
#### @MapTable
Уровень класса. Обязательная. Устанавливает связь класса с таблицей, имя таблицы берется такое же, как имя класса.
#### @MapTableName(name table)
Уровень класса. Обязательная. Устанавливает связь класса с таблицей, имя таблицы вводится как строка в аннотацию.


> [!NOTE]\
> Внимание: Без аннотаций MapTable и MapTableName никакой связи не произойдет, \
а при использовании этого класса, получится ошибка.

#### @MapAppendCommandCreateTable(string)
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
#### @MapWhere(line condition without the word where)
Уровень класса. В этом аттрибуте указывается условие которое будет автоматически подставляться\
в условия всех выборок из базы данных (при использовании сессии), даже если вы не укажете их при запросе.\
А так же, в ```session.count```
####  @MapPrimaryKey
Уровень поля класса. Обязательный. Устанавливает связь с первичным ключом таблицы, имя поля таблицы\
устанавливается как имя поля класса.
####  @MapPrimaryKeyName(name column)
Уровень поля класса. Обязательный. Устанавливает связь с первичным ключом таблицы, имя поля таблицы\
прописывается в аннотации.
#### @MapColumn
Уровень поля класса. Обязательный если вы хотите проецировать поле в таблицу.\
Название поля таблицы будет таким же как поле класса.

#### @MapColumnName(name column)
Уровень поля класса. Обязательный если вы хотите проецировать поле в таблицу.\
Название поля таблицы указывается в аннотации.

#### @MapJsonColumn
Поля класса помеченные этой аннотацией, будут отображаться в безе данных как текст в формате json.

> [!NOTE]\
> Внимание: Если вы хотите пользоваться атрибутом ```@MapJsonColumn```, у вас должна быть подключенная зависимость:
> ```implementation("com.google.code.gson:gson:2.13.2")```, версию можете выбрать сами.




####  @MapColumnType("TEXT UNIQUE")
Если вам не нравится как орм подбирает тип поля таблицы и значение по умолчанию, вы можете определить свое значение.
#### @MapIndex
При попытке или создании таблицы будет выполнен скрипт создания индекса по полю с условием если его нет.
Если вы хотите создать индекс по вум или более полям, вам стоит воспользоваться: ```MapAppendCommandCreateTable```\
или ``` session.executeSQL```
#### @MapForeignKey("FOREIGN KEY (email) REFERENCES SimpleTable (email)")
Будет вставлена строка создания ForeignKey при формировании скрипта запроса на создания таблицы.

#### @MapColumnReadOnly
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

#### < T > void insert(@NonNull T item)

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
#### < T > int update(@NonNull T item)
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
#### < T > int delete(@NonNull T item);
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
#### < T > int deleteRows(@NonNull Class<T> aClass);
Удаляет все записи в таблице, возвращает количество удаленных записей

```java
 ISession session = Configure.getSession;
 MyTable table = new MyTable();
 session.insert(table);
 var res = session.deleteRows(MyTable.class);
```
####  < T >int deleteRows(@NonNull Class<T> aClass, String where, Object... objects);
Удаляет записи из таблицы по условию (где возраст меньше 10), возвращает количество удаленных записей

```java
 ISession session = Configure.getSession;
 var res = session.deleteRows(MyTable.class,"age < 10");
```
#### < T > int updateRows(@NonNull Class<T> aClass, @NonNull PairColumnValue columnValues, String where, Object... objects)
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
####  < T > void insertBulk(@NonNull List<T> tList);
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
####   < T > List<T> getList(@NonNull Class<T> aClass);
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

#### < T, D extends Object > List<D> getListSelect(@NonNull Class<T> aClass,@NonNull String columnName, String where, Object... objects);

Позволяет получить список одиночных значений по определенному полю. \
Пример: Дай мне список электронных адресов, где адрес не равен null, я отправлю им всем сообщения.

```java
ISession session = Configure.getSession;
List<String> list= session.getLisSelect(MyTable.class,"email","email not null");
int count = list.size();
```
####  < T > T firstOrDefault(@NonNull Class<T> aClass, String where, Object... objects)
Иногда нужно получить один объект по условию, п при его отсутствии получить null.
Этим тут и займемся. \
Пример: Дай мне только первую запись, где возраст больше 149 при сортировке по имени.

```java
ISession session = Configure.getSession;
MyTable poz = session.firstOrDefault(MyTable.class,"age > ? order by name",149);
```
Тут понятно, может быть только один поц.

####   < T > T first(@NonNull Class<T> aClass, String where, Object... objects) throws Exception
Пытается получить первую запись, по условию, если такой записи не существует, выкидывается исключение.
Пример: Дай мне только первую запись, где возраст больше 149 при сортировке по имени.

```java
ISession session = Configure.getSession;
MyTable poz = session.firstOrDefault(MyTable.class,"age > ? and email not null order by name",200);
```
Тут все понятно, будет исключение, столько живут только черепахи, но у них нет электронного адреса.

####  < T > T singleOrDefault(@NonNull Class<T> aClass, String where, Object... objects)
Иногда возникает желание получить уникальны объект по условию, то есть, он существует в таблице в количестве одного или вообще не существует. \
Вернет уникальный объект или null;
Мы спешим к вам:
```java
ISession session = Configure.getSession;
MyTable poz = session.singleOrDefault(MyTable.class,"age > ? and email not null order by name",200);
```
####  < T > T single(@NonNull Class<T> aClass, String where, Object... objects) throws Exception
Возвращает уникальны объект по условию. Вернет уникальный объект или выкинет исключение;


####  < T > List < Object > distinctBy(@NonNull Class<T> aClass, @NonNull String columnName, String where, Object... objects)
Возвращает distinct значения по одному полю таблицы базы данных. \
Пример: Дай мне distinct возраста, что встречаются в таблице, где возраст больше 18 и отсортируй результат по возрастанию

```java
ISession session = Configure.getSession;
List<Integer> list = session.distinctBy(MyTable.class,"age","age > ?  order by age",18);
```
#### < T > Map < Object, List< T > > groupBy(@NonNull Class<T> aClass, @NonNull String columnName, String where, Object... objects)

Получает группированный результат по одному полю таблицы, по условию. \
Возвращает словарь, где ключ: уникальное значение поля, а value: список строк, которые содержат в себе это уникальное значение.

```java
ISession session = Configure.getSession;
Map<Integer>,List<MyTable>>   result = session.groupBy(MyTable.class,"age",null);
```


