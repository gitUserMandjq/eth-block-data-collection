package com.eth.framework.base.sysMessage.service;

import com.eth.framework.base.sysMessage.model.SysMessageModel;

import java.util.List;

public interface ISysMessageService {
    /**
     * 新增日志
     * @param type
     * @param message
     * @param blockNumber
     * @return
     */
    SysMessageModel addSysMessage(String type, String message, Long blockNumber, Long costTime);
    /**
     * 新增日志
     * @param type
     * @param message
     * @param blockNumber
     * @return
     */
    void addSysMessage(String type, String message, List<Long> blockNumber, Long costTime);
}
