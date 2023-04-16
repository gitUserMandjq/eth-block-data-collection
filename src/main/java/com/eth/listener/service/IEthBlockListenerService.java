package com.eth.listener.service;

import com.eth.listener.model.EthBlockListenerModel;

import java.io.IOException;

public interface IEthBlockListenerService {
    /**
     * 批量导入监听器
     * @param list
     */
    void addBatchEthListener(Iterable<EthBlockListenerModel> list) throws Exception;
    /**
     * 启动监听器
     * @param listener
     */
    void startEthListener(EthBlockListenerModel listener) throws IOException;
    /**
     * 停用监听器
     * @param listener
     */
    void stopEthListener(EthBlockListenerModel listener);
    /**
     * 初始化启动监听器
     */
    void startInitEthListenerAll();
}
