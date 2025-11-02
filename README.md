### Another ORM for Android
License: https://www.apache.org/licenses/LICENSE-2.0.txt
[Quick Start](#start)

| Mapping                                                       | ISession                                                |
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
| [Getting a Session](#session)                                 | [singleOrDefault](#singleOrDefault)                     |
| [class Persistent](#@Persistent)                              | [distinctBy](#distinctBy)                               |
| [Interface IEventOrm](#IEventOrm)                             | [groupBy](#groupBy)                                     |
| [Interface IUserType](#IUserType)                             | [executeScalar](#executeScalar)                         |
| [Fluent Interface](#Fluent)                                   | [executeSQL](#executeSQL)                               |
| [Getting a Partial Record from a Table](#312)                 | [any](#any)                                             |
| [How to connect to a project](#312312)                        | [tableExists](#tableExists)                             |
| [Asynchronous operations](#async)                             | [getTableName](#getTableName)                           |
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



Written in Java 11.

minSdk = 24
compileSdk = 36
namespace = "com.bitnic.bitnicorm"
Implemented in the [Hibernate](https://www.geeksforgeeks.org/java/hibernate-tutorial/) style.

Configuration initialization -> initialization of the hidden session factory on SQLiteOpenHelper -> obtaining the session
no caching.

Session: the unit of work with the database, implements [Closable](https://www.geeksforgeeks.org/java/closeable-interface-in-java/)
#### Terms and conventions for use:
The primary key field is required. There is only one.
The class must have a public constructor without parameters.

##### The following types are treated as private:
long, short, byte, int, Byte, Long, Short. Integer as INTEGER, LocalDateTime \
float, double, Float, Double as REAL, \
boolean, Boolean as BOOL, \
Date as DATE, stored as a string, \
String, UUID, BigDecimal as TEXT, \

### Quick Start <a name="start"></a>

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
    //or
    List<MyTable> result = session.query(MyTable.class).toList()
    
} catch (Exception e) {
    throw new RuntimeException(e);
}
```

Example of serialization using [Serializable](https://www.geeksforgeeks.org/java/serialization-and-deserialization-in-java/)
And
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
```java
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


Example of possible implementation:

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
Class level. Required. Associates the class with a table; the table name is the same as the class name.
#### @MapTableName(name table)
Class level. Required. Associates the class with a table; the table name is entered as a string in the annotation.

> [!NOTE]\
> Warning: Without the MapTable and MapTableName annotations, no association will occur,\
and using this class will result in an error.

#### @MapTableReadOnly <a name="@MapTableReadOnly"></a>
Type classes marked with this annotation are intended only for viewing table contents.\
Tables cannot be created based on the types; objects cannot be inserted, updated, or deleted from the table.

```java
@MapTable
@MapTableReadOnly
public class Part {
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
Allows you to specify a script to run when creating a table.
> [!NOTE]\
> Warning: This annotation should not be used when creating a table using Configure (new Configure),\
> or using the ```createTableIfNotExists``` command in its pure form, without checking for the table's existence,
> because the script will always be called, even if the table has already been created.

Where it should be applied (create tables under control):
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
Class level. This attribute specifies a condition that will be automatically substituted into the conditions of all database selections (when using a session), even if you don't specify them in the query.

Also, in ```session.count```
#### @MapPrimaryKey <a name="@MapPrimaryKey"></a>
Class field level. Required. Establishes a relationship with the table's primary key; the table field name is set as the class field name.
#### @MapPrimaryKeyName(name column)
Class field level. Required. Establishes a relationship with the table's primary key; the table field name is specified in the annotation.
#### @MapColumn <a name="@MapColumn"></a>
Class field level. Required if you want to map the field to the table.
The table field name will be the same as the class field.

#### @MapColumnName(name column)
Class field level. Required if you want to map the field to a table.

The table field name is specified in the annotation.

#### @MapColumnJson <a name="@MapColumnJson"></a>
Class fields marked with this annotation will be displayed in the database as text in JSON format.

> [!NOTE]
> Attention: If you want to use the ```@MapColumnJson``` attribute, you must have the following dependency enabled:
> ```implementation("com.google.code.gson:gson:2.13.2")```. You can choose the version yourself.

#### @MapColumnType("TEXT UNIQUE") <a name="@MapColumnType"></a>
If you don't like how the form selects the table field type and default value, you can define your own. #### @MapColumnIndex <a name="@MapColumnIndex"></a>
When attempting to create a table, the index creation script for the field will be executed, with a condition if it doesn't exist.
If you want to create an index on multiple or more fields, you should use: ```MapAppendCommandCreateTable```\
or ``` session.executeSQL```
#### @MapForeignKey("FOREIGN KEY (email) REFERENCES SimpleTable (email)") <a name="@MapForeignKey"></a>
A ForeignKey creation string will be inserted when generating the table create query script.

#### @MapColumnReadOnly <a name="@MapColumnReadOnly"></a>
Class fields marked with this annotation will not be included in insert and update queries. \
Example: A table with a dateCreate field that specifies the record's creation date cannot be modified and is populated by the database.

```java
@MapTableName("t_23_1"
static class Table22{
@MapPrimaryKey
UUID uuid=UUID.randomUUID();

@MapColumn
int count=3;

@MapColumn
@MapColumnType("DATE  DEFAULT CURRENT_TIMESTAMP")
@MapColumnReadOnly
public Date dateCreate;
}

```

### Usage
When starting the application, you need to create a configuration that specifies: the database file name or full path to it, the database version, and the application context,
and optionally whether to log database queries.
Three constructors:

```java
Configure(String dataBaseName, int version, Context context);
Configure(String dataBaseName, int version, Context context, boolean isWriteLog);
Configure(String dataBaseName, int version, Context context, List<Class> classList, boolean isWriteLog);

```
```List<Class> classList``` is a list of class types based on which tables will be automatically created in the database.\
Tables are created within the context of a single transaction, and if an error occurs, the entire creation is rolled back.\
Typically, creation occurs at application startup.\
Example with a list:
```java
List<Class> classList=new ArrayList<>();
classList.add(MyTable.class);
new Configure("db.sqlite",3,appContext,classList,true);
```
Example without a list:
```java
new Configure("db.sqlite",3,appContext,true);
//or new Configure("db.sqlite",3,appContext);
```
### Getting a session <a name="session"></a>
You can get a session in two ways Types: \
```ISession session=Configure.getSession()```\
- A session is acquired for multiple database operations and the ability to work with transactions. \
  ```ISesssion = Cinfigure.getSessionAutoClose()```
- A session is acquired for a single operation; after execution, the session is automatically closed; transactions cannot be worked with.

Now you can get and work with a session anywhere in the application. \
Example of specific work:
```java
try (ISession session = Configure.getSession()) {

//do work

} catch (IOException e) {
throw new RuntimeException(e);
}

```
When creating an activity, the session is created in ```onCreate``` and closed in ```onDestroy``` \
Session object: ```ISession``` - can be passed to the method as parameter.

Creating a configuration via a constructor with a list of table creation types is probably deprecated.

I recommend creating a class with a private Starter constructor, in a static run method, and creating tables under control by checking for table existence. Within the context of a single transaction.

### Let's talk about what's implemented in ```ISession```

#### < T > void insert(@NonNull T item) <a name="insert"></a>

This method allows you to insert an object as a record into the database.

It does nothing.
```
#### < T > int delete(@NonNull T item);<a name="delete"></a>
This method deletes a record in the database based on the primary key value. \
If successful, 1 will be returned; 0 means the record is not deleted. \
A 0 return value may indicate that a record with the primary key to delete was most likely not found. \
```java
ISession session = Configure.getSession;
MyTable table = new MyTable();
session.insert(table);
table = session.firstOrDefault(MyTable.class,"id = ?",table.id);
var res = session.delete(table);
```
#### < T > int deleteRows(@NonNull Class<T> aClass); <a name="deleteRows"></a>
Deletes all records in the table, returns the number of deleted records.

```java
ISession session = Configure.getSession;
MyTable table = new MyTable();
session.insert(table);
var res = session.deleteRows(MyTable.class);
```
#### < T >int deleteRows(@NonNull Class<T> aClass, String where, Object... parameters)
Deletes records from the table based on a condition (where the age is less than 10), returns the number of deleted records.

```java
ISession session = Configure.getSession;
var res = session.deleteRows(MyTable.class,"age < 10");
```
#### < T > int updateRows(@NonNull Class<T> aClass, @NonNull PairColumnValue columnValues, String where, Object... parameters) <a name="updateRows"></a>
Updates records in the table where the age is less than 10, creates a new name field, and changes the age to 22. Sounds absurd, of course,
but it's good for an example. Returns the number of updated records.
```java
ISession session = Configure.getSession;
var res = session.updateRows(MyTable.class,new PairColumnValue()
.put("name","name_new")
.put("age",22),"age < ?",10);
```
To update all records, without a condition:
```java
ISession session = Configure.getSession;
var res = session.updateRows(MyTable.class,new PairColumnValue()
.put("name","name_new")
.put("age",22),null);
```
#### < T > void insertBulk(@NonNull List<T> tList) <a name="insertBulk"></a>
Allows bulk insertion.
An exception will be raised if an error occurs.
> [!NOTE]\
> Caution: If your types have auto-incrementing primary keys, these fields are not updated after a bulk insert,
> unlike a single ```insert```. Empty values  in the list are not allowed.

```java
ISession session = Configure.getSession;
List<MyTable> list=new ArrayList<>();
for (int i = 0; i < 10 ; i++) {
MyTable myTable=new MyTable();
list.add(myTable);
}
session.insertBulk(list);
```
#### < T > void insertBulk(@NonNull T... object);
Enables bulk insertion.
An exception will be raised if an error occurs.
> [!NOTE]\
> Caution: If your types have auto-incrementing primary keys, these fields are not updated after a bulk insert,
> unlike a single ```insert```. Empty values  are not allowed in the collection.

```java
ISession session = Configure.getSession;
session.insertBulk(new MyTable(),new MyTable(),new MyTable());
```
#### < T > List<T> getList(@NonNull Class<T> aClass) <a name="getList"></a>
Gets a complete typed list of objects associated with table records.
If none, the result is an empty list.
```java
ISession session = Configure.getSession;
List<MyTable> list= session.getList(MyTable.class);
int count = list.size();
```
#### < T > List<T> getList(@NonNull Class<T> aClass, String where, Object... parameters);

Gets a typed list of objects based on the selection criteria associated with table records.
If none, the result is an empty list.
Example: Give me all records where the age is greater than 18 but less than 64 (I'll slaughter them), and sort them by name.
```java
ISession session = Configure.getSession;
List<MyTable> list= session.getList(MyTable.class,"age > 18 and age < 64 order by name" );
int count = list.size();
```
Or equivalently through parameters:

```java
ISession session = Configure.getSession;
List<MyTable> list= session.getList(MyTable.class,"age > ? and age < ? order by name",18,64 );
int count = list.size();
```
Example: Give me all the records from the table, sorted by the name field.

```java
ISession session = Configure.getSession;
List<MyTable> list= session.getList(MyTable.class,"1 order by name");
int count = list.size();
```
Example: Give me all the records from a table.

```java
ISession session = Configure.getSession;
List<MyTable> list= session.getList(MyTable.class,null);
int count = list.size();
```

Example: Give me 10 records from a table.

```java
ISession session = Configure.getSession;
List<MyTable> list= session.getList(MyTable.class,"1 LIMIT 10");
int count = list.size();
```
> [!NOTE]\
> Note: If you don't want to use the where parameter, specify null.
> If you still need it but don't need to consider the where parameter, specify 1 and continue writing the condition.

#### < T > List<T> getListFree(@NonNull Class<T> aClass,String sql, Object... parameters) <a name="getListFree"></a>
Allows you to get a typed list of objects from a table based on a user query.
The type class can

#### < T, D extends Object > List<D> getListSelect(@NonNull Class<T> aClass,@NonNull String columnName, String where, Object... parameters); <a name="getListSelect"></a>
Allows you to get a list of single values  by a specific field. \
Example: Give me a list of email addresses where the address is not null, I'll send messages to them all.

```java
ISession session = Configure.getSession;
List<String> list= session.getLisSelect(MyTable.class,"email","email not null");
int count = list.size();
```
#### < T > T firstOrDefault(@NonNull Class<T> aClass, String where, Object... parameters) <a name="firstOrDefault"></a>
Sometimes you need to get a single object based on a condition, and if it doesn't exist, get null.
That's what we'll do here. \
Example: Give me only the first record where the age is greater than 149 when sorting by name.

```java
ISession session = Configure.getSession;
MyTable poz = session.firstOrDefault(MyTable.class,"age > ? order by name",149);
```
This is clear, there can only be one poz.

#### < T > T first(@NonNull Class<T> aClass, String where, Object... parameters) throws Exception <a name="first"></a>
Tries to get the first record. If such a record doesn't exist, an exception is thrown.
Example: Give me only the first record where the age is greater than 149 when sorting by name.

```java
ISession session = Configure.getSession;
MyTable poz = session.firstOrDefault(MyTable.class,"age > ? and email not null order by name",200);
```
This is clear, there will be an exception, only turtles live that long, but they don't have email addresses.

#### < T > T singleOrDefault(@NonNull Class<T> aClass, String where, Object... parameters) <a name="singleOrDefault"></a>
Sometimes you want to get a unique object based on a condition, meaning it exists in the table only once or doesn't exist at all. \
Returns a unique object or null;
We're coming to you:
```java
ISession session = Configure.getSession;
MyTable poz = session.singleOrDefault(MyTable.class,"age > ? and email not null order by name",200);
```
#### < T > T single(@NonNull Class<T> aClass, String where, Object... parameters) throws Exception <a name="single"></a>
Returns a unique object based on a condition. Returns a unique object or throws an exception;

#### < T > List < Object > distinctBy(@NonNull Class<T> aClass, @NonNull String columnName, String where, Object... parameters) <a name="distinctBy"></a>
Returns distinct values  for a single field in a database table. \
Example: Give me distinct ages in the table where age is greater than 18 and sort the results in ascending order.

```java
ISession session = Configure.getSession;
List<Integer> list = session.distinctBy(MyTable.class,"age","age > ? order by age",18);
```
#### < T > Map < Object, List< T > > groupBy(@NonNull Class<T> aClass, @NonNull String columnName, String where, Object... parameters) <a name="groupBy"></a>

Gets a grouped result by a single table field, based on a condition. \
Returns a dictionary where key is a unique field value, and value is a list of rows that contain this unique value.

```java
ISession session = Configure.getSession;
Map<Integer>,List<MyTable>> result = session.groupBy(MyTable.class,"age",null);
```
#### Object executeScalar(@NonNull String sql, Object... parameters) <a name="executeScalar"></a>
#### Object executeScalar(@NonNull String sql);
These are standard functions found in any ORM; they return a single value packed into an Object. \
For those in the know, this is the first row of the cursor with column index 0.
```java
ISession session = Configure.getSession();
String sql="Select count (*) from "+session.getTableName(MyTable.class);
int count= (int) session.executeScalar(sql);
```
ISession session = Configure.getSession;

#### void executeSQL(@NonNull String sql, Object... parameters) <a name="executeSQL"></a>

This is a standard function found in any ORM; it simply executes the query and doesn't return a result. Parameters can be applied. \
It is typically used at application startup, after configuration initialization, or after table creation.
```java
ISession session = Configure.getSession();
session.executeSQL("CREATE INDEX IF NOT EXISTS test_name ON 'MyTable' ('name');",null);

```

#### < T > boolean any(@NonNull Class<T> aClass, String where, Object... parameters) <a name="any"></a>
#### < T > boolean any(@NonNull Class<T> aClass)

These are standard functions found in any ORM; they allow you to check whether records exist in a table, both conditionally and unconditionally.
```java
ISession session = Configure.getSession();
boolean b=session.any(MyTable.class," name is null");
assertFalse(b);
```

#### boolean tableExists(@NonNull Class<T> aClass) <a name="tableExists"></a>
#### boolean tableExists(@NonNull String tableName)
Checks the database for the existence of a table. If the table is found, returns true; otherwise, false.
getTableName
```java
Isession session = Configure.getSesion();
boolean exist = session.tableExists(MyTable.class);
exist = session.tableExists(session.getTableName(MyTable.class));
```
#### < T > String getTableName(@NonNull Class<T> aClass) <a name="getTableName"></a>
Returns the name of the table associated
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
Obtains a SQLiteDatabase object in the session context. This object can be used to manage the database without using the ORM.

Example of obtaining a cursor for a conditional selection:
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
#### < T > ContentValues  getContentValues(@NonNull T item) <a name="getContentValues"></a>
Gets a ```ContentValues``` object. The ```item``` type must implement mapping annotations. \
The ```ContentValues``` object gets data for all fields associated with the table.
```java
ISession session=Configure.getSession();
ContentValues  contentValues=sesssion.getContentValues(new MyTable);
```

#### < T > ContentValues  getContentValuesForUpdate(@NonNull Class<T> aClass,PairColumnValue columnValues) <a name="getContentValuesForUpdate"></a>
Gets a ```ContentValues``` object. The ```item``` type must implement mapping annotations.

The ```ContentValues``` object retrieves data for all fields entered by the user in PairColumnValue. Typically, it can be used
when updating a table record based on primary key equality.
```java
ISession session=Configure.getSession();
ContentValues  contentValues  = session.getContentValuesForUpdate(TableUser.class,new PairColumnValue()
.put("name","newName")
.put("age",20)
.put("email","ion100@df.com"));
```

#### < T > int save(@NonNull T item) <a name="save"></a>
This method can be used to insert or update an object associated with a table row. The class of this object's type
must implement the ```Prsistent``` class. The ORM automatically decides whether to insert or update the object. \
Field: ```boolean isPersistent;``` The ORM automatically populates this field (insert, update, retrieve).
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
Helper method used when traversing a cursor; returns the object filled from the cursor. \
The object type class can be any type, or a type associated with a table. The following condition applies: the names of the type fields or table columns (annotations) \
must match the names of the columns in the SQL query string. \
The type must have a public, parameterless constructor. An error may occur when creating the type or when casting fields. \

Example:
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
TestFillingPart userPart= session.objectFiller(TestFillingPart.class, cursor); 
list.add(userPart); 
} while (cursor.moveToNext()); 
}
}catch (Exception e){
throw new RuntimeException(e);
}
```

#### < T > void objectFiller(Cursor cursor, T instance) throws Exception
Helper method used when traversing a cursor; fills a previously created object from the cursor. \
The object type class can be any type or a type associated with a table. The only requirement is that the names of the type fields or table columns (annotations)
must match the names of the columns in the SQL query string.
An error may occur when casting the field type to the type obtained from the cursor. \

Example:
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
Type classes that implement this interface can receive calls when manipulating table data on the client. \
Through this call, you can control the action and state of an object in the context of table modification.
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

If the class fields are a type that implements this interface, then this field is placed in the table as a string field. \
The user controls how the string is formed and how the object body is populated from the string.

```java
public class UserClass implements IUserType
{
public String name;
public int age;
@Override
public void initBody(String str) {
Gson gson=new Gson();
UserClass inner = gson.fromJson(str, UserClass.class);
name = inner.name;
age = inner.age;
}
@Override
public String getString() {
Gson gson = new Gson();
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

### Fluent Interface <a name="Fluent"></a>
The ```IQueryable``` wrapper interface is based on the ```ISession``` interface, allowing you to construct queries using a chain of methods. \
While some may find it convenient, it's typical, like all ORMS, but it's worth noting a few points: \
When using ```rawSqlSelect```, you can select not only types whose classes are marked with an annotation,
but also arbitrary classes with a public constructor without a parameter. The main thing is that the field names match the fields in the selection.
You can also iterate over a cursor without creating a list. \
When using the ```toString()``` overload, you can view the query in text form. \
Implementation examples:
```java
    @MapTable
    class Master {
    
    @MapPrimaryKey
    public int id;
    
    @MapColumn
    public int age=10;
    
    @MapColumn
    public String name="name";
    
    @MapColumn
    public LocalDateTime dateTime = LocalDateTime.now().minusDays(1);
    }
    
    class PartialMaster {
        public int id;
        
        public int age;
    }

try (ISession session = Configure.getSession()) {
    session.query(Master.class).dropTableIfExists();
    session.query(Master.class).createTable();

    List<Master>  list=new ArrayList<>(20);
    for (int i = 0; i < 20; i++) {
        Master master=new Master();
        master.age=i;
        master.dateTime=LocalDateTime.now().plusDays(i);
        master.name="name"+i;
        list.add(master);

    }
    session.insertBulk(list);

    List<PartialMaster> listT=session.query(PartialMaster.class).rawSqlSelect("select id age from "+session.getTableName(Master.class)).toList();
    assert listT.size()==20;

    List<Master> listT3=session.query(Master.class).rawSqlSelect("select * from "+session.getTableName(Master.class))
        .where("name not null").where("age > ?",-1).orderBy("name").toList();
    assert listT3.size()==20;

    String sql= session.query(Master.class)
        .rawSqlSelect("select * from "+session.getTableName(Master.class))
        .where("name not null")
        .where("age > ?",-1)
        .orderBy("name").toString();
    Log.i("____sql____",sql);

    session.query(PartialMaster.class).rawSqlSelect("select * from "+session.getTableName(Master.class))
        .where("name not null").orderBy("age")
        .iterator(master -> Log.i("____age_____",String.valueOf(master.age)));

    List<Integer> integers=new ArrayList<>();
    session.query(Master.class).where(" name not null").iterator(master -> integers.add(master.age));
    assert integers.size()==20;

    int count= session.query(Master.class).where("name not null").where("age > ? ",5).orderBy("mame").count();
    assert count==14;

    var o=session.query(Master.class)
        .where(" name = ?","name5")
        .where("age==?",5)
        .orderBy("name")
        .orderBy("age")
        .limit(10).toList();
    assert o.size()==1;

    o=session.query(Master.class).limitOffSet(3,5).toList();
    assert o.size()==3;

    var r=session.query(Master.class).where("dateTime > ?",LocalDateTime.now().plusDays(5)).firstOrDefault();
    assert r!=null;

    r=session.query(Master.class).where("dateTime > ?",LocalDateTime.now().plusDays(50)).firstOrDefault();
    assert r==null;
    r=session.query(Master.class).where("dateTime > ?",LocalDateTime.now().plusDays(5)).singleOrDefault();
    assert r==null;
    //r=session.query(Master.class).where("dateTime > ?",LocalDateTime.now().plusDays(50)).single(); //Error

    var t=session.query(Master.class).groupBy("name");
    assert t.size()==20;

    var names=session.query(Master.class).distinctBy("name");
    assert t.size()==20;

    String tempSql="select * from "+session.getTableName(Master.class);
    var listTemp=session.query(PartialMaster.class).rawSqlSelect(tempSql).where("age > ?",-1).toList();
    assert listTemp.size()==20;

    var any=session.query(Master.class).where("age < 0").any();
    assert any==false;

}
```
### interface IUserType
### Marking objects through inheritance of the Persistent class <a name="Persistent"></a>
One of the challenges when creating form objects is storing information about an object. In our case, this is information about where the object was obtained, whether from a database or not. Different form objects use different approaches, for example, creating a proxy object based on a given type (Java, C#), marking the object with a special attribute (C#), etc.
In our case, this is inheritance of the object describing the table entity from the Persistent class.
This class has only one Boolean field, "boolean isPersistent;", which characterizes the object's origin (true - retrieved from the database; false - created on the client and not saved in the database).
Based on this, it can be used to decide what to do with an object when it's passed to the "save" method: insert or update. At the same time, this field also decides whether to throw an exception when inserting an object retrieved from the database, or whether to delete or update a locally created object.

Using this inheritance isn't a must; you can opt out and track the object's origin yourself. In that case, you won't be able to use the "save" method.

#### Retrieving typed lists from a partial record from the <a name="312"></a> table
Probably the simplest way to solve this problem is through the use of subclasses.

Let's decompose the target class into subclasses:
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
> Note that I've marked all subclasses with the annotation: ```@MapTableReadOnly```.
> This protects my table if I modify the table through objects of these subclasses or by specifying the subclass type.\
> When I try to modify the table, I'll get an error.

Another way is to get a typed list using the [getListFree](#getListFree) method. You need to prepare a select query.
This can be a JOIN or UNION SELECT. The main requirement is that the target type have fields with names matching the query fields,
or fields annotated with "@MapColumnName".\
As a final option, get a Cursor and iterate over it yourself.
When iterating over a cursor, you can use the fill method: [objectFiller](#objectFiller)
It's worth noting the use of the query parameter (where) and the Object... parameters parameter in the function. The "where" parameter doesn't require the word "where." If "where" isn't needed, you can use ":1" or "1=1."

Example: "id= 2," "id=2 and name not null, order by name LIMIT 10," "1 LIMIT 10," "1 order by name," etc.

The parameters parameter is translated into a string array. The order of entries
must match the order of the parameter (?) in the query condition string.

### Asynchronous Operations <a name="async"></a>
Fluent methods of the IQueryable interface implement "CompletableFuture" wrappers for asynchronous database access,
without blocking the main thread. Not all methods are implemented, but the most commonly used ones. This is a typical use case.
Examples of how to work with them are in the Javadoc archive.
Basic example:
```java
java
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Create a custom thread pool for background tasks
ExecutorService executor = Executors.newFixedThreadPool(4);

public void fetchDataAndDisplay() {
// 1. Start an asynchronous task on our thread pool
CompletableFuture.supplyAsync(() -> {
// Simulate a long-running operation (e.g., a network request)
try {
Thread.sleep(2000);
return "Data downloaded";
} catch (InterruptedException e) {
throw new IllegalStateException(e);
}
}, executor)
// 2. Process the result after the first task completes
.thenApplyAsync(result -> {
// Simulate data processing
return "Processed data: " + result;
}, executor)
// 3. Perform actions on the result on the main thread
.thenAcceptAsync(finalResult -> {
// Update the UI component
updateTextView(finalResult);
}, command -> new Handler(Looper.getMainLooper()).post(command));
}

// Method for updating the UI; must be called on the main thread
private void updateTextView(String text) {
// Here's the code for updating the TextView, ProgressBar, etc.
// textView.setText(text);
}
```

### How to add to the project <a name="312312"></a>
At the root of the project, there is a directory: ```aar```. It contains two files: ```bitnicorm-release.aar``` and ```sources.jar``` (help description tables).
To add to the project: create a ```libs``` directory
```markdown
app/
├─ libs/
│ ├─ bitnicorm-release.aar
│ └─ sources.jar
├─ build.gradle.kts
│
└─../ settings.gradle.kts

```

In ```build.gradle.kts```
```markdown
dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
             repositories {
                  google()
                  mavenCentral()
                  latDir {
                    dirs("app/libs")
                  }
             }
}
```
Installing from jitpack.io
```markdown
dependencyResolutionManagement {
                 repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                         repositories {
                         mavenCentral()
                         maven { url = uri("https://jitpack.io") }
                       }
}
```
```markdown
dependencies {
                implementation("com.github.ionson100:orm_android_aar:v1.2.5")
}
```
Also, in the project root there is a rar archive ```javadoc.rar```, which is a folder containing javadoc.




