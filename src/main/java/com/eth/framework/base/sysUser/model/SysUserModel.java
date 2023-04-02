package com.eth.framework.base.sysUser.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "sys_user")
@Data
public class SysUserModel {
    @Id
    @Column(name="user_id")
    private Long user_id;//用户id
    @Column(name="user_address")
    private String user_address;//用户钱包地址
    @Column(name="user_name")
    private String user_name;//用户名称
    @Column(name="user_nick_name")
    private String user_nick_name;//用户昵称
    @Column(name="last_login_time")
    private Date last_login_time;//最后登录时间
    @Column(name="create_time")
    private Date create_time;//创建时间
    @Column(name="last_update_time")
    private Date last_update_time;//最后更新时间
    @Column(name="enabled")
    private Integer enabled;//是否可用（0不可用1可用）
    public static final String LOGIN_USER = "login_user";
}
