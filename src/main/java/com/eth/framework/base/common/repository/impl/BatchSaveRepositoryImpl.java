package com.eth.framework.base.common.repository.impl;

import com.eth.framework.base.common.repository.BatchSaveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


@NoRepositoryBean
public class BatchSaveRepositoryImpl<T,ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BatchSaveRepository<T> {
    private final Integer BATCH_SIZE = 500;

    private static Logger logger = LoggerFactory.getLogger(BatchSaveRepositoryImpl.class);

    private EntityManager em = null;
    private static Map<String, SqlStorage> sqlMap = new HashMap<>();
    private static final String SQL_INSERTIGNORE = "insert_ignore";
    private static final String SQL_REPLACE = "insert_replace";

    public BatchSaveRepositoryImpl(JpaEntityInformation entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.em = entityManager;
    }
    @Override
    @Transactional
    public <S extends T> Iterable<S> batchSave(Iterable<S> var1) {
        return batchSave(var1, BATCH_SIZE);
    }
    @Override
    @Transactional
    public <S extends T> Iterable<S> batchSave(Iterable<S> var1, int batchInt) {
        Iterator<S> iterator = var1.iterator();
        int index = 0;
        while (iterator.hasNext()){
            em.persist(iterator.next());
            index++;
            if (index % batchInt == 0){
                em.flush();
                em.clear();
            }
        }
        if (index % batchInt != 0){
            em.flush();
            em.clear();
        }
        return var1;
    }

