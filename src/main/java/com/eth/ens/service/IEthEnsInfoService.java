package com.eth.ens.service;

import com.eth.ens.model.EnsDomainsDTO;
import com.eth.ens.model.EnsDomainsQO;
import com.eth.ens.model.EthEnsDTO;
import com.eth.framework.base.common.model.PageData;
import com.eth.framework.base.common.model.PageParam;

import java.io.IOException;

public interface IEthEnsInfoService {
    /**
     * 新增或更新ens
     * @param ensDTO
     */
    void insertOrUpdateEns(EthEnsDTO ensDTO) throws IOException;
    /**
     * 查询ens列表
     * @param qo
     * @param pageParam
     * @return
     */
    PageData<EnsDomainsDTO> listEnsDomain(EnsDomainsQO qo, PageParam pageParam);
}
