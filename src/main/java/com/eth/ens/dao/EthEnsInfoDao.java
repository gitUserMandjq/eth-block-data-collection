package com.eth.ens.dao;

import com.eth.ens.model.EthEnsInfoModel;
import com.eth.framework.base.common.repository.BatchSaveRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EthEnsInfoDao extends JpaRepository<EthEnsInfoModel,String> ,EthEnsInfoDao2, BatchSaveRepository<EthEnsInfoModel> {
    /**
     * 通过id批量查询ens
     * @param tokenIds
     * @return
     */
    @Query(value="select u from EthEnsInfoModel u where u.tokenId in (?1)")
    List<EthEnsInfoModel> listEnsByIds(List<String> tokenIds);
}
