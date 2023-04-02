package com.eth.event.dao;

import com.eth.event.model.EthEventTransferDTO;
import com.eth.event.model.EthEventTransferQO;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EthEventTransferDao2 {
    List<EthEventTransferDTO> listFromGroup(String address, List<EthEventTransferQO> qos);
    List<EthEventTransferDTO> listToGroup(String address, List<EthEventTransferQO> qos);

}
