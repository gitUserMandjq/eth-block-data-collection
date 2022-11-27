package com.eth.framework.base.sysMessage.dao.impl;

import com.eth.framework.base.sysMessage.dao.SysMessageDao2;
import com.eth.framework.base.sysMessage.model.SysMessageModel;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.eth.framework.base.common.utils.StringUtils.montageInsertSql;
import static com.eth.framework.base.common.utils.StringUtils.transSqlValue;

public class SysMessageDaoImpl implements SysMessageDao2 {
    @PersistenceContext
    private EntityManager em;
    @Override
    public void batchInsertModel(List<SysMessageModel> mList) throws Exception {
        if(mList != null && !mList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            String insertSql = "INSERT INTO `ethereum_ens`.`sys_message`(`type`, `message`, `create_time`, `block_number`, `cost_time`) VALUES";
            sb.append(insertSql);
            int i = 0;
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for(SysMessageModel m:mList){
                sb.append(montageInsertSql(
                        transSqlValue(m.getType())
                        ,transSqlValue(m.getMessage())
                        ,transSqlValue(m.getCreateTime(), yyyyMMddHHmmss)
                        ,transSqlValue(m.getBlockNumber())
                        ,transSqlValue(m.getCostTime())
                ) + "\n,");
                i++;
                if(i == 1000) {
                    sb.deleteCharAt(sb.length() - 1);
                    Query query = em.createNativeQuery(sb.toString());
                    int count = query.executeUpdate();
                    sb = new StringBuilder(insertSql);
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
