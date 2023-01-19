package com.eth.framework.base.sysMessage.dao;

import com.eth.framework.base.sysMessage.model.SysMessageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface SysMessageDao extends JpaRepository<SysMessageModel,Long>,SysMessageDao2 {
    @Query(value="select max(u.blockNumber) from SysMessageModel u where u.type = ?1")
    Long getMaxBlockNumber(String type);
}
