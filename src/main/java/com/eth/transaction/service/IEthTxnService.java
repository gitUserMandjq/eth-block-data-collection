package com.eth.transaction.service;

import com.eth.transaction.model.EthTxnEnsModel;
import com.eth.transaction.model.EthTxnModel;

import java.util.List;
import java.util.Map;

public interface IEthTxnService {
    /**
     * 批量插入交易列表
     * @param list
     * @throws Exception
     */
    void batchInsertTransaction(Map<String, EthTxnModel> list) throws Exception;
    /**
     * 批量插入交易列表
     * @param list
     * @throws Exception
     */
    void batchInsertTransactionEns(Map<String, EthTxnModel> list) throws Exception;
}
