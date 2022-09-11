package com.eth.transaction.service.impl;

import com.eth.transaction.dao.EthTxnDao;
import com.eth.transaction.model.EthTxnEnsModel;
import com.eth.transaction.model.EthTxnModel;
import com.eth.transaction.service.IEthTxnService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class EthTxnServiceImpl implements IEthTxnService {
    @Resource
    EthTxnDao ethTxnDao;
    /**
     * 批量插入交易列表
     * @param list
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertTransaction(Map<String, EthTxnModel> list) throws Exception {
       ethTxnDao.batchInsertTxn(list);
    }
    /**
     * 批量插入交易列表
     * @param list
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertTransactionEns(Map<String, EthTxnModel> list) throws Exception {
        ethTxnDao.batchInsertTxnEns(list);
    }
}
