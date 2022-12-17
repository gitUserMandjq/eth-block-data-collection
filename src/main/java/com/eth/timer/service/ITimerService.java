package com.eth.timer.service;

public interface ITimerService {
    /**
     * 处理某个范围的区块链任务
     * @param high
     * @param batchNumber
     * @throws Exception
     */
    void dealEtlTask(Long high, Integer batchNumber)throws Exception;
    /**
     * 处理某个范围的区块链任务
     * @param batchNumber
     * @throws Exception
     */
    void dealEtlTask(Integer batchNumber)throws Exception;
    /**
     * 处理某个范围的区块链任务
     * @param start
     * @param high
     * @param batchNumber
     * @throws Exception
     */
    void dealEtlTask(Long start, Long high, Integer batchNumber)throws Exception;

    /**
     * 处理错误的任务
     * @throws Exception
     */
    void dealErrorEthTask(Integer errorNum) throws Exception;
}
