package com.eth.nft.dao;

import com.eth.framework.base.common.repository.BatchSaveRepository;
import com.eth.nft.model.EthNftInfoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EthNftInfoDao extends JpaRepository<EthNftInfoModel,String> , BatchSaveRepository<EthNftInfoModel> {
    /**
     * 通过id批量查询ens
     * @param tokenIds
     * @return
     */
    @Query(value="select u from EthNftInfoModel u where u.tokenId in (?1)")
    List<EthNftInfoModel> listNftByIds(List<String> tokenIds);
}
