package com.eth.transaction.dao;

import com.eth.framework.base.common.repository.BatchSaveRepository;
import com.eth.transaction.model.EthTxnModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EthTxnDao extends JpaRepository<EthTxnModel,String>, BatchSaveRepository<EthTxnModel>, EthTxnDao2
{

}
