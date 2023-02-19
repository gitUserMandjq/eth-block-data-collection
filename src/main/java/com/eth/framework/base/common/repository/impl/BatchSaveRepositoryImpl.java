package com.eth.framework.base.common.repository.impl;

import com.eth.framework.base.common.repository.BatchSaveRepository;
import com.eth.framework.base.common.utils.ReflectUtils;
import com.eth.framework.base.common.utils.VariableNameConversion;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

@NoRepositoryBean
public class BatchSaveRepositoryImpl<T,ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BatchSaveRepository<T> {
    private final Integer BATCH_SIZE = 500;
 
 
    private EntityManager em = null;
    private static Map<String, SqlStorage> sqlMap = new HashMap<>();
    private static final String SQL_INSERTIGNORE = "insert_ignore";
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
    @Override
    @Transactional
    public <S extends T> Iterable<S> batchIgnoreSave(Iterable<S> var1, int batchInt) throws Exception {
        if(var1 != null && var1.iterator().hasNext()){
            S o1 = var1.iterator().next();
            Class<?> clazz = o1.getClass();
            Field[] field = clazz.getDeclaredFields();
            //拼接忽略插入语句
            SqlStorage sqlStorage = getInsertIgnoreSql(clazz, field);
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
                    executeBatchSql(field, sqlBuilder, tempList);
                    sqlBuilder = new StringBuilder(sqlStorage.getInsertSql());
                    tempList = new ArrayList<>();
                }
            }
            if (index % batchInt != 0){
                executeBatchSql(field, sqlBuilder, tempList);
            }
        }
        return var1;
    }

    private <S extends T> void executeBatchSql(Field[] field, StringBuilder sqlBuilder, List<S> tempList) throws Exception {
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        Query query = em.createNativeQuery(sqlBuilder.toString());
        int paramIndex = 1;
        for(S t: tempList){
            for(Field f: field){
                query.setParameter(paramIndex++, ReflectUtils.getField(t, f.getName()));
            }
        }
        query.executeUpdate();
    }

    static class SqlStorage{
        private String insertSql;
        private String valueSql;
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
    }
    @NotNull
    private static SqlStorage getInsertIgnoreSql(Class<?> clazz, Field[] field) {
        String tableName = ReflectUtils.getTableName(clazz);
        SqlStorage sqlStorage = sqlMap.get(SQL_INSERTIGNORE + tableName);
        if(sqlStorage == null){
            sqlStorage = new SqlStorage();
            StringBuilder insertStr = new StringBuilder("insert ignore into `"+ tableName+"`(");
            StringBuilder valuesStr = new StringBuilder("(");
            for(Field f: field){
                insertStr.append(" `"+ VariableNameConversion.humpToLowerLine(f.getName())+"`,");
                valuesStr.append(" ?,");
            }
            insertStr.deleteCharAt(insertStr.length() - 1);
            insertStr.append(") values");
            valuesStr.deleteCharAt(valuesStr.length() - 1);
            valuesStr.append("),");
            sqlStorage.setInsertSql(insertStr.toString());
            sqlStorage.setValueSql(valuesStr.toString());
            sqlMap.put(SQL_INSERTIGNORE+tableName, sqlStorage);
        }
        return sqlStorage;
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
