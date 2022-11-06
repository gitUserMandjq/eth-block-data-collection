package com.eth.framework.base.sysMessage.dao;

import com.eth.framework.base.sysMessage.model.SysMessageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SysMessageDao extends JpaRepository<SysMessageModel,Long>  {

}
