package com.eth.event.dao;

import com.eth.event.model.EthEventErc20TransferModel;
import com.eth.framework.base.common.repository.BatchSaveRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EthEventErc20TransferDao extends JpaRepository<EthEventErc20TransferModel,String> , BatchSaveRepository<EthEventErc20TransferModel> {

}
