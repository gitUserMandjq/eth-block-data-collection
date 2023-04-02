package com.eth.event.dao;

import com.eth.event.model.EthEventTransferSmartModel;
import com.eth.framework.base.common.repository.BatchSaveRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EthEventTransferSmartDao extends JpaRepository<EthEventTransferSmartModel,String> , BatchSaveRepository<EthEventTransferSmartModel> {

}
