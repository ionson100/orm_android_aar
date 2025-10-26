package com.bitnic.bitnicorm;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

class ScopedValue<T> implements IQueryable<T> {
    private PairColumnValue columnValues;
    private final ISession iSession;
    private final Class<T> aClass;
    private String where = "";

    private String orderBy = "";
    private String limit = "";
    private String rawSelect = "";
    private Object[] objects = new Object[0];

    public ScopedValue(ISession iSession, Class<T> aClass) {
        this.iSession = iSession;
        this.aClass = aClass;
    }




    public IQueryable<T> where(@NonNull String where, Object... parameters) {
        if (!where.trim().isEmpty()) {
            if (this.where.isEmpty()) {
                this.where = where;
            } else {
                this.where += " AND " + where;
            }
            if (parameters.length > 0) {
                if (this.objects.length == 0) {
                    this.objects = parameters;
                } else {
                    Object[] combined2 = Arrays.copyOf(this.objects, this.objects.length + parameters.length);
                    System.arraycopy(parameters, 0, combined2, this.objects.length, parameters.length);
                    this.objects = combined2;
                }
            }
        }
        return this;
    }

    public IQueryable<T> limit(int limit) {
        if (limit <= 0) {
            throw new RuntimeException("лимит не может быть меньше или равно нулю");
        }
        this.limit = " LIMIT " + limit;
        return this;
    }

    @Override
    public IQueryable limitOffSet(int rowCount, int offSet) {
        this.limit = " LIMIT " + rowCount + " OFFSET " + offSet + " ";
        return this;
    }

    public IQueryable<T> orderBy(@NonNull String columnName) {
        if (this.orderBy.isEmpty()) {
            this.orderBy = " ORDER BY " + columnName;
        } else {
            this.orderBy += ", " + columnName;
        }
        return this;
    }

    @Override
    public IQueryable update(@NonNull String columnName, Object value) {
        if (this.columnValues == null) {
            this.columnValues = new PairColumnValue();
        }
        this.columnValues.put(columnName, value);
        return this;
    }

    @Override
    public IQueryable<T> rawSqlSelect(@NonNull String sql) {
        if (!sql.toUpperCase().contains("FROM")) {
            throw new RuntimeException("The query string must contain the FROM operator (select .... from table-name)");
        }
        if (sql.toUpperCase().contains("WHERE")) {
            throw new RuntimeException("The query string must not include the WHERE operator (select .... from table-name)");
        }

        this.rawSelect = sql;

        return this;
    }

    private String whereCore() {
        if (this.where.trim().isEmpty()) {
            return "1 ";
        }
        return this.where;
    }

    public T firstOrDefault() {
        String res = whereCore() +  orderBy;
        return iSession.firstOrDefault(aClass, res.trim(), this.objects);
    }

    @Override
    public T first() throws Exception {
        String res = whereCore() +  orderBy;
        return iSession.first(aClass, res.trim(), this.objects);
    }

    public T singleOrDefault() {
        String res = whereCore() +  orderBy;
        return iSession.singleOrDefault(aClass, res.trim(), this.objects);
    }

    public T single() throws Exception {
        String res = whereCore() +  orderBy;
        return iSession.single(aClass, res, this.objects);
    }

    public Map<Object, List<T>> groupBy(@NonNull String columnName) {
        String res = whereCore() +  orderBy;
        return iSession.groupBy(aClass, columnName, res.trim(), objects);
    }

    public String getTableName() {
        return iSession.getTableName(aClass);
    }

    public int deleteRows() {
        return iSession.deleteRows(aClass, whereCore(), this.objects);
    }

    public boolean tableExists() {
        return iSession.tableExists(aClass);
    }

    public List<T> toList() {
        String res;
        if (!this.rawSelect.trim().isEmpty()) {
            res = rawSelect + " WHERE " + whereCore() +  orderBy + limit;
            return iSession.getListFree(aClass, res.trim(), objects);
        } else {
            res = whereCore() +  orderBy + limit;
            return iSession.getList(aClass, res.trim(), objects);
        }


    }

    public List<Object> distinctBy(@NonNull String columnName) {
        String res = whereCore() +  orderBy + limit;
        return iSession.distinctBy(aClass, columnName, res.trim(), objects);
    }

