package com.eth.ens.dao.impl;

import com.eth.ens.dao.EthEnsInfoDao2;
import com.eth.ens.model.EnsDomainsDTO;
import com.eth.ens.model.EnsDomainsQO;
import com.eth.framework.base.common.model.PageParam;
import com.eth.framework.base.common.utils.PageUtils;
import com.eth.framework.base.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;
@Slf4j
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
        listEnsDomainWhere(qo, strSql);
        strSql.append(PageUtils.initPageSql(pageParam));
        Query query = em.createNativeQuery(strSql.toString());
        listEnsDomainSetParam(qo, query);
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

    private static void listEnsDomainSetParam(EnsDomainsQO qo, Query query) {
        if(!StringUtils.isEmpty(qo.getDomain())){
            query.setParameter("domain", qo.getDomain());
        }
        if(!StringUtils.isEmpty(qo.getExpiration_date_start())){
            query.setParameter("expirationStartTime", qo.getExpiration_date_start());
        }
        if(!StringUtils.isEmpty(qo.getExpiration_date_end())){
            query.setParameter("expirationEndTime", qo.getExpiration_date_end());
        }
        if(qo.getLength_min() != null){
            query.setParameter("lengthMin", qo.getLength_min());
        }
        if(qo.getLength_max() != null){
            query.setParameter("lengthMax", qo.getLength_max());
        }
        if(!StringUtils.isEmpty(qo.getStarts_with())){
            query.setParameter("startsWith", qo.getStarts_with());
        }
        if(!StringUtils.isEmpty(qo.getEnds_with())){
            query.setParameter("endsWith", qo.getEnds_with());
        }
        if(!StringUtils.isEmpty(qo.getLetters_only())){
            query.setParameter("lettersOnly", qo.getLetters_only());
        }
        if(!StringUtils.isEmpty(qo.getHas_numbers())){
            query.setParameter("hasNumbers", qo.getHas_numbers());
        }
        if(!StringUtils.isEmpty(qo.getHas_unicode())){
            query.setParameter("hasUnicode", qo.getHas_unicode());
        }
        if(!StringUtils.isEmpty(qo.getHas_emoji())){
            query.setParameter("hasEmoji", qo.getHas_emoji());
        }
        if(!StringUtils.isEmpty(qo.getHas_invisibles())){
            query.setParameter("hasInvisibles", qo.getHas_invisibles());
        }
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
        listEnsDomainWhere(qo, strSql);
        Query query = em.createNativeQuery(strSql.toString());
        listEnsDomainSetParam(qo, query);
        return new BigInteger(query.getSingleResult().toString()).intValue();
    }


    private static void listEnsDomainWhere(EnsDomainsQO qo, StringBuilder strSql) {
        if(!StringUtils.isEmpty(qo.getDomain())){
            strSql.append(" and en.domain like concat('%',:domain,'%')");
        }
        if(!StringUtils.isEmpty(qo.getExpiration_date_start())){
            strSql.append(" and en.expiration_date >= :expirationStartTime");
        }
        if(!StringUtils.isEmpty(qo.getExpiration_date_end())){
            strSql.append(" and en.expiration_date <= :expirationEndTime");
        }
        if(qo.getLength_min() != null){
            strSql.append(" and en.length >= :lengthMin");
        }
        if(qo.getLength_max() != null){
            strSql.append(" and en.length <= :lengthMax");
        }
        if(!StringUtils.isEmpty(qo.getStarts_with())){
            strSql.append(" and en.domain like concat(:startsWith,'%')");
        }
        if(!StringUtils.isEmpty(qo.getEnds_with())){
            strSql.append(" and en.domain like concat('%',:endsWith)");
        }
        if(!StringUtils.isEmpty(qo.getLetters_only())){
            strSql.append(" and en.letters_only = :lettersOnly");
        }
        if(!StringUtils.isEmpty(qo.getHas_numbers())){
            strSql.append(" and en.has_numbers = :hasNumbers");
        }
        if(!StringUtils.isEmpty(qo.getHas_unicode())){
            strSql.append(" and en.has_unicode = :hasUnicode");
        }
        if(!StringUtils.isEmpty(qo.getHas_emoji())){
            strSql.append(" and en.has_emoji = :hasEmoji");
        }
        if(!StringUtils.isEmpty(qo.getHas_invisibles())){
            strSql.append(" and en.has_invisibles = :hasInvisibles");
        }
    }
