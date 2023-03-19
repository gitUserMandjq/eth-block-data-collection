package com.eth.event.service.impl;

import com.eth.event.dao.EthEventTransferDao;
import com.eth.event.model.EthEventTransferModel;
import com.eth.event.service.IEthEventTransferService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class EthEventTransferServiceImpl implements IEthEventTransferService {
    @Resource
    EthEventTransferDao ethEventTransferDao;
    /**
     * 批量新增交易事件
     * @param transferList
     * @return
     */
    @Override
    public void addBatchEventTransfer(List<EthEventTransferModel> transferList) throws Exception {
        ethEventTransferDao.batchIgnoreSave(transferList, 500);
    }
}
