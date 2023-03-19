package com.eth.event.dao;

import com.eth.event.model.EthEventTransferDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface EthEventTransferDao2 {
    List<EthEventTransferDTO> listFromGroup(String address, Date startTime, Date endTime, Pageable pageable);

}
