package com.eth.etlTask.service;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public interface IEtlTaskService {
    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    void etlCommonBlock(List<Long> blockNumber, Integer retry);
    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    void etlCommonBlock(List<Long> blockNumber, Integer retry, boolean filterNumber);
    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    void etlCommonBlock(List<Long> blockNumber, Integer retry, boolean filterNumber, CountDownLatch latch);
    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    void etlCommonBlock(List<Long> blockNumber, Integer retry, boolean filterNumber, CountDownLatch latch, Semaphore lock);


    /**
     * 解析某一高度的区块链数据
     * @param startBlockNumber
     * @param endBlockNumber
     * @throws Exception
     */
    void etlCommonBlock(Long startBlockNumber, Long endBlockNumber, Integer batchNum)throws Exception;
    /**
     * 解析某一高度的ens数据
     * @param blockNumber
     * @throws Exception
     */
    void etlEns(Long blockNumber, Integer retry);
    /**
     * 解析某一高度的ens数据
     * @param blockNumber
     * @throws Exception
     */
    void etlEns(List<Long> blockNumber, Integer retry);
    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    void etlEns(List<Long> blockNumber, Integer retry, CountDownLatch latch);
    /**
     * 解析某一高度的区块链数据
     * @param blockNumber
     * @throws Exception
     */
    void etlEns(List<Long> blockNumber, Integer retry, CountDownLatch latch, Semaphore lock);
    /**
     * 解析某一高度的区块链数据
     * @param startBlockNumber
     * @param endBlockNumber
     * @throws Exception
     */
    void etlEns(Long startBlockNumber, Long endBlockNumber)throws Exception;

    /**
     * 处理异常区块链数据
     * @throws Exception
     */
    void dealErrorEth(Integer errorNum)throws Exception;
}