//    @Override
//    public void batchInsertModel(List<EthEnsInfoModel> mList) throws Exception {
//        if(mList != null && !mList.isEmpty()) {
//            StringBuilder sb = new StringBuilder();
//            String insertSql = "REPLACE INTO `ethereum_ens`.`eth_ens_info`(`token_id`, `create_date`, `image`, `background_image`, `url`, `character_set`" +
//                    ", `constract_address`, `domain`, `owner`, `last_txn_hash`, `last_txn_time`, `last_txn_fee`, `normailized`, `expiration_date`" +
//                    ", `length`, `letters_only`, `has_numbers`, `has_unicode`, `has_emoji`, `has_invisibles`, `registration_date`, `meta`, `open_sea_price`" +
//                    ", `open_sea_price_updated_time`, `open_sea_price_token`, `open_sea_auction`, `open_sea_auction_type`, `open_sea_listing_date`) VALUES";
//            sb.append(insertSql);
//            int i = 0;
//            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            for(EthEnsInfoModel m:mList){
//                sb.append(montageInsertSql(
//                        transSqlValue(m.getTokenId())
//                        ,transSqlValue(m.getCreateDate(), yyyyMMddHHmmss)
//                        ,transSqlValue(m.getImage())
//                        ,transSqlValue(m.getBackgroundImage())
//                        ,transSqlValue(m.getUrl())
//                        ,transSqlValue(m.getCharacterSet())
//                        ,transSqlValue(m.getConstractAddress())
//                        ,transSqlValue(m.getDomain())
//                        ,transSqlValue(m.getOwner())
//                        ,transSqlValue(m.getLastTxnHash())
//                        ,transSqlValue(m.getLastTxnTime(), yyyyMMddHHmmss)
//                        ,transSqlValue(m.getLastTxnFee())
//                        ,transSqlValue(m.getNormailized())
//                        ,transSqlValue(m.getExpirationDate(), yyyyMMddHHmmss)
//                        ,transSqlValue(m.getLength())
//                        ,transSqlValue(m.getLettersOnly())
//                        ,transSqlValue(m.getHasNumbers())
//                        ,transSqlValue(m.getHasUnicode())
//                        ,transSqlValue(m.getHasEmoji())
//                        ,transSqlValue(m.getHasInvisibles())
//                        ,transSqlValue(m.getRegistrationDate(), yyyyMMddHHmmss)
//                        ,transSqlValue(m.getMeta())
//                        ,transSqlValue(m.getOpenSeaPrice())
//                        ,transSqlValue(m.getOpenSeaPriceUpdatedTime(), yyyyMMddHHmmss)
//                        ,transSqlValue(m.getOpenSeaPriceToken())
//                        ,transSqlValue(m.getOpenSeaAuction())
//                        ,transSqlValue(m.getOpenSeaAuctionType())
//                        ,transSqlValue(m.getOpenSeaListingDate(), yyyyMMddHHmmss)
//                ) + ",\n");
//                i++;
//                if(i == 1000) {
//                    sb.deleteCharAt(sb.length() - 1);
//                    Query query = em.createNativeQuery(sb.toString());
//                    int count = query.executeUpdate();
//                    sb = new StringBuilder(insertSql);
//                    i = 0;
//                }
//            }
//            if(i > 0) {
//                sb.deleteCharAt(sb.length() - 1);
//                Query query = em.createNativeQuery(sb.toString());
//                int count = query.executeUpdate();
//            }
//        }
//    }
}
