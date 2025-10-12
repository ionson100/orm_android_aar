package com.bitnic.bitnicorm;

@MapAppendCommandCreateTable("CREATE INDEX IF NOT EXISTS test_name ON 'test' ('name');")
@MapTableName("'test'")
public class BaseTable {
    @MapPrimaryKey
    public long id;
}
//@MapPrimaryKey("idu")
//public UUID id=UUID.randomUUID();

//@MapPrimaryKey("id")
//public long id;