package com.eth.timer.service;

import org.springframework.scheduling.annotation.Async;

public interface ITimerService {

    void dealComTask(Long maxBlock) throws Exception;

    @Async
    void dealErrorComtask() throws Exception;

    /**
     * 处理某个范围的区块链任务
     * @param high
     * @param batchNumber
     * @throws Exception
     */
    void dealEtlEnsTask(Long high, Integer batchNumber)throws Exception;
    /**
     * 处理某个范围的区块链任务
     * @param batchNumber
     * @throws Exception
     */
    void dealEtlEnsTask(Integer batchNumber)throws Exception;
    /**
     * 处理某个范围的区块链任务
     * @param start
     * @param high
     * @param batchNumber
     * @throws Exception
     */
    void dealEtlEnsTask(Long start, Long high, Integer batchNumber)throws Exception;

    /**
     * 处理错误的任务
     * @throws Exception
     */
    void dealErrorEthTask(Integer errorNum) throws Exception;
}
