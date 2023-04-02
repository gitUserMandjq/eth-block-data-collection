package com.eth.event.dao.impl;

import com.eth.event.dao.EthEventTransferDao2;
import com.eth.event.model.EthEventTransferDTO;
import com.eth.event.model.EthEventTransferQO;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Slf4j
public class EthEventTransferDaoImpl implements EthEventTransferDao2 {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<EthEventTransferDTO> listFromGroup(String address, List<EthEventTransferQO> qos) {
        StringBuilder strSql = new StringBuilder();
        strSql.append(" select `from`,sum(`count`) count, sum(`sum`) sum from(");
        for(int i=0;i<qos.size();i++){
            if(i > 0){
                strSql.append(" union");
            }
            strSql.append(" select e.from_address from,count(1) count, sum(tokenValue) sum from eth_event_transfer e" +
                    " where e.tokenAddress = :address" +
                    " and e.timestamp >= :startTime"+i+
                    " and e.timestamp < :endTime" + i +
                    " group by e.from_address");
        }
        strSql.append(") T group by `from`");
        Query query = em.createNativeQuery(strSql.toString());
        query.setParameter("address", address);
        for(int i=0;i<qos.size();i++){
            EthEventTransferQO qo = qos.get(i);
            query.setParameter("startTime"+i, qo.getStartTime())
                    .setParameter("endTime"+i, qo.getEndTime());
        }
        query.unwrap(NativeQuery.class).addScalar("from", StandardBasicTypes.STRING)
                .addScalar("count", StandardBasicTypes.INTEGER)
                .addScalar("sum", StandardBasicTypes.BIG_INTEGER)
                .setResultTransformer(Transformers.aliasToBean(EthEventTransferDTO.class));;
        return query.getResultList();
    }
    @Override
    public List<EthEventTransferDTO> listToGroup(String address, List<EthEventTransferQO> qos) {
        StringBuilder strSql = new StringBuilder();
        strSql.append(" select `to`,sum(`count`) count, sum(`sum`) sum from(");
        for(int i=0;i<qos.size();i++){
            if(i > 0){
                strSql.append(" union");
            }
            strSql.append(" select e.to_address to,count(1) count, sum(tokenValue) sum from eth_event_transfer e" +
                    " where e.tokenAddress = :address" +
                    " and e.timestamp >= :startTime"+i+
                    " and e.timestamp < :endTime" + i +
                    " group by e.to_address");
        }
        strSql.append(") T group by `to`");
        Query query = em.createNativeQuery(strSql.toString());
        query.setParameter("address", address);
        for(int i=0;i<qos.size();i++){
            EthEventTransferQO qo = qos.get(i);
            query.setParameter("startTime"+i, qo.getStartTime())
                    .setParameter("endTime"+i, qo.getEndTime());
        }
        query.unwrap(NativeQuery.class).addScalar("to", StandardBasicTypes.STRING)
                .addScalar("count", StandardBasicTypes.INTEGER)
                .addScalar("sum", StandardBasicTypes.BIG_INTEGER)
                .setResultTransformer(Transformers.aliasToBean(EthEventTransferDTO.class));;
        return query.getResultList();
    }
}
