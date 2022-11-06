package com.eth.framework.base.sysMessage.service.impl;

import com.eth.framework.base.sysMessage.dao.SysMessageDao;
import com.eth.framework.base.sysMessage.model.SysMessageModel;
import com.eth.framework.base.sysMessage.service.ISysMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class SysMessageService implements ISysMessageService {
    @Resource
    SysMessageDao sysMessageDao;
    /**
     * 新增日志
     * @param type
     * @param message
     * @param blockNumber
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysMessageModel addSysMessage(String type, String message, Long blockNumber) {
        SysMessageModel model = new SysMessageModel();
        model.setType(type);
        model.setMessage(message);
        model.setCreateTime(new Date());
        model.setBlockNumber(blockNumber);
        model = sysMessageDao.save(model);
        return model;
    }
}
