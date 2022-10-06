package com.eth.ens.service;

import com.eth.ens.model.EthEnsDTO;

import java.io.IOException;

public interface IEthEnsInfoService {
    /**
     * 新增或更新ens
     * @param ensDTO
     */
    void insertOrUpdateEns(EthEnsDTO ensDTO) throws IOException;
}
