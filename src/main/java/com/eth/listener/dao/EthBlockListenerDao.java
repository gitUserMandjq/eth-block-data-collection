package com.eth.listener.dao;

import com.eth.framework.base.common.repository.BatchSaveRepository;
import com.eth.listener.model.EthBlockListenerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface EthBlockListenerDao extends JpaRepository<EthBlockListenerModel,String>, BatchSaveRepository<EthBlockListenerModel>, JpaSpecificationExecutor<EthBlockListenerModel> {

}
