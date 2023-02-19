package com.eth.ens.service.impl;

import com.eth.ens.dao.EthEnsInfoAnomalyDao;
import com.eth.ens.dao.EthEnsInfoDao;
import com.eth.ens.model.*;
import com.eth.ens.service.IEthEnsInfoService;
import com.eth.framework.base.common.model.PageData;
import com.eth.framework.base.common.model.PageParam;
import com.eth.framework.base.common.utils.AlchemyUtils;
import com.eth.framework.base.common.utils.JsonUtil;
import com.eth.framework.base.common.utils.PageUtils;
import com.eth.framework.base.common.utils.StringUtils;
import com.eth.transaction.model.EthTxnModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EthEnsInfoServiceImpl implements IEthEnsInfoService {
    @Resource
    EthEnsInfoDao ethEnsInfoDao;
    @Resource
    EthEnsInfoAnomalyDao ethEnsInfoAnomalyDao;
    /**
     * 新增或更新ens
     * @param ensDTO
     */
    @Override
    public void insertOrUpdateEns(EthEnsDTO ensDTO) throws IOException, ParseException {
        Optional<EthEnsInfoModel> one = ethEnsInfoDao.findById(ensDTO.getTokenId());
        EthEnsInfoModel ethEnsInfoModel = null;
        Map<String, Object> map = ensDTO.getMeta();
        if(!one.isPresent()){
            ethEnsInfoModel = new EthEnsInfoModel();
            ethEnsInfoModel.setTokenId(ensDTO.getTokenId());
            ethEnsInfoModel.setMeta(JsonUtil.object2String(map));
            ethEnsInfoModel.setConstractAddress(ensDTO.getAddress());
        }else{
            ethEnsInfoModel = one.get();
        }
        dealEnsMeta(ethEnsInfoModel, map);
        if(StringUtils.isEmpty(ethEnsInfoModel.getDomain())){//数据错误，解析失败
            return;
        }
        EthTxnModel txn = ensDTO.getTxn();
        if(txn != null){
            Long timestamp = txn.getTimestamp();
            if(ethEnsInfoModel.getLastTxnTime() == null || ethEnsInfoModel.getLastTxnTime().getTime() <= timestamp){
                ethEnsInfoModel.setLastTxnTime(new Date(timestamp));
                ethEnsInfoModel.setLastTxnHash(txn.getTxnHash());
                ethEnsInfoModel.setLastTxnFee(txn.getEthValue().longValue());
                ethEnsInfoModel.setOwner(ensDTO.getTo());
            }
        }
        log.info(JsonUtil.object2String(ethEnsInfoModel));
        if(ethEnsInfoModel.getDomain().length() >= 200){
            //有的ens故意设置的很长，防止报错
            addAnormalyEns(ethEnsInfoModel);
            return;
        }
        ethEnsInfoDao.save(ethEnsInfoModel);
    }
    void addAnormalyEns(EthEnsInfoModel ethEnsInfoModel){
        EthEnsInfoAnomalyModel model = new EthEnsInfoAnomalyModel(ethEnsInfoModel);
        ethEnsInfoAnomalyDao.save(model);
    }
    /**
     * 新增或更新ens
     * @param ensDTOList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertOrUpdateEns(List<EthEnsDTO> ensDTOList) throws Exception {
        if(ensDTOList.isEmpty()){
            return;
        }
        List<String> tokenIds = ensDTOList.stream().map(EthEnsDTO::getTokenId).collect(Collectors.toList());
        List<EthEnsInfoModel> ensList = ethEnsInfoDao.listEnsByIds(tokenIds);
        Map<String, EthEnsInfoModel> ensMap = ensList.stream().collect(Collectors.toMap(EthEnsInfoModel::getTokenId, v -> v));
        List<EthEnsInfoModel> addOrUpdateList = new ArrayList<>();
        for(EthEnsDTO ensDTO:ensDTOList){
            Map<String, Object> map = ensDTO.getMeta();
            EthEnsInfoModel ethEnsInfoModel = ensMap.get(ensDTO.getTokenId());
            if(ethEnsInfoModel == null){
                ethEnsInfoModel = new EthEnsInfoModel();
                ethEnsInfoModel.setTokenId(ensDTO.getTokenId());
                ethEnsInfoModel.setMeta(JsonUtil.object2String(map));
                ethEnsInfoModel.setConstractAddress(ensDTO.getAddress());
            }
            dealEnsMeta(ethEnsInfoModel, map);
            if(StringUtils.isEmpty(ethEnsInfoModel.getDomain())){//数据错误，解析失败
                continue;
            }
            EthTxnModel txn = ensDTO.getTxn();
            if(txn != null){
                Long timestamp = txn.getTimestamp();
                if(ethEnsInfoModel.getLastTxnTime() == null || ethEnsInfoModel.getLastTxnTime().getTime() <= timestamp){
                    ethEnsInfoModel.setLastTxnTime(new Date(timestamp));
                    ethEnsInfoModel.setLastTxnHash(txn.getTxnHash());
                    ethEnsInfoModel.setLastTxnFee(txn.getEthValue().longValue());
                    ethEnsInfoModel.setOwner(ensDTO.getTo());
                }
            }
            if(ethEnsInfoModel.getDomain().length() >= 200){
                //有的ens故意设置的很长，防止报错
                addAnormalyEns(ethEnsInfoModel);
                continue;
            }
            addOrUpdateList.add(ethEnsInfoModel);
        }
        if(!addOrUpdateList.isEmpty()){
            ethEnsInfoDao.batchInsertModel(addOrUpdateList);
        }
    }
    /**
     * 查询ens列表
     * @param qo
     * @param pageParam
     * @return
     */
    @Override
    public PageData<EnsDomainsDTO> listEnsDomain(EnsDomainsQO qo, PageParam pageParam) {
        List<EnsDomainsDTO> list = ethEnsInfoDao.listEnsDomain(qo, pageParam);
        Integer count = ethEnsInfoDao.countEnsDomain(qo);
        PageData<EnsDomainsDTO> pageData = PageUtils.convertPageData(list, count, pageParam);
        return pageData;
    }
    private static class EnsMeta{
        private String domain = "";
        private String image = "";
        private String backgroundImage = "";
        private String url = "";
        private Integer length = null;
        private Date createDate = null;
        private Date expirationDate = null;
        private Date registrationDate = null;
        private String characterSet = "";
        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getBackgroundImage() {
            return backgroundImage;
        }

        public void setBackgroundImage(String backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        public Date getCreateDate() {
            return createDate;
        }

        public void setCreateDate(Date createDate) {
            this.createDate = createDate;
        }

        public Date getExpirationDate() {
            return expirationDate;
        }

        public void setExpirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
        }

        public Date getRegistrationDate() {
            return registrationDate;
        }

        public void setRegistrationDate(Date registrationDate) {
            this.registrationDate = registrationDate;
        }

        public String getCharacterSet() {
            return characterSet;
        }

        public void setCharacterSet(String characterSet) {
            this.characterSet = characterSet;
        }
    }
    private static void dealEnsMeta(EthEnsInfoModel ethEnsInfoModel, Map<String, Object> map) throws IOException, ParseException {
        log.info("开始处理meta："+ethEnsInfoModel.getMeta());
        String title = (String) map.get("title");
        EnsMeta ensMeta = new EnsMeta();
        if(StringUtils.isEmpty(title)){
            //{"contract":{"address":"0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85"},"id":{"tokenId":"0x4cbedf505977ad2333f03571681875b18ea2e1837c0791da20ee246e3ea7f34c","tokenMetadata":{"tokenType":"ERC721"}},"title":"","description":"","tokenUri":{"raw":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x4cbedf505977ad2333f03571681875b18ea2e1837c0791da20ee246e3ea7f34c","gateway":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x4cbedf505977ad2333f03571681875b18ea2e1837c0791da20ee246e3ea7f34c"},"media":[{"raw":"","gateway":""}],"metadata":{"message":{"name":"unknown.name","description":"Unknown ENS name","attributes":[{"display_type":"date","value":1580346653000000,"trait_type":"Created Date"},{"display_type":"number","value":7,"trait_type":"Length"},{"display_type":"string","value":"letter","trait_type":"Character Set"}],"name_length":7,"version":0,"is_normalized":true}},"timeLastUpdated":"2022-05-14T00:57:20.478Z","contractMetadata":{"tokenType":"ERC721","openSea":{"floorPrice":8.8E-4,"collectionName":"ENS: Ethereum Name Service","safelistRequestStatus":"verified","imageUrl":"https://i.seadn.io/gae/0cOqWoYA7xL9CkUjGlxsjreSYBdrUBE0c6EO1COG4XE8UeP-Z30ckqUNiL872zHQHQU5MUNMNhfDpyXIP17hRSC5HQ?w=500&auto=format","description":"Ethereum Name Service (ENS) domains are secure domain names for the decentralized world. ENS domains provide a way for users to map human readable names to blockchain and non-blockchain resources, like Ethereum addresses, IPFS hashes, or website URLs. ENS domains can be bought and sold on secondary markets.","externalUrl":"https://ens.domains","twitterUsername":"ensdomains","lastIngestedAt":"2022-11-01T15:30:04.000Z"}}}
            //如果title为空，说明ens已经过期了，只能通过tokenUri.raw查询
            //https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x4cbedf505977ad2333f03571681875b18ea2e1837c0791da20ee246e3ea7f34c
            //{"message":"'caizhuoyan.eth' is already been expired at Mon, 04 May 2020 00:00:00 GMT."}
            log.info("过期信息："+JsonUtil.object2String(map));
            Map tokenUri = (Map) map.get("tokenUri");
            String raw = (String) tokenUri.get("raw");
            String body = AlchemyUtils.getNFTMetadataByRaw(raw);
            Map metadata = JsonUtil.string2Obj(body);
            if(raw.contains("name")){//炼金术上没了，但是metadata上还有
                //{"is_normalized":true,"name":"jasonyi.eth","description":"jasonyi.eth, an ENS name.","attributes":[{"trait_type":"Created Date","display_type":"date","value":1550721176000},{"trait_type":"Length","display_type":"number","value":7},{"trait_type":"Segment Length","display_type":"number","value":7},{"trait_type":"Character Set","display_type":"string","value":"letter"},{"trait_type":"Registration Date","display_type":"date","value":1643942268000},{"trait_type":"Expiration Date","display_type":"date","value":2369752164000}],"name_length":7,"segment_length":7,"url":"https://app.ens.domains/name/jasonyi.eth","version":0,"background_image":"https://metadata.ens.domains/mainnet/avatar/jasonyi.eth","image":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x0336d6ac0b5b1b19f742899b81862e06e950a850e495ce6e74161d66daaa3190/image","image_url":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x0336d6ac0b5b1b19f742899b81862e06e950a850e495ce6e74161d66daaa3190/image"}
                extractedMetadata(ensMeta, metadata);
            }else{
                if(body.contains(" is already been expired at ")){
                    String message = (String) metadata.get("message");
                    String[] split = message.split(" is already been expired at ");
                    ensMeta.setDomain(split[0]);
                    ensMeta.setDomain(ensMeta.getDomain().substring(1, ensMeta.getDomain().length() - 1));
                    ensMeta.setLength(ensMeta.getDomain().length() - 4);//减去.eth的长度
                    String date = split[1];
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz.", Locale.US);
                    ensMeta.setExpirationDate(sdf.parse(date));
                }else{
                    return;
                }
            }
        }else{
            //{"contract":{"address":"0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85"},"id":{"tokenId":"0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c","tokenMetadata":{"tokenType":"ERC721"}},"title":"rehbein.eth","description":"rehbein.eth, an ENS name.","tokenUri":{"raw":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/105163109881267868532041562026418602960922071549544155763322457683184960142748","gateway":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/105163109881267868532041562026418602960922071549544155763322457683184960142748"},"media":[{"raw":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image","gateway":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image"}],"metadata":{"background_image":"https://metadata.ens.domains/mainnet/avatar/rehbein.eth","image":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image","is_normalized":true,"segment_length":7,"image_url":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image","name":"rehbein.eth","description":"rehbein.eth, an ENS name.","attributes":[{"display_type":"date","value":1500099683000,"trait_type":"Created Date"},{"display_type":"number","value":7,"trait_type":"Length"},{"display_type":"number","value":7,"trait_type":"Segment Length"},{"display_type":"string","value":"letter","trait_type":"Character Set"},{"display_type":"date","value":1661223139000,"trait_type":"Registration Date"},{"display_type":"date","value":1692780091000,"trait_type":"Expiration Date"}],"name_length":7,"version":0,"url":"https://app.ens.domains/name/rehbein.eth"},"timeLastUpdated":"2022-11-07T06:22:52.532Z","contractMetadata":{"tokenType":"ERC721","openSea":{"floorPrice":8.8E-4,"collectionName":"ENS: Ethereum Name Service","safelistRequestStatus":"verified","imageUrl":"https://i.seadn.io/gae/0cOqWoYA7xL9CkUjGlxsjreSYBdrUBE0c6EO1COG4XE8UeP-Z30ckqUNiL872zHQHQU5MUNMNhfDpyXIP17hRSC5HQ?w=500&auto=format","description":"Ethereum Name Service (ENS) domains are secure domain names for the decentralized world. ENS domains provide a way for users to map human readable names to blockchain and non-blockchain resources, like Ethereum addresses, IPFS hashes, or website URLs. ENS domains can be bought and sold on secondary markets.","externalUrl":"https://ens.domains","twitterUsername":"ensdomains","lastIngestedAt":"2022-11-01T15:30:04.000Z"}}}
            //https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/105163109881267868532041562026418602960922071549544155763322457683184960142748
            //{"is_normalized":true,"name":"rehbein.eth","description":"rehbein.eth, an ENS name.","attributes":[{"trait_type":"Created Date","display_type":"date","value":1500099683000},{"trait_type":"Length","display_type":"number","value":7},{"trait_type":"Segment Length","display_type":"number","value":7},{"trait_type":"Character Set","display_type":"string","value":"letter"},{"trait_type":"Registration Date","display_type":"date","value":1661223139000},{"trait_type":"Expiration Date","display_type":"date","value":1692780091000}],"name_length":7,"segment_length":7,"url":"https://app.ens.domains/name/rehbein.eth","version":0,"background_image":"https://metadata.ens.domains/mainnet/avatar/rehbein.eth","image":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image","image_url":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image"}
            Map contract = (Map) map.get("contract");
            Map metadata = (Map) map.get("metadata");
            extractedMetadata(ensMeta, metadata);
        }
        ethEnsInfoModel.setDomain(ensMeta.getDomain());
        ethEnsInfoModel.setImage(ensMeta.getImage());
        ethEnsInfoModel.setBackgroundImage(ensMeta.getBackgroundImage());
        ethEnsInfoModel.setUrl(ensMeta.getUrl());
        ethEnsInfoModel.setLength(ensMeta.getLength());
        ethEnsInfoModel.setCreateDate(ensMeta.getCreateDate());
        ethEnsInfoModel.setExpirationDate(ensMeta.getExpirationDate());
        ethEnsInfoModel.setRegistrationDate(ensMeta.getRegistrationDate());
        ethEnsInfoModel.setCharacterSet(ensMeta.getCharacterSet());
        if(StringUtils.isASCII(ensMeta.getDomain())){//是否包含unicode字符
            ethEnsInfoModel.setHasUnicode(1);
            ethEnsInfoModel.setNormailized(0);
            ethEnsInfoModel.setHasEmoji(0);
            if(StringUtils.containsEmoji(ensMeta.getDomain())){//是否包含emoji
                ethEnsInfoModel.setHasEmoji(1);
            }
        }else{
            ethEnsInfoModel.setHasUnicode(0);
            ethEnsInfoModel.setNormailized(1);
            ethEnsInfoModel.setHasEmoji(0);
        }
        if(StringUtils.onlyLetter(ensMeta.getDomain())){//是否是纯字母
            ethEnsInfoModel.setLettersOnly(1);
        }else{
            ethEnsInfoModel.setLettersOnly(0);
        }
        if(StringUtils.containsNumber(ensMeta.getDomain())){//是否包含数字
            ethEnsInfoModel.setHasNumbers(1);
        }else{
            ethEnsInfoModel.setHasNumbers(0);
        }
        if(StringUtils.containInvisibles(ensMeta.getDomain())){//是否包含不可见字符
            ethEnsInfoModel.setHasInvisibles(1);
        }else{
            ethEnsInfoModel.setHasInvisibles(0);
        }
    }

    private static void extractedMetadata(EnsMeta ensMeta, Map metadata) {
        ensMeta.setDomain((String) metadata.get("name"));
        ensMeta.setImage((String) metadata.get("image"));
        ensMeta.setBackgroundImage((String) metadata.get("background_image"));
        ensMeta.setUrl((String) metadata.get("url"));
        ensMeta.setLength((Integer) metadata.get("segment_length"));
        List<Map> attributes = (List<Map>) metadata.get("attributes");
        for(Map m:attributes){
            if("Created Date".equals(m.get("trait_type"))){
                Object value = m.get("value");
                if(value != null){//创建时间可能为空
                    ensMeta.setCreateDate(new Date(((Number) m.get("value")).longValue()));
                }
                continue;
            }
            if("Character Set".equals(m.get("trait_type"))){
                ensMeta.setCharacterSet((String) m.get("value"));
                continue;
            }
            if("Registration Date".equals(m.get("trait_type"))){
                ensMeta.setRegistrationDate(new Date(((Number) m.get("value")).longValue()));
                continue;
            }
            if("Expiration Date".equals(m.get("trait_type"))){
                ensMeta.setExpirationDate(new Date(((Number) m.get("value")).longValue()));
                continue;
            }
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        String meta = "{\"tokenId\":\"0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072\",\"createDate\":1651427434000,\"image\":\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072/image\",\"backgroundImage\":\"https://metadata.ens.domains/mainnet/avatar/77101.eth\",\"url\":\"https://app.ens.domains/name/77101.eth\",\"characterSet\":\"digit\",\"constractAddress\":\"0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85\",\"domain\":\"77101.eth\",\"owner\":null,\"lastTxnHash\":null,\"lastTxnTime\":null,\"lastTxnFee\":null,\"normailized\":0,\"expirationDate\":1746098290000,\"length\":5,\"lettersOnly\":0,\"hasNumbers\":1,\"hasUnicode\":1,\"hasEmoji\":0,\"hasInvisibles\":1,\"registrationDate\":1651427434000,\"meta\":\"{\\\"contract\\\":{\\\"address\\\":\\\"0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85\\\"},\\\"id\\\":{\\\"tokenId\\\":\\\"0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072\\\",\\\"tokenMetadata\\\":{\\\"tokenType\\\":\\\"ERC721\\\"}},\\\"title\\\":\\\"77101.eth\\\",\\\"description\\\":\\\"77101.eth, an ENS name.\\\",\\\"tokenUri\\\":{\\\"raw\\\":\\\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072\\\",\\\"gateway\\\":\\\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072\\\"},\\\"media\\\":[{\\\"raw\\\":\\\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072/image\\\",\\\"gateway\\\":\\\"https://res.cloudinary.com/alchemyapi/image/upload/mainnet/6f29033ca877294b5b15eb60995274a5.svg\\\",\\\"thumbnail\\\":\\\"https://res.cloudinary.com/alchemyapi/image/upload/w_256,h_256/mainnet/6f29033ca877294b5b15eb60995274a5.svg\\\",\\\"format\\\":\\\"svg\\\",\\\"bytes\\\":99874}],\\\"metadata\\\":{\\\"background_image\\\":\\\"https://metadata.ens.domains/mainnet/avatar/77101.eth\\\",\\\"image\\\":\\\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072/image\\\",\\\"is_normalized\\\":true,\\\"segment_length\\\":5,\\\"image_url\\\":\\\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072/image\\\",\\\"name\\\":\\\"77101.eth\\\",\\\"description\\\":\\\"77101.eth, an ENS name.\\\",\\\"attributes\\\":[{\\\"display_type\\\":\\\"date\\\",\\\"value\\\":1651427434000,\\\"trait_type\\\":\\\"Created Date\\\"},{\\\"display_type\\\":\\\"number\\\",\\\"value\\\":5,\\\"trait_type\\\":\\\"Length\\\"},{\\\"display_type\\\":\\\"number\\\",\\\"value\\\":5,\\\"trait_type\\\":\\\"Segment Length\\\"},{\\\"display_type\\\":\\\"string\\\",\\\"value\\\":\\\"digit\\\",\\\"trait_type\\\":\\\"Character Set\\\"},{\\\"display_type\\\":\\\"date\\\",\\\"value\\\":1651427434000,\\\"trait_type\\\":\\\"Registration Date\\\"},{\\\"display_type\\\":\\\"date\\\",\\\"value\\\":1746098290000,\\\"trait_type\\\":\\\"Expiration Date\\\"}],\\\"name_length\\\":5,\\\"version\\\":0,\\\"url\\\":\\\"https://app.ens.domains/name/77101.eth\\\"},\\\"timeLastUpdated\\\":\\\"2022-12-07T14:09:55.614Z\\\",\\\"contractMetadata\\\":{\\\"tokenType\\\":\\\"ERC721\\\",\\\"contractDeployer\\\":\\\"0x4fe4e666be5752f1fdd210f4ab5de2cc26e3e0e8\\\",\\\"deployedBlockNumber\\\":9380410,\\\"openSea\\\":{\\\"floorPrice\\\":0.0011,\\\"collectionName\\\":\\\"ENS: Ethereum Name Service\\\",\\\"safelistRequestStatus\\\":\\\"verified\\\",\\\"imageUrl\\\":\\\"https://i.seadn.io/gae/0cOqWoYA7xL9CkUjGlxsjreSYBdrUBE0c6EO1COG4XE8UeP-Z30ckqUNiL872zHQHQU5MUNMNhfDpyXIP17hRSC5HQ?w=500&auto=format\\\",\\\"description\\\":\\\"Ethereum Name Service (ENS) domains are secure domain names for the decentralized world. ENS domains provide a way for users to map human readable names to blockchain and non-blockchain resources, like Ethereum addresses, IPFS hashes, or website URLs. ENS domains can be bought and sold on secondary markets.\\\",\\\"externalUrl\\\":\\\"https://ens.domains\\\",\\\"twitterUsername\\\":\\\"ensdomains\\\",\\\"lastIngestedAt\\\":\\\"2022-12-13T21:09:16.000Z\\\"}}}\",\"openSeaPrice\":null,\"openSeaPriceUpdatedTime\":null,\"openSeaPriceToken\":null,\"openSeaAuction\":null,\"openSeaAuctionType\":null,\"openSeaListingDate\":null}";
        EthEnsInfoModel ethEnsInfoModel = new EthEnsInfoModel();
        ethEnsInfoModel.setMeta(meta);
        Map map = JsonUtil.string2Obj(meta);
        dealEnsMeta(ethEnsInfoModel, map);
        System.out.println(ethEnsInfoModel);
    }
}
