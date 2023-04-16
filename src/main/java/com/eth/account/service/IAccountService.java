package com.eth.account.service;

import com.eth.account.model.EthAccountSmartContractModel;
import com.eth.account.model.EthAccountSmartModel;
import com.eth.account.model.EthContractsModel;
import com.eth.event.model.EthEventTransferSmartModel;
import com.eth.framework.base.common.model.PageData;
import com.eth.framework.base.common.model.PageParam;

import java.io.IOException;
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
     * 删除聪明钱包
     * @param address
     * @throws Exception
     */
    void deleteAccountSmart(String address) throws Exception;

    /**
     * 查询导入的聪明钱包
     * @param beginTime
     * @param pageInfo
     * @return
     */
    PageData<EthAccountSmartModel> listAccountSmart(String tokenName, Date beginTime, PageParam pageInfo) throws Exception;

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
    /**
     * 更新聪明钱包最后交易
     * @param transfer
     * @return
     */
    EthAccountSmartModel updateAddressTransfer(EthEventTransferSmartModel transfer) throws IOException;
    /**
     * 更新聪明钱包最后交易（批量执行）
     * @param transfer
     * @return
     */
    EthAccountSmartModel updateAddressTransferWithOutSave(EthEventTransferSmartModel transfer) throws IOException;

    /**
     * 查询合约
     * @param contractAddress
     * @return
     */
    EthContractsModel getContractByAddress(String contractAddress) throws IOException;

    /**
     * 初始化合约map
     */
    void initContractMap();
}
