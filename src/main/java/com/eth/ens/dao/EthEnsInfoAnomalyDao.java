package com.eth.ens.dao;

import com.eth.ens.model.EthEnsInfoAnomalyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EthEnsInfoAnomalyDao extends JpaRepository<EthEnsInfoAnomalyModel,String>{
}
