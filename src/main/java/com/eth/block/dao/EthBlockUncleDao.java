package com.eth.block.dao;

import com.eth.block.model.EthBlockUncleModel;
import com.eth.framework.base.common.repository.BatchSaveRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EthBlockUncleDao extends JpaRepository<EthBlockUncleModel,String>, BatchSaveRepository<EthBlockUncleModel> {
}
