package com.eth.framework.base.sysMessage.service;

import com.eth.framework.base.sysMessage.model.SysErrorMessageModel;

import java.util.List;

public interface ISysErrorMessageService {
    /**
     * 新增错误日志
     * @param type
     * @param message
     * @param blockNumber
     * @return
     */
    SysErrorMessageModel addSysErrorMessage(String type, String message, Long blockNumber);

    /**
     * 查询未处理的错误日志
     * @param type
     * @return
     */
    List<SysErrorMessageModel> listNotDealSysErrorMessage(String type, Integer limit);
    /**
     * 处理错误日志
     * @param id
     * @return
     */
    void dealSysErrorMessage(List<Long> id);
}
