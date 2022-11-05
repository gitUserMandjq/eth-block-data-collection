package com.eth.framework.base.sysUser.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "sys_user")
@Data
public class SysUserModel {
    @Id
    private Long user_id;//用户id
    private String user_address;//用户钱包地址
    private String user_name;//用户名称
    private String user_nick_name;//用户昵称
    private Date last_login_time;//最后登录时间
    private Date create_time;//创建时间
    private Date last_update_time;//最后更新时间
    private Integer enabled;//是否可用（0不可用1可用）
    public static final String LOGIN_USER = "login_user";
}
