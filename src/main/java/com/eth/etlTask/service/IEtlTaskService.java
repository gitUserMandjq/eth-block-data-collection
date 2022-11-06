package com.eth.etlTask.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public interface IEtlTaskService {
    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    void etlEthBlock(Long blockNumber, Integer retry);
    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    void etlEthBlock(Long blockNumber, Integer retry, CountDownLatch latch);
    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    void etlEthBlock(Long blockNumber, Integer retry, CountDownLatch latch, Semaphore lock);
    /**
     * 解析某一高度的区块链数据
     * @param startBlockNumber
     * @param endBlockNumber
     * @throws Exception
     */
    void etlEthBlock(Long startBlockNumber, Long endBlockNumber)throws Exception;

    /**
     * 处理异常区块链数据
     * @throws Exception
     */
    void dealErrorEth()throws Exception;
}
