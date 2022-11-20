package com.eth.framework.base.sysMessage.service;

import com.eth.framework.base.sysMessage.model.SysMessageModel;

public interface ISysMessageService {
    /**
     * 新增日志
     * @param type
     * @param message
     * @param blockNumber
     * @return
     */
    SysMessageModel addSysMessage(String type, String message, Long blockNumber, Long costTime);
}
