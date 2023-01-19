package com.eth.ens.model;

import lombok.Data;

import java.util.Date;
@Data
public class EnsDomainsQO {
    private String domain;//查询域名
    private Date expiration_date_start;//过期开始时间
    private Date expiration_date_end;//过期结束时间
    private Integer length_min;//最小长度
    private Integer length_max;//最大长度
    private String starts_with;//以某字符开始
    private String ends_with;//以某字符结束
    private String letters_only;//只有文字（0否1是）
    private String has_numbers;//包含数字（0否1是）
    private String has_unicode;//包含unicode（0否1是）
    private String has_emoji;//包含表情（0否1是）
    private String has_invisibles;//包含隐藏字符（0否1是）
}
