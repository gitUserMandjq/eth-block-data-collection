package com.eth.account.service;

import com.eth.account.model.EthAccountSmartContractModel;
import com.eth.account.model.EthAccountSmartModel;
import com.eth.framework.base.common.model.PageData;
import com.eth.framework.base.common.model.PageParam;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IAccountService {
    /**
     * 获取账户代码，如果是非合约账户返回0x
     * @param address
     * @return
     */
    String getAccountCode(String address) throws Exception;
    /**
     * 批量获取账户代码，如果是非合约账户返回0x
     * @param address
     * @return
     */
    List<String> getAccountCode(Iterable<String> address) throws Exception;
    /**
     * 批量导入聪明钱包
     * @param accountList
     * @return
     */
    void addBatchAccountSmart(Iterable<EthAccountSmartModel> accountList) throws Exception;
    /**
     * 查询导入的聪明钱包
     * @param beginTime
     * @param pageInfo
     * @return
     */
    PageData<EthAccountSmartModel> listAccountSmart(Date beginTime, PageParam pageInfo) throws Exception;

    /**
     * 批量导入聪明钱包和合约地址关联
     * @param accountContractList
     * @return
     */
    void addBatchAccountSmartContract(Iterable<EthAccountSmartContractModel> accountContractList) throws Exception;

    /**
     * 根据合约地址获得监听的聪明钱包
     * @param tokenAddress
     * @return
     */
    Set<String> getSmartContractByTokenAddress(String tokenAddress);
}
