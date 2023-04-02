package com.eth.account.dao;

import com.eth.account.model.EthAccountSmartContractModel;
import com.eth.framework.base.common.repository.BatchSaveRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EthAccountSmartContractDao extends JpaRepository<EthAccountSmartContractModel,String>, BatchSaveRepository<EthAccountSmartContractModel> {
    /**
     * 根据合约地址查找监听的聪明钱包
     * @param tokenAddress
     * @return
     */
    List<EthAccountSmartContractModel> findByTokenAddress(String tokenAddress);
}
