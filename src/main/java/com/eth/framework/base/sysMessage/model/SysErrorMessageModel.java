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
    @Column(name="id")
    private Long id;//主键
    @Column(name="type")
    private String type;//类型
    @Column(name="message")
    private String message;//错误内容
    @Column(name="create_time")
    private Date createTime;//提醒时间
    @Column(name="status")
    private Integer status;//状态（0未处理1已处理）
    @Column(name="block_number")
    private Long blockNumber;//区块高度
}
