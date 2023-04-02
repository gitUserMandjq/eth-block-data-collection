package com.eth.listener.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "eth_block_listener")
@Data
public class EthBlockListenerModel {
    @Id
    @Column(name="id")
    private String id;//主键id=监听器类型+监听器地址+监听器名称
    @Column(name="block_number")
    private Long blockNumber;//区块高度
    @Column(name="created_at")
    private Date createdAt;//系统时间
    @Column(name="updated_at")
    private Date updatedAt;//系统时间
    @Column(name="type")
    private String type;//监听器类型（event事件）
    @Column(name="event")
    private String event;//监听事件名称（Transfer）
    @Column(name="contract_address")
    private String contractAddress;//监听合约地址
    @Column(name="status")
    private String status;//状态 0停用 1启用
    public boolean isEnable(){
        if("1".equals(status)){
            return true;
        }
        return false;
    }
}
