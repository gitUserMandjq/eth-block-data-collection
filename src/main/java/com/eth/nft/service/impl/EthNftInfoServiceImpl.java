package com.eth.nft.service.impl;

import com.eth.ens.model.EthNftDTO;
import com.eth.framework.base.common.utils.AlchemyUtils;
import com.eth.framework.base.common.utils.JsonUtil;
import com.eth.framework.base.common.utils.StringUtils;
import com.eth.nft.dao.EthNftInfoDao;
import com.eth.nft.model.EthNftInfoModel;
import com.eth.nft.service.IEthNftInfoService;
import com.eth.transaction.model.EthTxnModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EthNftInfoServiceImpl implements IEthNftInfoService {
    @Resource
    EthNftInfoDao ethNftInfoDao;
    /**
     * 新增或更新ens
     * @param ensDTO
     */
    @Override
    public void insertOrUpdateEns(EthNftDTO ensDTO) throws IOException, ParseException {
        Optional<EthNftInfoModel> one = ethNftInfoDao.findById(ensDTO.getTokenId());
        EthNftInfoModel ethNftInfoModel = null;
        Map<String, Object> map = ensDTO.getMeta();
        if(!one.isPresent()){
            ethNftInfoModel = new EthNftInfoModel();
            ethNftInfoModel.setTokenId(ensDTO.getTokenId());
            ethNftInfoModel.setMeta(JsonUtil.object2String(map));
            ethNftInfoModel.setConstractAddress(ensDTO.getAddress());
        }else{
            ethNftInfoModel = one.get();
        }
        dealEnsMeta(ethNftInfoModel, map);
        EthTxnModel txn = ensDTO.getTxn();
        if(txn != null){
            Date timestamp = txn.getTimestamp();
            if(ethNftInfoModel.getLastTxnTime() == null || ethNftInfoModel.getLastTxnTime().getTime() <= timestamp.getTime()){
                ethNftInfoModel.setLastTxnTime(timestamp);
                ethNftInfoModel.setLastTxnHash(txn.getTxnHash());
                ethNftInfoModel.setLastTxnFee(txn.getEthValue().longValue());
                ethNftInfoModel.setOwner(ensDTO.getTo());
            }
        }
        log.info(JsonUtil.object2String(ethNftInfoModel));
        ethNftInfoDao.save(ethNftInfoModel);
    }
    /**
     * 新增或更新ens
     * @param ensDTOList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertOrUpdateEns(List<EthNftDTO> ensDTOList) throws Exception {
        if(ensDTOList.isEmpty()){
            return;
        }
        List<String> tokenIds = ensDTOList.stream().map(EthNftDTO::getTokenId).collect(Collectors.toList());
        List<EthNftInfoModel> ensList = ethNftInfoDao.listNftByIds(tokenIds);
        Map<String, EthNftInfoModel> ensMap = ensList.stream().collect(Collectors.toMap(EthNftInfoModel::getTokenId, v -> v));
        List<EthNftInfoModel> addOrUpdateList = new ArrayList<>();
        for(EthNftDTO ensDTO:ensDTOList){
            Map<String, Object> map = ensDTO.getMeta();
            EthNftInfoModel ethNftInfoModel = ensMap.get(ensDTO.getTokenId());
            if(ethNftInfoModel == null){
                ethNftInfoModel = new EthNftInfoModel();
                ethNftInfoModel.setTokenId(ensDTO.getTokenId());
                ethNftInfoModel.setMeta(JsonUtil.object2String(map));
                ethNftInfoModel.setConstractAddress(ensDTO.getAddress());
            }
            dealEnsMeta(ethNftInfoModel, map);
            EthTxnModel txn = ensDTO.getTxn();
            if(txn != null){
                Date timestamp = txn.getTimestamp();
                if(ethNftInfoModel.getLastTxnTime() == null || ethNftInfoModel.getLastTxnTime().getTime() <= timestamp.getTime()){
                    ethNftInfoModel.setLastTxnTime(timestamp);
                    ethNftInfoModel.setLastTxnHash(txn.getTxnHash());
                    ethNftInfoModel.setLastTxnFee(txn.getEthValue().longValue());
                    ethNftInfoModel.setOwner(ensDTO.getTo());
                }
            }
            addOrUpdateList.add(ethNftInfoModel);
        }
        if(!addOrUpdateList.isEmpty()){
            ethNftInfoDao.batchReplace(addOrUpdateList, 500);
        }
    }
    @Data
    private static class NftMeta {
        private String name = "";
        private String image = "";
        private String backgroundImage = "";
        private String url = "";
        private Date createDate = null;
    }
    private static void dealEnsMeta(EthNftInfoModel ethNftInfoModel, Map<String, Object> map) throws IOException, ParseException {
        log.info("开始处理meta："+ethNftInfoModel.getMeta());
        String title = (String) map.get("title");
        NftMeta nftMeta = new NftMeta();
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
                extractedMetadata(nftMeta, metadata);
            }
        }else{
            //{"contract":{"address":"0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85"},"id":{"tokenId":"0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c","tokenMetadata":{"tokenType":"ERC721"}},"title":"rehbein.eth","description":"rehbein.eth, an ENS name.","tokenUri":{"raw":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/105163109881267868532041562026418602960922071549544155763322457683184960142748","gateway":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/105163109881267868532041562026418602960922071549544155763322457683184960142748"},"media":[{"raw":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image","gateway":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image"}],"metadata":{"background_image":"https://metadata.ens.domains/mainnet/avatar/rehbein.eth","image":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image","is_normalized":true,"segment_length":7,"image_url":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image","name":"rehbein.eth","description":"rehbein.eth, an ENS name.","attributes":[{"display_type":"date","value":1500099683000,"trait_type":"Created Date"},{"display_type":"number","value":7,"trait_type":"Length"},{"display_type":"number","value":7,"trait_type":"Segment Length"},{"display_type":"string","value":"letter","trait_type":"Character Set"},{"display_type":"date","value":1661223139000,"trait_type":"Registration Date"},{"display_type":"date","value":1692780091000,"trait_type":"Expiration Date"}],"name_length":7,"version":0,"url":"https://app.ens.domains/name/rehbein.eth"},"timeLastUpdated":"2022-11-07T06:22:52.532Z","contractMetadata":{"tokenType":"ERC721","openSea":{"floorPrice":8.8E-4,"collectionName":"ENS: Ethereum Name Service","safelistRequestStatus":"verified","imageUrl":"https://i.seadn.io/gae/0cOqWoYA7xL9CkUjGlxsjreSYBdrUBE0c6EO1COG4XE8UeP-Z30ckqUNiL872zHQHQU5MUNMNhfDpyXIP17hRSC5HQ?w=500&auto=format","description":"Ethereum Name Service (ENS) domains are secure domain names for the decentralized world. ENS domains provide a way for users to map human readable names to blockchain and non-blockchain resources, like Ethereum addresses, IPFS hashes, or website URLs. ENS domains can be bought and sold on secondary markets.","externalUrl":"https://ens.domains","twitterUsername":"ensdomains","lastIngestedAt":"2022-11-01T15:30:04.000Z"}}}
            //https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/105163109881267868532041562026418602960922071549544155763322457683184960142748
            //{"is_normalized":true,"name":"rehbein.eth","description":"rehbein.eth, an ENS name.","attributes":[{"trait_type":"Created Date","display_type":"date","value":1500099683000},{"trait_type":"Length","display_type":"number","value":7},{"trait_type":"Segment Length","display_type":"number","value":7},{"trait_type":"Character Set","display_type":"string","value":"letter"},{"trait_type":"Registration Date","display_type":"date","value":1661223139000},{"trait_type":"Expiration Date","display_type":"date","value":1692780091000}],"name_length":7,"segment_length":7,"url":"https://app.ens.domains/name/rehbein.eth","version":0,"background_image":"https://metadata.ens.domains/mainnet/avatar/rehbein.eth","image":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image","image_url":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image"}
            Map contract = (Map) map.get("contract");
            Map metadata = (Map) map.get("metadata");
            extractedMetadata(nftMeta, metadata);
        }
        if(nftMeta.getName().length() >= 200){
            ethNftInfoModel.setName(nftMeta.getName().substring(0, 199));
        }else{
            ethNftInfoModel.setName(nftMeta.getName());
        }
        ethNftInfoModel.setImage(nftMeta.getImage());
        ethNftInfoModel.setBackgroundImage(nftMeta.getBackgroundImage());
        ethNftInfoModel.setUrl(nftMeta.getUrl());
        ethNftInfoModel.setCreateDate(nftMeta.getCreateDate());
    }

    private static void extractedMetadata(NftMeta nftMeta, Map metadata) {
        nftMeta.setName((String) metadata.get("name"));
        nftMeta.setImage((String) metadata.get("image"));
        nftMeta.setBackgroundImage((String) metadata.get("background_image"));
        nftMeta.setUrl((String) metadata.get("url"));
        List<Map> attributes = (List<Map>) metadata.get("attributes");
        for(Map m:attributes){
            if("Created Date".equals(m.get("trait_type"))){
                Object value = m.get("value");
                if(value != null){//创建时间可能为空
                    nftMeta.setCreateDate(new Date(((Number) m.get("value")).longValue()));
                }
                continue;
            }
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        String meta = "{\"tokenId\":\"0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072\",\"createDate\":1651427434000,\"image\":\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072/image\",\"backgroundImage\":\"https://metadata.ens.domains/mainnet/avatar/77101.eth\",\"url\":\"https://app.ens.domains/name/77101.eth\",\"characterSet\":\"digit\",\"constractAddress\":\"0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85\",\"domain\":\"77101.eth\",\"owner\":null,\"lastTxnHash\":null,\"lastTxnTime\":null,\"lastTxnFee\":null,\"normailized\":0,\"expirationDate\":1746098290000,\"length\":5,\"lettersOnly\":0,\"hasNumbers\":1,\"hasUnicode\":1,\"hasEmoji\":0,\"hasInvisibles\":1,\"registrationDate\":1651427434000,\"meta\":\"{\\\"contract\\\":{\\\"address\\\":\\\"0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85\\\"},\\\"id\\\":{\\\"tokenId\\\":\\\"0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072\\\",\\\"tokenMetadata\\\":{\\\"tokenType\\\":\\\"ERC721\\\"}},\\\"title\\\":\\\"77101.eth\\\",\\\"description\\\":\\\"77101.eth, an ENS name.\\\",\\\"tokenUri\\\":{\\\"raw\\\":\\\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072\\\",\\\"gateway\\\":\\\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072\\\"},\\\"media\\\":[{\\\"raw\\\":\\\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072/image\\\",\\\"gateway\\\":\\\"https://res.cloudinary.com/alchemyapi/image/upload/mainnet/6f29033ca877294b5b15eb60995274a5.svg\\\",\\\"thumbnail\\\":\\\"https://res.cloudinary.com/alchemyapi/image/upload/w_256,h_256/mainnet/6f29033ca877294b5b15eb60995274a5.svg\\\",\\\"format\\\":\\\"svg\\\",\\\"bytes\\\":99874}],\\\"metadata\\\":{\\\"background_image\\\":\\\"https://metadata.ens.domains/mainnet/avatar/77101.eth\\\",\\\"image\\\":\\\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072/image\\\",\\\"is_normalized\\\":true,\\\"segment_length\\\":5,\\\"image_url\\\":\\\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x53221614ddf513d6133eb10716402a549bba0cf7eb01fc59b893ac34676dd072/image\\\",\\\"name\\\":\\\"77101.eth\\\",\\\"description\\\":\\\"77101.eth, an ENS name.\\\",\\\"attributes\\\":[{\\\"display_type\\\":\\\"date\\\",\\\"value\\\":1651427434000,\\\"trait_type\\\":\\\"Created Date\\\"},{\\\"display_type\\\":\\\"number\\\",\\\"value\\\":5,\\\"trait_type\\\":\\\"Length\\\"},{\\\"display_type\\\":\\\"number\\\",\\\"value\\\":5,\\\"trait_type\\\":\\\"Segment Length\\\"},{\\\"display_type\\\":\\\"string\\\",\\\"value\\\":\\\"digit\\\",\\\"trait_type\\\":\\\"Character Set\\\"},{\\\"display_type\\\":\\\"date\\\",\\\"value\\\":1651427434000,\\\"trait_type\\\":\\\"Registration Date\\\"},{\\\"display_type\\\":\\\"date\\\",\\\"value\\\":1746098290000,\\\"trait_type\\\":\\\"Expiration Date\\\"}],\\\"name_length\\\":5,\\\"version\\\":0,\\\"url\\\":\\\"https://app.ens.domains/name/77101.eth\\\"},\\\"timeLastUpdated\\\":\\\"2022-12-07T14:09:55.614Z\\\",\\\"contractMetadata\\\":{\\\"tokenType\\\":\\\"ERC721\\\",\\\"contractDeployer\\\":\\\"0x4fe4e666be5752f1fdd210f4ab5de2cc26e3e0e8\\\",\\\"deployedBlockNumber\\\":9380410,\\\"openSea\\\":{\\\"floorPrice\\\":0.0011,\\\"collectionName\\\":\\\"ENS: Ethereum Name Service\\\",\\\"safelistRequestStatus\\\":\\\"verified\\\",\\\"imageUrl\\\":\\\"https://i.seadn.io/gae/0cOqWoYA7xL9CkUjGlxsjreSYBdrUBE0c6EO1COG4XE8UeP-Z30ckqUNiL872zHQHQU5MUNMNhfDpyXIP17hRSC5HQ?w=500&auto=format\\\",\\\"description\\\":\\\"Ethereum Name Service (ENS) domains are secure domain names for the decentralized world. ENS domains provide a way for users to map human readable names to blockchain and non-blockchain resources, like Ethereum addresses, IPFS hashes, or website URLs. ENS domains can be bought and sold on secondary markets.\\\",\\\"externalUrl\\\":\\\"https://ens.domains\\\",\\\"twitterUsername\\\":\\\"ensdomains\\\",\\\"lastIngestedAt\\\":\\\"2022-12-13T21:09:16.000Z\\\"}}}\",\"openSeaPrice\":null,\"openSeaPriceUpdatedTime\":null,\"openSeaPriceToken\":null,\"openSeaAuction\":null,\"openSeaAuctionType\":null,\"openSeaListingDate\":null}";
        EthNftInfoModel ethNftInfoModel = new EthNftInfoModel();
        ethNftInfoModel.setMeta(meta);
        Map map = JsonUtil.string2Obj(meta);
        dealEnsMeta(ethNftInfoModel, map);
        System.out.println(ethNftInfoModel);
    }
}
