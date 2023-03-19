package com.eth.event.dao.impl;

import com.eth.event.dao.EthEventTransferDao2;
import com.eth.event.model.EthEventTransferDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Slf4j
public class EthEventTransferDaoImpl implements EthEventTransferDao2 {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<EthEventTransferDTO> listFromGroup(String address, Date startTime, Date endTime, Pageable pageable) {
        return null;
    }
}