    /**
     * 通过拼接insert ignore into () values(),(),();的方式批量插入
     * @param var1
     * @param batchInt
     * @param <S>
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public <S extends T> Iterable<S> batchIgnoreSave(Iterable<S> var1, int batchInt) throws Exception {
        //清除托管对象，不然会自动执行update方法
        em.clear();
        if(var1 != null && var1.iterator().hasNext()){//判断集合是否为空
            S o1 = var1.iterator().next();
            Class<?> clazz = o1.getClass();
            //拼接语句和参数
            SqlStorage sqlStorage = getInsertIgnoreSql(clazz);
            //执行SQL语句
            executeSql(var1, batchInt, sqlStorage);
        }
        return var1;
    }
    /**
     * 通过拼接replace into () values(),(),();的方式批量插入
     * @param var1
     * @param batchInt
     * @param <S>
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public <S extends T> Iterable<S> batchReplace(Iterable<S> var1, int batchInt) throws Exception {
        //清除托管对象，不然会自动执行update方法
        em.clear();
        if(var1 != null && var1.iterator().hasNext()){//判断集合是否为空
            S o1 = var1.iterator().next();
            Class<?> clazz = o1.getClass();
            //拼接语句和参数
            SqlStorage sqlStorage = getReplaceSql(clazz);
            //执行SQL语句
            executeSql(var1, batchInt, sqlStorage);
        }
        return var1;
    }

    private <S extends T> void executeSql(Iterable<S> var1, int batchInt, SqlStorage sqlStorage) throws Exception {
        List<Field> field = sqlStorage.getField();
        Iterator<S> iterator = var1.iterator();
        int index = 0;
        StringBuilder sqlBuilder = new StringBuilder(sqlStorage.getInsertSql());
        List<S> tempList = new ArrayList<>();
        while (iterator.hasNext()){
            S next = iterator.next();
            sqlBuilder.append(sqlStorage.getValueSql());
            tempList.add(next);
            index++;
            if (index % batchInt == 0){
                executeBatchSql(field, sqlBuilder, tempList);//执行批量SQL语句
                sqlBuilder = new StringBuilder(sqlStorage.getInsertSql());
                tempList = new ArrayList<>();
            }
        }
        if (index % batchInt != 0){
            executeBatchSql(field, sqlBuilder, tempList);//执行批量SQL语句
        }
    }

    private static SqlStorage getInsertIgnoreSql(Class<?> clazz) {
        //通过@Table注解获得表格名称
        Table table = clazz.getAnnotation(Table.class);
        String tableName = table.name();
        String key = SQL_INSERTIGNORE + tableName;
        SqlStorage sqlStorage = sqlMap.get(key);
        if(sqlStorage == null){
            String insertPrefix = "insert ignore into";
            sqlStorage = getSqlStorage(clazz, tableName, key, insertPrefix);
        }

        return sqlStorage;
    }
    private static SqlStorage getReplaceSql(Class<?> clazz) {
        //通过@Table注解获得表格名称
        Table table = clazz.getAnnotation(Table.class);
        String tableName = table.name();
        String key = SQL_REPLACE + tableName;
        SqlStorage sqlStorage = sqlMap.get(key);
        if(sqlStorage == null){
            String insertPrefix = "replace into";
            sqlStorage = getSqlStorage(clazz, tableName, key, insertPrefix);
        }
        return sqlStorage;
    }

    private static SqlStorage getSqlStorage(Class<?> clazz, String tableName, String key, String insertPrefix) {
        SqlStorage sqlStorage;
        sqlStorage = new SqlStorage();
        Field[] fields = clazz.getDeclaredFields();
        List<Field> field = new ArrayList<>();
        sqlStorage.setField(field);
        StringBuilder insertStr = new StringBuilder(insertPrefix + " `"+ tableName +"`(");
        StringBuilder valuesStr = new StringBuilder("(");
        for(Field f: fields){
            //通过@Column注解获得字段名
            String columnName = getColumnName(clazz, f);
            if(!StringUtils.isEmpty(columnName)){
                //拼接插入语句和value语句
                insertStr.append(" `"+ columnName+"`,");
                valuesStr.append(" ?,");
                field.add(f);
            }
        }
        insertStr.deleteCharAt(insertStr.length() - 1);
        insertStr.append(") values");
        valuesStr.deleteCharAt(valuesStr.length() - 1);
        valuesStr.append("),");
        sqlStorage.setInsertSql(insertStr.toString());
        sqlStorage.setValueSql(valuesStr.toString());
        logger.info("初始化SQL插入对象:insertSql:{},valueSql:{},field:{}", sqlStorage.getInsertSql()
                , sqlStorage.getValueSql()
                , sqlStorage.getField().stream().map(Field::getName).collect(Collectors.toList()));
        sqlMap.put(key, sqlStorage);
        return sqlStorage;
    }

    private <S extends T> void executeBatchSql(List<Field> field, StringBuilder sqlBuilder, List<S> tempList) throws Exception {
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        Query query = em.createNativeQuery(sqlBuilder.toString());
        int paramIndex = 1;
        for(S t: tempList){
            for(Field f: field){
                query.setParameter(paramIndex++, getField(t, f.getName()));
            }
        }
        query.executeUpdate();
    }
    public static <T> Object getField(T t, String fieldName) throws Exception {
        Field f = t.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(t);
    }


    static class SqlStorage{
        private String insertSql;
        private String valueSql;
        private List<Field> field;
        public String getInsertSql() {
            return insertSql;
        }


        public void setInsertSql(String insertSql) {
            this.insertSql = insertSql;
        }


        public String getValueSql() {
            return valueSql;
        }


        public void setValueSql(String valueSql) {
            this.valueSql = valueSql;
        }

        public List<Field> getField() {
            return field;
        }

        public void setField(List<Field> field) {
            this.field = field;
        }
    }

    private static String getColumnName(Class<?> clazz, Field f) {
        boolean fieldHasAnno = f.isAnnotationPresent(Column.class);
        String fieldName = f.getName();
        if(fieldHasAnno) {//先查询field上是否有@Column
            Column column = f.getAnnotation(Column.class);
            String columnName = column.name();
            return columnName;
        }else{//再查询field的get方法上是否有@Column
            String getName = "get"+fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            try {
                Method method = clazz.getDeclaredMethod(getName);
                Column column = method.getAnnotation(Column.class);
                if(column != null){
                    String columnName = column.name();
                    return columnName;
                }
            }catch (Exception e){

            }
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#save(java.lang.Object)
     */
    @Override
    @Transactional
    public <S extends T> S insert(S entity) {
        em.persist(entity);
        return entity;
    }
    @Override
    @Transactional
    public <S extends T> S update(S entity) {
        return em.merge(entity);
    }

}