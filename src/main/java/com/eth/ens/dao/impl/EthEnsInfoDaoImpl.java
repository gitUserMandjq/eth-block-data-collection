package com.eth.ens.dao.impl;

import com.eth.ens.dao.EthEnsInfoDao2;
import com.eth.ens.model.EnsDomainsDTO;
import com.eth.ens.model.EnsDomainsQO;
import com.eth.framework.base.model.PageParam;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class EthEnsInfoDaoImpl implements EthEnsInfoDao2 {
    @PersistenceContext
    private EntityManager em;
    /**
     * 查询ens列表
     * @param qo
     * @param pageParam
     * @return
     */
    @Override
    public List<EnsDomainsDTO> listEnsDomain(EnsDomainsQO qo, PageParam pageParam) {
        return null;
    }
    /**
     * 查询ens列表总数
     * @param qo
     * @param pageParam
     * @return
     */
    @Override
    public Integer countEnsDomain(EnsDomainsQO qo, PageParam pageParam) {
        return null;
    }
}
