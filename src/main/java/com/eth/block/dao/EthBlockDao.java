package com.eth.block.dao;

import com.eth.block.model.EthBlockModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EthBlockDao  extends JpaRepository<EthBlockModel,Long> {
}
