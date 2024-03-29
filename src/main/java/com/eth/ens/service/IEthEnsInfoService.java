package com.eth.ens.service;

import com.eth.ens.model.EnsDomainsDTO;
import com.eth.ens.model.EnsDomainsQO;
import com.eth.ens.model.EthNftDTO;
import com.eth.framework.base.common.model.PageData;
import com.eth.framework.base.common.model.PageParam;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface IEthEnsInfoService {
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
    /**
     * 查询ens列表
     * @param qo
     * @param pageParam
     * @return
     */
    PageData<EnsDomainsDTO> listEnsDomain(EnsDomainsQO qo, PageParam pageParam);
}
