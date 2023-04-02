package com.eth.etlTask.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "eth_task_process")
@Data
public class EtlTaskProcessModel {
    @Id
    @Column(name="id")
    private Long id;//主键,自增长
    //开始时间
    @Column(name="starttime")
    private Date starttime;
    //结束时间
    @Column(name="endtime")
    private Date endtime;
    //开始区块
    @Column(name="start_block_number")
    private Long startBlockNumber;
    //结束区块
    @Column(name="end_block_number")
    private Long endBlockNumber;
    //当前计算区块
    @Column(name="current_block_number")
    private Long currentBlockNumber;
    //状态（进行中，完成）
    @Column(name="status")
    private String status;
    public static final String STATUS_NEW = "开始";
    public static final String STATUS_PROCESSED = "进行中";
    public static final String STATUS_FINISH = "完成";
    @Column(name="created_at")
    private Date createdAt;
    @Column(name="updated_at")
    private Date updatedAt;
    //类型
    @Column(name="type")
    private String type;
}
