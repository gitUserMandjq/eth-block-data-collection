package com.eth.etlTask.dao;

import com.eth.etlTask.model.EtlTaskProcessModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface EtlTaskProcessDao extends JpaRepository<EtlTaskProcessModel,Long>{
    /**
     * 查询经过某个时间段的任务
     * @param type
     * @param starttime
     * @param endtime
     * @return
     */
    @Query(value="select u from EtlTaskProcessModel u where u.type = ?1" +
            " and u.endtime >?2 and " +
            "(u.starttime <= ?2 and u.endtime >= ?2" +
            " or u.starttime<= ?3 and u.endtime >= ?3" +
            " or u.starttime > ?2 and u.endtime < ?3)" +
            " order by u.starttime asc,u.endtime asc")
    List<EtlTaskProcessModel> listEtlTaskProcessPass(String type, Date starttime, Date endtime);
    /**
     * 查询某个任务
     * @param type
     * @param starttime
     * @param endtime
     * @return
     */
    @Query(value="select u from EtlTaskProcessModel u where u.type = ?1 and u.starttime = ?2 and u.endtime = ?2" +
            " order by u.endtime desc")
    List<EtlTaskProcessModel> listEtlTaskProcessEquals(String type, Date starttime, Date endtime);
}
