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
    private Long id;//主键
    private String type;//类型
    private String message;//消息内容
    private Date createTime;//提醒时间
    private Long blockNumber;//区块高度
    private Long costTime;//消耗时间
    public static final String TYPE_ETHTASK = "ethtask";
    public static final String TYPE_COMTASK = "comtask";
}
