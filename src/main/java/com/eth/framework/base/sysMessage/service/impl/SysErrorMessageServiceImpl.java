package com.eth.framework.base.sysMessage.service.impl;

import com.eth.framework.base.sysMessage.dao.SysErrorMessageDao;
import com.eth.framework.base.sysMessage.model.SysErrorMessageModel;
import com.eth.framework.base.sysMessage.service.ISysErrorMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
@Service
public class SysErrorMessageServiceImpl implements ISysErrorMessageService {
    @Resource
    SysErrorMessageDao sysErrorMessageDao;
    /**
     * 新增错误日志
     * @param type
     * @param message
     * @param blockNumber
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysErrorMessageModel addSysErrorMessage(String type, String message, Long blockNumber) {
        SysErrorMessageModel model = new SysErrorMessageModel();
        model.setType(type);
        model.setMessage(message);
        model.setCreateTime(new Date());
        model.setBlockNumber(blockNumber);
        model.setStatus(0);
        model = sysErrorMessageDao.save(model);
        return model;
    }
    /**
     * 查询未处理的错误日志
     * @param type
     * @return
     */
    @Override
    public List<SysErrorMessageModel> listNotDealSysErrorMessage(String type) {
        List<SysErrorMessageModel> list = sysErrorMessageDao.findByTypeAndStatus(type, 0);
        return list;
    }
    /**
     * 处理错误日志
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dealSysErrorMessage(List<Long> id) {
        sysErrorMessageDao.dealSysErrorMessage(id);
    }
}
