package com.eth.ens.dao;

import com.eth.ens.model.EthEnsInfoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EthEnsInfoDao extends JpaRepository<EthEnsInfoModel,String> ,EthEnsInfoDao2{

}
