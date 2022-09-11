package com.eth.etlTask.service;

public interface IEtlTaskService {
    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    void etlEthBlock(Long blockNumber)throws Exception;
}
