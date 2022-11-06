package com.eth.framework.base.sysMessage.dao;

import com.eth.framework.base.sysMessage.model.SysErrorMessageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SysErrorMessageDao extends JpaRepository<SysErrorMessageModel,Long>  {
    /**
     * 查询错误日志列表
     * @param type
     * @param status
     * @return
     */
    @Query(value="select u from SysErrorMessageModel u where u.type = ?1 and u.status = ?2")
    List<SysErrorMessageModel> findByTypeAndStatus(String type, Integer status);

    /**
     * 处理错误日志
     * @param ids
     * @return
     */
    @Query(value="update SysErrorMessageModel u set u.status = 1 where u.id in ?1")
    @Modifying
    Integer dealSysErrorMessage(List<Long> ids);
}
