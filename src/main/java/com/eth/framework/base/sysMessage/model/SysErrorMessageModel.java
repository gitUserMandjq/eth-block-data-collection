package com.eth.framework.base.sysMessage.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sys_error_message")
@Data
public class SysErrorMessageModel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;//主键
    private String type;//类型
    private String message;//错误内容
    private Date createTime;//提醒时间
    private Integer status;//状态（0未处理1已处理）
    private Long blockNumber;//区块高度
    public static final String TYPE_ETHTASK = "ethtask";
    public static final String TYPE_COMTASK = "comtask";
    public static final String TYPE_BLOCKTASK = "blocktask";
}
