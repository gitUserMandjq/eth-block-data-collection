package com.eth.nft.service;

import com.eth.ens.model.EthNftDTO;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface IEthNftInfoService {
    /**
     * 新增或更新ens
     * @param ensDTO
     */
    void insertOrUpdateEns(EthNftDTO ensDTO) throws IOException, ParseException;
    /**
     * 新增或更新ens
     * @param ensDTOList
     */
    void batchInsertOrUpdateEns(List<EthNftDTO> ensDTOList) throws Exception;
}
