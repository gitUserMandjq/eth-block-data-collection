package com.eth.transaction.dao;

import com.eth.transaction.model.EthTxnModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EthTxnDao extends JpaRepository<EthTxnModel,String>, EthTxnDao2 {

}
