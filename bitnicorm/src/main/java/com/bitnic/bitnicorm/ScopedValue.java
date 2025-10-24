package com.bitnic.bitnicorm;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionHelper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

 public class ScopedValue<T> implements IQueryable<T>{
     private PairColumnValue columnValues;
    private final ISession iSession;
    private final Class<T> aClass;
    private String where="";
    private String textSql="";
    private String orderBy="";
    private String limit="";
    private Object[] objects=new Object[0];
    public ScopedValue(ISession iSession,Class<T> aClass){
        this.iSession = iSession;
        this.aClass = aClass;
    }




    public IQueryable<T> textSql(@NonNull String text,Object... objects){
        if(text!=null&&text.trim().isEmpty()==false){
            this.textSql+=" "+textSql;
            if(objects.length>0){
                if(this.objects.length==0){
                    this.objects=objects;
                }else {
                    Object[] combined2 = Arrays.copyOf(this.objects, this.objects.length + objects.length);
                    System.arraycopy(objects, 0, combined2, this.objects.length, objects.length);
                    this.objects=combined2;
                }
            }


        }
        return this;
    }

    public IQueryable<T> where(@NonNull String where,Object... objects) {
        if(where!=null&&where.trim().isEmpty()==false){
            if(this.where.isEmpty()){
                this.where=where;
            }else {
                this.where+=" AND "+where;
            }
            if(objects.length>0){
                if(this.objects.length==0){
                    this.objects=objects;
                }else {
                    Object[] combined2 = Arrays.copyOf(this.objects, this.objects.length + objects.length);
                    System.arraycopy(objects, 0, combined2, this.objects.length, objects.length);
                    this.objects=combined2;
                }
            }
        }
        return this;
    }
    public IQueryable<T> limit(@NonNull int limit){
        if(limit<=0){
            throw new RuntimeException("лимит не может быть меньше или равно нулю");
        }
        this.limit= " LIMIT "+ limit;
        return this;
    }
    public IQueryable<T> orderBy(@NonNull String columnName){
        if(this.orderBy.isEmpty()){
            this.orderBy= " ORDER BY "+columnName;
        }else{
            this.orderBy+=", "+columnName;
        }
        return this;
    }

     @Override
     public IQueryable update(@NonNull String columnName, Object value) {
         if(this.columnValues==null){
             this.columnValues=new PairColumnValue();
         }
         this.columnValues.put(columnName,value);
         return this;
     }

     public T firstOrDefault(){
        String res= where+textSql+orderBy;
        return iSession.firstOrDefault(aClass,res,this.objects);
    }
    public T first() throws Exception {
        String res= where+textSql+orderBy;
        return iSession.first(aClass,res,this.objects);
    }
    public T singleOrDefault(){
        String res= where+textSql+orderBy;
        return iSession.singleOrDefault(aClass,res,this.objects);
    }
    public T single() throws Exception {
        String res= where+textSql+orderBy;
        return iSession.single(aClass,res,this.objects);
    }
    public Map<Object,List<T>> groupBy(@NonNull String columnName){
        String res= where+textSql+orderBy;
        return  iSession.groupBy(aClass,columnName,where,objects);
    }
    public String getTableName(){
        return iSession.getTableName(aClass);
    }

    public int deleteRows(){
        return iSession.deleteRows(aClass);
    }
    public boolean tableExists(){
        return iSession.tableExists(aClass);
    }

    public List<T> getList() {
        String res= where+textSql+orderBy+limit;
        return iSession.getList(aClass,res,objects);
    }
    public List<Object> distinctBy(@NonNull String columnName){
        String res= where+textSql+orderBy+limit;
        return iSession.distinctBy(aClass,columnName,where,objects);
    }
    public List<T> select(@NonNull String columnName){
        String res= where+textSql+orderBy+limit;
        return iSession.getListSelect(aClass,columnName,res,objects);
    }
    public void dropTableIfExists(){
        iSession.dropTableIfExists(aClass);
    }
    public void createTable() throws Exception {
        iSession.createTable(aClass);
    }

    public void  createTableIfNotExists() throws Exception {
        iSession.createTableIfNotExists(aClass);
    }
    public  <R> R count(){
        return  iSession.count(aClass,this.where,this.objects);
    }
    public boolean any(){
        return iSession.any(aClass,this.where,this.objects);
    }
    public List<T> getListFree(@NonNull String sql,Object... objects){
        return iSession.getListFree(aClass,sql,objects);
    }

     @Override
     public int updateNow() {
         if(this.columnValues==null){
             throw new RuntimeException("Нет данных для обновления");
         }
         return iSession.updateRows(this.aClass,this.columnValues,where,objects);
     }

     public int updateRows(@NonNull PairColumnValue columnValues){
        return  iSession.updateRows(aClass,columnValues,this.where,this.objects);
    }



     @NonNull
     @Override
     public String toString() {
        StringBuilder stringBuilder=new StringBuilder("[WHERE] ");
        if(where.isEmpty()){
            stringBuilder.append("1=1");
        }else {
            stringBuilder.append(where);
        }
        stringBuilder.append(this.textSql).append(orderBy).append(limit);
        return stringBuilder.toString();

     }
 }
