package com.eth.transaction.dao;

import com.eth.transaction.model.EthTxnEnsModel;
import com.eth.transaction.model.EthTxnModel;

import java.util.LinkedHashMap;
import java.util.List;
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
     * @param mList
     * @throws Exception
     */
    void batchInsertTxnEns(Map<String, EthTxnModel> mList) throws Exception;
}
