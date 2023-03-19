package com.eth.block.dao;

import com.eth.block.model.EthBlockModel;
import com.eth.framework.base.common.repository.BatchSaveRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface EthBlockDao  extends JpaRepository<EthBlockModel,Long>, BatchSaveRepository<EthBlockModel> {
    @Query(value="select min(u.blockNumber) from EthBlockModel u")
    Long getMinBlockNumber();
    @Query(value="select max(u.blockNumber) from EthBlockModel u")
    Long getMaxBlockNumber();
}
