package com.eth.ens.dao.impl;

import com.eth.ens.dao.EthEnsInfoDao2;
import com.eth.ens.model.EnsDomainsDTO;
import com.eth.ens.model.EnsDomainsQO;
import com.eth.framework.base.model.PageParam;
import com.eth.framework.base.utils.PageUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
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
        StringBuilder strSql = new StringBuilder("select token_id,create_date,domain,normailized,expiration_date,length,letters_only,has_numbers,has_unicode,has_emoji,has_invisibles,registration_date,open_sea_price,open_sea_price_updated_time,open_sea_price_token,open_sea_auction,open_sea_auction_type,open_sea_listing_date" +
                " from eth_ens_info en" +
                " where 1=1");
        strSql.append(PageUtils.initPageSql(pageParam));
        Query query = em.createNativeQuery(strSql.toString());
        query.unwrap(NativeQuery.class)
                .addScalar("token_id", StandardBasicTypes.STRING)
                .addScalar("create_date", StandardBasicTypes.TIMESTAMP)
                .addScalar("domain", StandardBasicTypes.STRING)
                .addScalar("normailized", StandardBasicTypes.INTEGER)
                .addScalar("expiration_date", StandardBasicTypes.TIMESTAMP)
                .addScalar("length", StandardBasicTypes.INTEGER)
                .addScalar("letters_only", StandardBasicTypes.INTEGER)
                .addScalar("has_numbers", StandardBasicTypes.INTEGER)
                .addScalar("has_unicode", StandardBasicTypes.INTEGER)
                .addScalar("has_emoji", StandardBasicTypes.INTEGER)
                .addScalar("has_invisibles", StandardBasicTypes.INTEGER)
                .addScalar("registration_date", StandardBasicTypes.TIMESTAMP)
                .addScalar("open_sea_price", StandardBasicTypes.LONG)
                .addScalar("open_sea_price_updated_time", StandardBasicTypes.TIMESTAMP)
                .addScalar("open_sea_price_token", StandardBasicTypes.STRING)
                .addScalar("open_sea_auction", StandardBasicTypes.INTEGER)
                .addScalar("open_sea_auction_type", StandardBasicTypes.STRING)
                .addScalar("open_sea_listing_date", StandardBasicTypes.TIMESTAMP)
                .setResultTransformer(Transformers.aliasToBean(EnsDomainsDTO.class));
        return query.getResultList();
    }
    /**
     * 查询ens列表总数
     * @param qo
     * @return
     */
    @Override
    public Integer countEnsDomain(EnsDomainsQO qo) {
        StringBuilder strSql = new StringBuilder("select count(1)" +
                " from eth_ens_info en" +
                " where 1=1");

        Query query = em.createNativeQuery(strSql.toString());
        return new BigInteger(query.getSingleResult().toString()).intValue();
    }
}
