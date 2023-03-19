package com.eth.ens.dao;

import com.eth.ens.model.EnsDomainsDTO;
import com.eth.ens.model.EnsDomainsQO;
import com.eth.framework.base.common.model.PageParam;

import java.util.List;

public interface EthEnsInfoDao2 {
    /**
     * 查询ens列表
     * @param qo
     * @param pageParam
     * @return
     */
    List<EnsDomainsDTO> listEnsDomain(EnsDomainsQO qo, PageParam pageParam);

    /**
     * 查询ens列表总数
     * @param qo
     * @return
     */
    Integer countEnsDomain(EnsDomainsQO qo);
//    /**
//     * 批量新增
//     * @param mList
//     * @throws Exception
//     */
//    void batchInsertModel(List<EthEnsInfoModel> mList) throws Exception;
}
