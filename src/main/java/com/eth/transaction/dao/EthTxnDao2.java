package com.eth.transaction.dao;

import com.eth.transaction.model.EthTxnModel;

import java.util.Map;

public interface EthTxnDao2 {
    /**
     * 批量新增
     * @param mList
     * @throws Exception
     */
    void batchInsertTxn(Map<String, EthTxnModel> mList) throws Exception;
    /**
     * 批量新增
     * @param txn
     * @throws Exception
     */
    void batchInsertTxn(EthTxnModel txn) throws Exception;
    /**
     * 批量新增
     * @param mList
     * @throws Exception
     */
    void batchInsertTxnEns(Map<String, EthTxnModel> mList) throws Exception;
}
