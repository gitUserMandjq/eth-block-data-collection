package com.eth.framework.base.sysMessage.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sys_message")
@Data
public class SysMessageModel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;//主键
    @Column(name="type")
    private String type;//类型
    @Column(name="message")
    private String message;//消息内容
    @Column(name="create_time")
    private Date createTime;//提醒时间
    @Column(name="block_number")
    private Long blockNumber;//区块高度
    @Column(name="cost_time")
    private Long costTime;//消耗时间
}
