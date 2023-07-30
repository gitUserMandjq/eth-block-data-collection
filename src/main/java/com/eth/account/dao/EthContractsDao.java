package com.eth.account.dao;

import com.eth.account.model.EthContractsModel;
import com.eth.framework.base.common.repository.BatchSaveRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


@Repository
public interface EthContractsDao extends JpaRepository<EthContractsModel, BigInteger>, BatchSaveRepository<EthContractsModel>, JpaSpecificationExecutor<EthContractsModel> {
    /**
     * 根据合约地址查询合约
     * @param address
     * @return
     */
    EthContractsModel findTopByAddress(String address);

    /**
     * 查询某些类型的合约
     * @param type
     * @return
     */
    @Query("select u from EthContractsModel u where u.type in ?1")
    List<EthContractsModel> listContractInType(Iterable<String> type, Pageable page);
    /**
     * 查询某些类型的合约
     * @param type
     * @return
     */
    @Query("select count(u) from EthContractsModel u where u.type in ?1")
    Integer countContractInType(Iterable<String> type);
}
