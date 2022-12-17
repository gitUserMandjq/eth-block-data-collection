package com.eth.ens.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "eth_ens_info_anomaly")
@Data
public class EthEnsInfoAnomalyModel {

    @Id
    @Column(name="tokenId")
    private String tokenId;//主键，nft编号
    @Column(name="createDate")
    private Date createDate;//创建时间
    @Column(name="image")
    private String image;//图片
    @Column(name="backgroundImage")
    private String backgroundImage;//背景图片
    @Column(name="url")
    private String url;//在ens上查看的路径
    @Column(name="characterSet")
    private String characterSet;//文字类型
    @Column(name="constractAddress")
    private String constractAddress;//合约地址
    @Column(name="domain")
    private String domain;//域名
    @Column(name="owner")
    private String owner;//目前所有人地址
    @Column(name="lastTxnHash")
    private String lastTxnHash;//最近交易hash
    @Column(name="lastTxnTime")
    private Date lastTxnTime;//最近交易时间
    @Column(name="lastTxnFee")
    private Long lastTxnFee;//最近交易金额
    @Column(name="normailized")
    private Integer normailized;//是否规范
    @Column(name="expirationDate")
    private Date expirationDate;//过期时间
    @Column(name="length")
    private Integer length;//字符长度
    @Column(name="lettersOnly")
    private Integer lettersOnly;//只有文字
    @Column(name="hasNumbers")
    private Integer hasNumbers;//包含数字
    @Column(name="hasUnicode")
    private Integer hasUnicode;//包含unicode
    @Column(name="hasEmoji")
    private Integer hasEmoji;//包含表情
    @Column(name="hasInvisibles")
    private Integer hasInvisibles;//包含隐藏字符
    @Column(name="registrationDate")
    private Date registrationDate;//注册时间
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

    public EthEnsInfoAnomalyModel(EthEnsInfoModel model) {
        this.tokenId = model.getTokenId();
        this.createDate = model.getCreateDate();
        this.image = model.getImage();
        this.backgroundImage = model.getBackgroundImage();
        this.url = model.getUrl();
        this.characterSet = model.getCharacterSet();
        this.constractAddress = model.getConstractAddress();
        this.domain = model.getDomain();
        this.owner = model.getOwner();
        this.lastTxnHash = model.getLastTxnHash();
        this.lastTxnTime = model.getLastTxnTime();
        this.lastTxnFee = model.getLastTxnFee();
        this.normailized = model.getNormailized();
        this.expirationDate = model.getExpirationDate();
        this.length = model.getLength();
        this.lettersOnly = model.getLettersOnly();
        this.hasNumbers = model.getHasNumbers();
        this.hasUnicode = model.getHasUnicode();
        this.hasEmoji = model.getHasEmoji();
        this.hasInvisibles = model.getHasInvisibles();
        this.registrationDate = model.getRegistrationDate();
        this.meta = model.getMeta();
        this.openSeaPrice = model.getOpenSeaPrice();
        this.openSeaPriceUpdatedTime = model.getOpenSeaPriceUpdatedTime();
        this.openSeaPriceToken = model.getOpenSeaPriceToken();
        this.openSeaAuction = model.getOpenSeaAuction();
        this.openSeaAuctionType = model.getOpenSeaAuctionType();
        this.openSeaListingDate = model.getOpenSeaListingDate();
    }

    public static final Long createBlockHeight = 9380422L;//合约创建高度
    public static final String tokenType = "ERC721";

    public EthEnsInfoAnomalyModel() {

    }

    @Override
    public String toString() {
        return "EthEnsInfoModel{" +
                "tokenId='" + tokenId + '\'' +
                ", createDate=" + createDate +
                ", image='" + image + '\'' +
                ", backgroundImage='" + backgroundImage + '\'' +
                ", url='" + url + '\'' +
                ", characterSet='" + characterSet + '\'' +
                ", constractAddress='" + constractAddress + '\'' +
                ", domain='" + domain + '\'' +
                ", owner='" + owner + '\'' +
                ", lastTxnHash='" + lastTxnHash + '\'' +
                ", lastTxnTime=" + lastTxnTime +
                ", lastTxnFee=" + lastTxnFee +
                ", normailized=" + normailized +
                ", expirationDate=" + expirationDate +
                ", length=" + length +
                ", lettersOnly=" + lettersOnly +
                ", hasNumbers=" + hasNumbers +
                ", hasUnicode=" + hasUnicode +
                ", hasEmoji=" + hasEmoji +
                ", hasInvisibles=" + hasInvisibles +
                ", registrationDate=" + registrationDate +
                ", meta='" + meta + '\'' +
                ", openSeaPrice=" + openSeaPrice +
                ", openSeaPriceUpdatedTime=" + openSeaPriceUpdatedTime +
                ", openSeaPriceToken='" + openSeaPriceToken + '\'' +
                ", openSeaAuction=" + openSeaAuction +
                ", openSeaAuctionType='" + openSeaAuctionType + '\'' +
                ", openSeaListingDate=" + openSeaListingDate +
                '}';
    }
}
