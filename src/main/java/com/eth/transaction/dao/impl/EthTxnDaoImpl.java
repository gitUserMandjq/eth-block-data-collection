package com.eth.transaction.dao.impl;

import com.eth.transaction.dao.EthTxnDao2;
import com.eth.transaction.model.EthTxnModel;

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
