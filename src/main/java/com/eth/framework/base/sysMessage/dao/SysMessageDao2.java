package com.eth.framework.base.sysMessage.dao;

import com.eth.framework.base.sysMessage.model.SysMessageModel;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SysMessageDao2  {

    /**
     * 批量新增
     * @param mList
     * @throws Exception
     */
    void batchInsertModel(List<SysMessageModel> mList) throws Exception;
}
