package com.eth.nft.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "eth_nft_info")
@Data
public class EthNftInfoModel {

    @Id
    @Column(name="tokenId")
    private String tokenId;//主键，nft编号
    @Column(name="name")
    private String name;//nft名称
    @Column(name="createDate")
    private Date createDate;//创建时间
    @Column(name="image")
    private String image;//图片
    @Column(name="backgroundImage")
    private String backgroundImage;//背景图片
    @Column(name="url")
    private String url;//在ens上查看的路径
    @Column(name="constractAddress")
    private String constractAddress;//合约地址
    @Column(name="owner")
    private String owner;//目前所有人地址
    @Column(name="lastTxnHash")
    private String lastTxnHash;//最近交易hash
    @Column(name="lastTxnTime")
    private Date lastTxnTime;//最近交易时间
    @Column(name="lastTxnFee")
    private Long lastTxnFee;//最近交易金额
    @Column(name="meta")
    private String meta;//meta信息
    @Column(name="openSeaPrice")
    private Long openSeaPrice;//价格
    @Column(name="openSeaPriceUpdatedTime")
    private Date openSeaPriceUpdatedTime;//价格最近更新时间
    @Column(name="openSeaPriceToken")
    private String openSeaPriceToken;//使用的代币，ETH之类的
    @Column(name="openSeaAuction")
    private Integer openSeaAuction;//是否拍卖
    @Column(name="openSeaAuctionType")
    private String openSeaAuctionType;//拍卖类型
    @Column(name="openSeaListingDate")
    private Date openSeaListingDate;//上市时间
    public static final Long createBlockHeight = 9380422L;//合约创建高度
    public static final String tokenType = "ERC721";
}
