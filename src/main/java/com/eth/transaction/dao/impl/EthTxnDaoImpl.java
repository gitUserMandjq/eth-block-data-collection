package com.eth.transaction.dao.impl;

import com.eth.transaction.dao.EthTxnDao2;
import com.eth.transaction.model.EthTxnModel;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

import static com.eth.framework.base.common.utils.StringUtils.montageInsertSql;
import static com.eth.framework.base.common.utils.StringUtils.transSqlValue;


public class EthTxnDaoImpl implements EthTxnDao2 {
    @PersistenceContext
    private EntityManager em;
    @Override
    public void batchInsertTxn(Map<String, EthTxnModel> mMap) throws Exception {
        if(mMap != null && !mMap.isEmpty()) {
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT Ignore INTO `eth_txns`(`txn_hash`, `block_number`, `txn_index`" +
                    ", `from_address`, `to_address`, `eth_value`, `gas_used`, `gas_price`, `gas_fee`, `status`, `nonce`" +
                    ", `timestamp`, `contract_address`, `cumulative_gas_used`, `effective_gas_price`, `input`, `max_fee_per_gas`" +
                    ", `max_priority_fee_per_gas`, `is_error`, `err_msg`, `method_id`, `type`, `logs_num`, `created_at`, `updated_at`) VALUES");
            int i = 0;
            Iterator<Map.Entry<String, EthTxnModel>> iterator = mMap.entrySet().iterator();
            while(iterator.hasNext()) {
                EthTxnModel m = iterator.next().getValue();
                sb.append(montageInsertSql(
                        transSqlValue(m.getTxnHash())
                        ,transSqlValue(m.getBlockNumber())
                        ,transSqlValue(m.getTxnIndex())
                        ,transSqlValue(m.getFromAddress())
                        ,transSqlValue(m.getToAddress())
                        ,transSqlValue(m.getEthValue())
                        ,transSqlValue(m.getGasUsed())
                        ,transSqlValue(m.getGasPrice())
                        ,transSqlValue(m.getGasFee())
                        ,transSqlValue(m.getStatus())
                        ,transSqlValue(m.getNonce())
                        ,transSqlValue(m.getTimestamp())
                        ,transSqlValue(m.getContractAddress())
                        ,transSqlValue(m.getCumulativeGasUsed())
                        ,transSqlValue(m.getEffectiveGasPrice())
                        ,transSqlValue(m.getInput())
                        ,transSqlValue(m.getMaxFeePerGas())
                        ,transSqlValue(m.getMaxPriorityFeePerGas())
                        ,transSqlValue(m.getIsError())
                        ,transSqlValue(m.getErrMsg())
                        ,transSqlValue(m.getMethodId())
                        ,transSqlValue(m.getType())
                        ,transSqlValue(m.getLogsNum())
                        ,transSqlValue(m.getCreatedAt(), yyyyMMddHHmmss)
                        ,transSqlValue(m.getUpdatedAt(), yyyyMMddHHmmss)
                ) + "\n,");
                i++;
                if(i == 1000) {
                    sb.deleteCharAt(sb.length() - 1);
                    Query query = em.createNativeQuery(sb.toString());
                    int count = query.executeUpdate();
                    sb = new StringBuilder("INSERT Ignore INTO `eth_txns`(`txn_hash`, `block_number`, `txn_index`" +
                            ", `from_address`, `to_address`, `eth_value`, `gas_used`, `gas_price`, `gas_fee`, `status`, `nonce`, `timestamp`" +
                            ", `contract_address`, `cumulative_gas_used`, `effective_gas_price`, `input`, `max_fee_per_gas`, `max_priority_fee_per_gas`" +
                            ", `is_error`, `err_msg`, `method_id`, `type`, `logs_num`, `created_at`, `updated_at`) VALUES");
                    i = 0;
                }
            }
            if(i > 0) {
                sb.deleteCharAt(sb.length() - 1);
                Query query = em.createNativeQuery(sb.toString());
                int count = query.executeUpdate();
            }
        }
    }

