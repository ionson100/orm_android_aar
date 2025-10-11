package com.bitnic.bitnicorm.actionorm;

import com.bitnic.bitnicorm.IEventOrm;
import com.bitnic.bitnicorm.MapColumn;
import com.bitnic.bitnicorm.MapPrimaryKey;
import com.bitnic.bitnicorm.MapTableName;

import java.util.UUID;

/**
 * The type Table invoke orm.
 */
@MapTableName("23-34")
public class TableActionOrm implements IEventOrm {

    /**
     * The Id.
     */
    @MapPrimaryKey
    public UUID id=UUID.randomUUID();

    /**
     * The Name.
     */
    @MapColumn
    public String name;
    /**
     * The Action.
     */
    public int action;
    @Override
    public void beforeUpdate() {
        this.action=this.action+1;
    }

    @Override
    public void afterUpdate() {
        this.action=this.action+1;
    }

    @Override
    public void beforeInsert() {
        this.action=this.action+100;

    }

    @Override
    public void afterInsert() {
        this.action=this.action+100;
    }

    @Override
    public void beforeDelete() {
        this.action=this.action+3;
    }

    @Override
    public void afterDelete() {
        this.action=this.action+3;
    }
}
