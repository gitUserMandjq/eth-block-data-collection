package com.eth.event.service;

import com.eth.event.model.EthEventTransferModel;

import java.util.List;

public interface IEthEventTransferService {
    /**
     * 批量新增交易事件
     * @param transferList
     * @return
     */
    void addBatchEventTransfer(List<EthEventTransferModel> transferList) throws Exception;
}