    /**
     * 批量新增
     * @param txn
     * @throws Exception
     */
    @Override
    @Transactional
    public void batchInsertTxn(EthTxnModel txn) throws Exception{

        StringBuilder sb = new StringBuilder("INSERT Ignore INTO `eth_txns`(`txn_hash`, `block_number`, `txn_index`" +
                ", `from_address`, `to_address`, `eth_value`, `gas_used`, `gas_price`, `gas_fee`, `status`, `nonce`" +
                ", `timestamp`, `contract_address`, `cumulative_gas_used`, `effective_gas_price`, `input`, `max_fee_per_gas`" +
                ", `max_priority_fee_per_gas`, `is_error`, `err_msg`, `method_id`, `type`, `logs_num`, `created_at`, `updated_at`) VALUES" +
                "(:txn_hash,:block_number,:txn_index,:from_address,:to_address,:eth_value,:gas_used,:gas_price,:gas_fee,:status,:nonce" +
                ",:timestamp,:contract_address,:cumulative_gas_used,:effective_gas_price,:input,:max_fee_per_gas,:max_priority_fee_per_gas" +
                ",:is_error,:err_msg,:method_id,:type,:logs_num,:created_at,:updated_at)");
        Query query = em.createNativeQuery(sb.toString()).setParameter("txn_hash",txn.getTxnHash())
                .setParameter("block_number",txn.getBlockNumber())
                .setParameter("txn_index",txn.getTxnIndex())
                .setParameter("from_address",txn.getFromAddress())
                .setParameter("to_address",txn.getToAddress())
                .setParameter("eth_value",txn.getEthValue())
                .setParameter("gas_used",txn.getGasUsed())
                .setParameter("gas_price",txn.getGasPrice())
                .setParameter("gas_fee",txn.getGasFee())
                .setParameter("status",txn.getStatus())
                .setParameter("nonce",txn.getNonce())
                .setParameter("timestamp",txn.getTimestamp())
                .setParameter("contract_address",txn.getContractAddress())
                .setParameter("cumulative_gas_used",txn.getCumulativeGasUsed())
                .setParameter("effective_gas_price",txn.getEffectiveGasPrice())
                .setParameter("input",txn.getInput())
                .setParameter("max_fee_per_gas",txn.getMaxFeePerGas())
                .setParameter("max_priority_fee_per_gas",txn.getMaxPriorityFeePerGas())
                .setParameter("is_error",txn.getIsError())
                .setParameter("err_msg",txn.getErrMsg())
                .setParameter("method_id",txn.getMethodId())
                .setParameter("type",txn.getType())
                .setParameter("logs_num",txn.getLogsNum())
                .setParameter("created_at",txn.getCreatedAt())
                .setParameter("updated_at",txn.getUpdatedAt());
        query.executeUpdate();
    }
    @Override
    public void batchInsertTxnEns(Map<String, EthTxnModel> mMap) throws Exception {
        if(mMap != null && !mMap.isEmpty()) {
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT Ignore INTO `eth_txns_ens`(`txn_hash`, `block_number`, `txn_index`" +
                    ", `from_address`, `to_address`, `eth_value`, `gas_used`, `gas_price`, `gas_fee`, `status`, `nonce`" +
                    ", `timestamp`, `contract_address`, `cumulative_gas_used`, `effective_gas_price`, `input`, `max_fee_per_gas`" +
                    ", `max_priority_fee_per_gas`, `is_error`, `err_msg`, `method_id`, `type`, `logs_num`, `created_at`, `updated_at`) VALUES");
            int i = 0;
            Iterator<Map.Entry<String, EthTxnModel>> iterator = mMap.entrySet().iterator();
            while(iterator.hasNext()) {
                EthTxnModel m = iterator.next().getValue();
                sb.append(montageInsertSql(
                        transSqlValue(m.getTxnHash())
                        ,transSqlValue(m.getBlockNumber())
                        ,transSqlValue(m.getTxnIndex())
                        ,transSqlValue(m.getFromAddress())
                        ,transSqlValue(m.getToAddress())
                        ,transSqlValue(m.getEthValue())
                        ,transSqlValue(m.getGasUsed())
                        ,transSqlValue(m.getGasPrice())
                        ,transSqlValue(m.getGasFee())
                        ,transSqlValue(m.getStatus())
                        ,transSqlValue(m.getNonce())
                        ,transSqlValue(m.getTimestamp())
                        ,transSqlValue(m.getContractAddress())
                        ,transSqlValue(m.getCumulativeGasUsed())
                        ,transSqlValue(m.getEffectiveGasPrice())
                        ,transSqlValue(m.getInput())
                        ,transSqlValue(m.getMaxFeePerGas())
                        ,transSqlValue(m.getMaxPriorityFeePerGas())
                        ,transSqlValue(m.getIsError())
                        ,transSqlValue(m.getErrMsg())
                        ,transSqlValue(m.getMethodId())
                        ,transSqlValue(m.getType())
                        ,transSqlValue(m.getLogsNum())
                        ,transSqlValue(m.getCreatedAt(), yyyyMMddHHmmss)
                        ,transSqlValue(m.getUpdatedAt(), yyyyMMddHHmmss)
                ) + "\n,");
                i++;
                if(i == 1000) {
                    sb.deleteCharAt(sb.length() - 1);
                    Query query = em.createNativeQuery(sb.toString());
                    int count = query.executeUpdate();
                    sb = new StringBuilder("INSERT Ignore INTO `eth_txns_ens`(`txn_hash`, `block_number`, `txn_index`" +
                            ", `from_address`, `to_address`, `eth_value`, `gas_used`, `gas_price`, `gas_fee`, `status`, `nonce`, `timestamp`" +
                            ", `contract_address`, `cumulative_gas_used`, `effective_gas_price`, `input`, `max_fee_per_gas`, `max_priority_fee_per_gas`" +
                            ", `is_error`, `err_msg`, `method_id`, `type`, `logs_num`, `created_at`, `updated_at`) VALUES");
                    i = 0;
                }
            }
            if(i > 0) {
                sb.deleteCharAt(sb.length() - 1);
                Query query = em.createNativeQuery(sb.toString());
                int count = query.executeUpdate();
            }
        }
    }

}
