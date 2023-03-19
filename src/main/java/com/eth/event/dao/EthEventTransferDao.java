package com.eth.event.dao;

import com.eth.event.model.EthEventTransferModel;
import com.eth.framework.base.common.repository.BatchSaveRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface EthEventTransferDao extends JpaRepository<EthEventTransferModel,String> , BatchSaveRepository<EthEventTransferModel> {
    @Query(value="select u from EthEventTransferModel u where u.tokenAddress = ?1 and u.timestamp >= ?2 and u.timestamp < ?3")
    Page<EthEventTransferModel> listTransferByAddressAndTime(String address, Date startTime, Date endTime, Pageable pageable);

}
