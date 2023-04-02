package com.eth.framework.base.common.repository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BatchSaveRepository<T>{
	/**
	 * 批量新增
	 * @param var1
	 * @return
	 */
	public <S extends T> Iterable<S> batchSave(Iterable<S> var1);
	/**
	 * 批量新增
	 * @param var1
	 * @return
	 */
	public <S extends T> Iterable<S> batchSave(Iterable<S> var1, int batchInt);
	/**
	 * 通过拼接insert ignore into () values(),(),();的方式批量插入
	 * @param var1
	 * @param batchInt
	 * @param <S>
	 * @return
	 * @throws Exception
	 */
	public <S extends T> Iterable<S> batchIgnoreSave(Iterable<S> var1, int batchInt) throws Exception;
	/**
	 * 通过拼接replace into () values(),(),();的方式批量插入
	 * @param var1
	 * @param batchInt
	 * @param <S>
	 * @return
	 * @throws Exception
	 */
	public <S extends T> Iterable<S> batchReplace(Iterable<S> var1, int batchInt) throws Exception;

	/**
	 * 新增对象，不进行id验证
	 * @param entity
	 * @return
	 */
	<S extends T> S insert(S entity);
	/**
	 * 更新对象，不进行id验证
	 * @param entity
	 * @return
	 */
	<S extends T> S update(S entity);
}