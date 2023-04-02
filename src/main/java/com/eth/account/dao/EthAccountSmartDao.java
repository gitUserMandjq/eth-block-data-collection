package com.eth.account.dao;

import com.eth.account.model.EthAccountSmartModel;
import com.eth.framework.base.common.repository.BatchSaveRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface EthAccountSmartDao extends JpaRepository<EthAccountSmartModel,String>, BatchSaveRepository<EthAccountSmartModel>, JpaSpecificationExecutor<EthAccountSmartModel> {

}
