package com.eth.framework.base.sysMessage.service.impl;

import com.eth.framework.base.sysMessage.dao.SysMessageDao;
import com.eth.framework.base.sysMessage.model.SysMessageModel;
import com.eth.framework.base.sysMessage.service.ISysMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
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
    public SysMessageModel addSysMessage(String type, String message, Long blockNumber, Long costTime) {
        SysMessageModel model = new SysMessageModel();
        model.setType(type);
        model.setMessage(message);
        model.setCreateTime(new Date());
        model.setBlockNumber(blockNumber);
        model.setCostTime(costTime);
        model = sysMessageDao.save(model);
        return model;
    }
    /**
     * 新增日志
     * @param type
     * @param message
     * @param blockNumber
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSysMessage(String type, String message, List<Long> blockNumber, Long costTime) {
        Integer size = blockNumber.size();
        Long avgCostTime = costTime/size;
        List<SysMessageModel> mList = new ArrayList<>();
        for(Long bn:blockNumber){
            SysMessageModel model = new SysMessageModel();
            model.setType(type);
            model.setMessage(message);
            model.setCreateTime(new Date());
            model.setBlockNumber(bn);
            model.setCostTime(avgCostTime);
            mList.add(model);
        }

        try {
            sysMessageDao.batchInsertModel(mList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    /**
     * 获取某个消息的最大处理值
     * @param type
     * @return
     */
    @Override
    public Long getMaxBlockNumber(String type) {
        return sysMessageDao.getMaxBlockNumber(type);
    }
}