    public List<Object> select(@NonNull String columnName) {
        String res = whereCore() +  orderBy + limit;
        return iSession.getListSelect(aClass, columnName, res.trim(), objects);
    }

    public void dropTableIfExists() {
        iSession.dropTableIfExists(aClass);
    }

    public void createTable() throws Exception {
        iSession.createTable(aClass);
    }

    public void createTableIfNotExists() throws Exception {
        iSession.createTableIfNotExists(aClass);
    }

    public int count() {
        String res = whereCore() ;
        return iSession.count(aClass, res.trim(), this.objects);
    }

    public boolean any() {
        String res = whereCore() ;
        return iSession.any(aClass, res.trim(), this.objects);
    }


    @Override
    public int updateNow() {
        if (this.columnValues == null) {
            throw new RuntimeException("No data to update");
        }
        String res = whereCore();
        return iSession.updateRows(this.aClass, this.columnValues, res.trim(), objects);
    }

    @Override
    public void iterator(ITask<T> action) {
        String res;
        if (!this.rawSelect.trim().isEmpty()) {
            res = this.rawSelect + " WHERE " + whereCore()  + orderBy + limit;
            iSession.iteratorFree(this.aClass, res.trim(), action, this.objects);
        } else {
            res = whereCore() + orderBy + limit;
            iSession.iterator(this.aClass, action, res.trim(), this.objects);
        }
    }

    @Override
    public CompletableFuture<List<T>> toListAsync() {
        return CompletableFuture.supplyAsync(this::toList);
    }



    @Override
    public CompletableFuture<List<Object>> selectAsync(@NonNull String columnName) {

        return CompletableFuture.supplyAsync(() -> select(columnName));

    }

    @Override
    public CompletableFuture<List<Object>> selectExpressionAsync(@NonNull String expression) {
        return CompletableFuture.supplyAsync(() -> selectExpression(expression));
    }

    @Override
    public List<Object> selectExpression(String expression) {
        String res = whereCore() +  orderBy + limit;
        CacheMetaData metaData = CacheDictionary.getCacheMetaData(aClass);
        String sql = "SELECT " + expression + " FROM " + metaData.tableName + " WHERE " + res.trim() + ";";
        return iSession.getListSelect(sql, objects);
    }

    @Override
    public CompletableFuture<Boolean> anyAsync() {
        return CompletableFuture.supplyAsync(this::any);
    }

    @Override
    public CompletableFuture<Integer> countAsync() {
        return CompletableFuture.supplyAsync(this::count);
    }

    @Override
    public CompletableFuture<Integer> updateNowAsync() {
        return CompletableFuture.supplyAsync(this::updateNow);
    }

    @Override
    public CompletableFuture<List<Object>> distinctByAsync(@NonNull String columnName) {
        return CompletableFuture.supplyAsync(() -> distinctBy(columnName));
    }

    @Override
    public CompletableFuture<Map<Object, List<T>>> groupByAsync(@NonNull String columnName) {
        return CompletableFuture.supplyAsync(() -> groupBy(columnName));
    }

    @Override
    public CompletableFuture<T> singleOrDefaultAsync() {
        return CompletableFuture.supplyAsync(this::singleOrDefault);
    }

    @Override
    public CompletableFuture<T> firstOrDefaultAsync() {

        return CompletableFuture.supplyAsync(this::firstOrDefault);
    }

    @Override
    public CompletableFuture<T> getByIdAsync(@NonNull Object primaryKey) {
        return CompletableFuture.supplyAsync(()->getById(primaryKey));
    }

    @Override
    public T getById(@NonNull Object primaryKey) {
        return iSession.getById(aClass,primaryKey);
    }


    @NonNull
    @Override
    public String toString() {
        return BuilderToString(this.rawSelect, this.where, this.orderBy, this.limit);
    }

    private String BuilderToString(String rawSelect, String where, String orderBy, String limit) {
        if (where.trim().isEmpty()) {
            where = where + "1";
        }
        if (!rawSelect.isEmpty()) {
            return rawSelect + " " + " WHERE " + " " + where +  " " + orderBy + " " + limit;
        } else {
            return " [WHERE] " + " " + where + " " + orderBy + " " + limit;
        }

    }
}
