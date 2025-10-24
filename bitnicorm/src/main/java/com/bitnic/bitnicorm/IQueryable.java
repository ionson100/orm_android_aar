package com.bitnic.bitnicorm;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

public interface IQueryable<T> {

    public IQueryable textSql(@NonNull String text,Object... objects);
    public IQueryable where(@NonNull String where,Object... objects);
    public IQueryable limit(@NonNull int limit);
    public IQueryable orderBy(@NonNull String columnName);
    public IQueryable update(@NonNull String columnName,Object value);

    public T firstOrDefault();

    public T first() throws Exception;

    public T singleOrDefault();

    public T single() throws Exception;
    public Map<Object, List<T>> groupBy(@NonNull String columnName);
    public String getTableName();
    public int deleteRows();
    public boolean tableExists();

    public List<T> getList();
    public List<Object> distinctBy(@NonNull String columnName);
    public List<T> select(@NonNull String columnName);
    public void dropTableIfExists();
    public void createTable() throws Exception;
    public void  createTableIfNotExists() throws Exception;
    public  <R> R count();
    public boolean any();
    public List<T> getListFree(@NonNull String sql,Object... objects);

    public int updateNow();
    public int updateRows(@NonNull PairColumnValue columnValues);

}
