package com.eth.ens.model;

import lombok.Data;

import java.util.Date;
@Data
public class EnsDomainsDTO {
    private String token_id;//主键，nft编号
    private Date create_date;//创建时间
    private String domain;//域名
    private Integer normailized;//是否规范
    private Date expiration_date;//过期时间
    private Integer length;//字符长度
    private Integer letters_only;//只有文字
    private Integer has_numbers;//包含数字
    private Integer has_unicode;//包含unicode
    private Integer has_emoji;//包含表情
    private Integer has_invisibles;//包含隐藏字符
    private Date registration_date;//注册时间
    private Long open_sea_price;//价格（暂时没有）
    private Date open_sea_price_updated_time;//价格最近更新时间（暂时没有）
    private String open_sea_price_token;//使用的代币，ETH之类的（暂时没有）
    private Integer open_sea_auction;//是否拍卖（暂时没有）
    private String open_sea_auction_type;//拍卖类型（暂时没有）
    private Date open_sea_listing_date;//上市时间（暂时没有）
}
