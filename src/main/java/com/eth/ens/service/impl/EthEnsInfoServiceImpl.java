package com.eth.ens.service.impl;

import com.eth.ens.dao.EthEnsInfoDao;
import com.eth.ens.model.EnsDomainsDTO;
import com.eth.ens.model.EnsDomainsQO;
import com.eth.ens.model.EthEnsDTO;
import com.eth.ens.model.EthEnsInfoModel;
import com.eth.ens.service.IEthEnsInfoService;
import com.eth.framework.base.common.model.PageData;
import com.eth.framework.base.common.model.PageParam;
import com.eth.framework.base.common.utils.JsonUtil;
import com.eth.framework.base.common.utils.PageUtils;
import com.eth.framework.base.common.utils.StringUtils;
import com.eth.transaction.model.EthTxnModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class EthEnsInfoServiceImpl implements IEthEnsInfoService {
    @Resource
    EthEnsInfoDao ethEnsInfoDao;
    /**
     * 新增或更新ens
     * @param ensDTO
     */
    @Override
    public void insertOrUpdateEns(EthEnsDTO ensDTO) throws IOException {
        Optional<EthEnsInfoModel> one = ethEnsInfoDao.findById(ensDTO.getTokenId());
        EthEnsInfoModel ethEnsInfoModel = null;
        String meta = ensDTO.getMeta();
        if(!one.isPresent()){
            ethEnsInfoModel = new EthEnsInfoModel();
            ethEnsInfoModel.setTokenId(ensDTO.getTokenId());
            ethEnsInfoModel.setMeta(meta);
        }else{
            ethEnsInfoModel = one.get();
        }
        dealEnsMeta(ethEnsInfoModel, meta);
        EthTxnModel txn = ensDTO.getTxn();
        Long timestamp = txn.getTimestamp();
        if(ethEnsInfoModel.getLastTxnTime() == null || ethEnsInfoModel.getLastTxnTime().getTime() <= timestamp){
            ethEnsInfoModel.setLastTxnTime(new Date(timestamp));
            ethEnsInfoModel.setLastTxnHash(txn.getTxnHash());
            ethEnsInfoModel.setLastTxnFee(txn.getEthValue().longValue());
            ethEnsInfoModel.setOwner(ensDTO.getTo());
        }
        log.info(JsonUtil.object2String(ethEnsInfoModel));
        ethEnsInfoDao.save(ethEnsInfoModel);
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

    private static void dealEnsMeta(EthEnsInfoModel ethEnsInfoModel, String meta) throws IOException {
        Map<String, Object> map = JsonUtil.string2Obj(meta);
        String title = (String) map.get("title");
        String address = "";
        String domain = "";
        String image = "";
        String backgroundImage = "";
        String url = "";
        Integer length = null;
        Date createDate = null;
        Date expirationDate = null;
        Date registrationDate = null;
        String characterSet = "";
        if(StringUtils.isEmpty(title)){
            //{"contract":{"address":"0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85"},"id":{"tokenId":"0x4cbedf505977ad2333f03571681875b18ea2e1837c0791da20ee246e3ea7f34c","tokenMetadata":{"tokenType":"ERC721"}},"title":"","description":"","tokenUri":{"raw":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x4cbedf505977ad2333f03571681875b18ea2e1837c0791da20ee246e3ea7f34c","gateway":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x4cbedf505977ad2333f03571681875b18ea2e1837c0791da20ee246e3ea7f34c"},"media":[{"raw":"","gateway":""}],"metadata":{"message":{"name":"unknown.name","description":"Unknown ENS name","attributes":[{"display_type":"date","value":1580346653000000,"trait_type":"Created Date"},{"display_type":"number","value":7,"trait_type":"Length"},{"display_type":"string","value":"letter","trait_type":"Character Set"}],"name_length":7,"version":0,"is_normalized":true}},"timeLastUpdated":"2022-05-14T00:57:20.478Z","contractMetadata":{"tokenType":"ERC721","openSea":{"floorPrice":8.8E-4,"collectionName":"ENS: Ethereum Name Service","safelistRequestStatus":"verified","imageUrl":"https://i.seadn.io/gae/0cOqWoYA7xL9CkUjGlxsjreSYBdrUBE0c6EO1COG4XE8UeP-Z30ckqUNiL872zHQHQU5MUNMNhfDpyXIP17hRSC5HQ?w=500&auto=format","description":"Ethereum Name Service (ENS) domains are secure domain names for the decentralized world. ENS domains provide a way for users to map human readable names to blockchain and non-blockchain resources, like Ethereum addresses, IPFS hashes, or website URLs. ENS domains can be bought and sold on secondary markets.","externalUrl":"https://ens.domains","twitterUsername":"ensdomains","lastIngestedAt":"2022-11-01T15:30:04.000Z"}}}
            //如果title为空，说明ens已经过期了，只能通过tokenUri.raw查询
            //https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0x4cbedf505977ad2333f03571681875b18ea2e1837c0791da20ee246e3ea7f34c
            //{"message":"'caizhuoyan.eth' is already been expired at Mon, 04 May 2020 00:00:00 GMT."}
            Map tokenUri = (Map) map.get("tokenUri");
            String raw = (String) tokenUri.get("raw");
        }else{
            //{"contract":{"address":"0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85"},"id":{"tokenId":"0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c","tokenMetadata":{"tokenType":"ERC721"}},"title":"rehbein.eth","description":"rehbein.eth, an ENS name.","tokenUri":{"raw":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/105163109881267868532041562026418602960922071549544155763322457683184960142748","gateway":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/105163109881267868532041562026418602960922071549544155763322457683184960142748"},"media":[{"raw":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image","gateway":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image"}],"metadata":{"background_image":"https://metadata.ens.domains/mainnet/avatar/rehbein.eth","image":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image","is_normalized":true,"segment_length":7,"image_url":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image","name":"rehbein.eth","description":"rehbein.eth, an ENS name.","attributes":[{"display_type":"date","value":1500099683000,"trait_type":"Created Date"},{"display_type":"number","value":7,"trait_type":"Length"},{"display_type":"number","value":7,"trait_type":"Segment Length"},{"display_type":"string","value":"letter","trait_type":"Character Set"},{"display_type":"date","value":1661223139000,"trait_type":"Registration Date"},{"display_type":"date","value":1692780091000,"trait_type":"Expiration Date"}],"name_length":7,"version":0,"url":"https://app.ens.domains/name/rehbein.eth"},"timeLastUpdated":"2022-11-07T06:22:52.532Z","contractMetadata":{"tokenType":"ERC721","openSea":{"floorPrice":8.8E-4,"collectionName":"ENS: Ethereum Name Service","safelistRequestStatus":"verified","imageUrl":"https://i.seadn.io/gae/0cOqWoYA7xL9CkUjGlxsjreSYBdrUBE0c6EO1COG4XE8UeP-Z30ckqUNiL872zHQHQU5MUNMNhfDpyXIP17hRSC5HQ?w=500&auto=format","description":"Ethereum Name Service (ENS) domains are secure domain names for the decentralized world. ENS domains provide a way for users to map human readable names to blockchain and non-blockchain resources, like Ethereum addresses, IPFS hashes, or website URLs. ENS domains can be bought and sold on secondary markets.","externalUrl":"https://ens.domains","twitterUsername":"ensdomains","lastIngestedAt":"2022-11-01T15:30:04.000Z"}}}
            //https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/105163109881267868532041562026418602960922071549544155763322457683184960142748
            //{"is_normalized":true,"name":"rehbein.eth","description":"rehbein.eth, an ENS name.","attributes":[{"trait_type":"Created Date","display_type":"date","value":1500099683000},{"trait_type":"Length","display_type":"number","value":7},{"trait_type":"Segment Length","display_type":"number","value":7},{"trait_type":"Character Set","display_type":"string","value":"letter"},{"trait_type":"Registration Date","display_type":"date","value":1661223139000},{"trait_type":"Expiration Date","display_type":"date","value":1692780091000}],"name_length":7,"segment_length":7,"url":"https://app.ens.domains/name/rehbein.eth","version":0,"background_image":"https://metadata.ens.domains/mainnet/avatar/rehbein.eth","image":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image","image_url":"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xe88035fbf85c5a3294f84d9cfbe92cdcd2ade4db4d18a1980236733458dffd9c/image"}
            Map contract = (Map) map.get("contract");
            address = (String) contract.get("address");
            Map metadata = (Map) map.get("metadata");
            domain = (String) metadata.get("name");
            image = (String) metadata.get("image");
            backgroundImage = (String) metadata.get("background_image");
            url = (String) metadata.get("url");
            length = (Integer) metadata.get("segment_length");
            List<Map> attributes = (List<Map>) metadata.get("attributes");
            for(Map m:attributes){
                if("Created Date".equals(m.get("trait_type"))){
                    createDate = new Date((Long) m.get("value"));
                    continue;
                }
                if("Character Set".equals(m.get("trait_type"))){
                    characterSet = (String) m.get("value");
                    continue;
                }
                if("Registration Date".equals(m.get("trait_type"))){
                    registrationDate = new Date((Long) m.get("value"));
                    continue;
                }
                if("Expiration Date".equals(m.get("trait_type"))){
                    expirationDate = new Date((Long) m.get("value"));
                    continue;
                }
            }
        }
        ethEnsInfoModel.setConstractAddress(address);
        ethEnsInfoModel.setDomain(domain);
        ethEnsInfoModel.setImage(image);
        ethEnsInfoModel.setBackgroundImage(backgroundImage);
        ethEnsInfoModel.setUrl(url);
        ethEnsInfoModel.setLength(length);
        ethEnsInfoModel.setCreateDate(createDate);
        ethEnsInfoModel.setExpirationDate(expirationDate);
        ethEnsInfoModel.setRegistrationDate(registrationDate);
        ethEnsInfoModel.setCharacterSet(characterSet);
        if(StringUtils.isASCII(domain)){//是否包含unicode字符
            ethEnsInfoModel.setHasUnicode(1);
            ethEnsInfoModel.setNormailized(0);
            ethEnsInfoModel.setHasEmoji(0);
            if(StringUtils.containsEmoji(domain)){//是否包含emoji
                ethEnsInfoModel.setHasEmoji(1);
            }
        }else{
            ethEnsInfoModel.setHasUnicode(0);
            ethEnsInfoModel.setNormailized(1);
            ethEnsInfoModel.setHasEmoji(0);
        }
        if(StringUtils.onlyLetter(domain)){//是否是纯字母
            ethEnsInfoModel.setLettersOnly(1);
        }else{
            ethEnsInfoModel.setLettersOnly(0);
        }
        if(StringUtils.containsNumber(domain)){//是否包含数字
            ethEnsInfoModel.setHasNumbers(1);
        }else{
            ethEnsInfoModel.setHasNumbers(0);
        }
        if(StringUtils.containInvisibles(domain)){//是否包含不可见字符
            ethEnsInfoModel.setHasInvisibles(1);
        }else{
            ethEnsInfoModel.setHasInvisibles(0);
        }
    }

    public static void main(String[] args) throws IOException {
        String meta = "{\"contract\":{\"address\":\"0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85\"},\"id\":{\"tokenId\":\"0xa1054194986dd041b30c44125ee3cbf09b3c7c5583671af365c1f3215ace009d\",\"tokenMetadata\":{\"tokenType\":\"ERC721\"}},\"title\":\"0357.eth\",\"description\":\"0357.eth, an ENS name.\",\"tokenUri\":{\"raw\":\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xa1054194986dd041b30c44125ee3cbf09b3c7c5583671af365c1f3215ace009d\",\"gateway\":\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xa1054194986dd041b30c44125ee3cbf09b3c7c5583671af365c1f3215ace009d\"},\"media\":[{\"raw\":\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xa1054194986dd041b30c44125ee3cbf09b3c7c5583671af365c1f3215ace009d/image\",\"gateway\":\"https://res.cloudinary.com/alchemyapi/image/upload/mainnet/de91f29381af44bda4ada2ea64e8bf22.svg\",\"thumbnail\":\"https://res.cloudinary.com/alchemyapi/image/upload/w_256,h_256/mainnet/de91f29381af44bda4ada2ea64e8bf22.svg\",\"format\":\"svg\",\"bytes\":1040926}],\"metadata\":{\"background_image\":\"https://metadata.ens.domains/mainnet/avatar/0357.eth\",\"image\":\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xa1054194986dd041b30c44125ee3cbf09b3c7c5583671af365c1f3215ace009d/image\",\"is_normalized\":true,\"segment_length\":4,\"image_url\":\"https://metadata.ens.domains/mainnet/0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85/0xa1054194986dd041b30c44125ee3cbf09b3c7c5583671af365c1f3215ace009d/image\",\"name\":\"0357.eth\",\"description\":\"0357.eth, an ENS name.\",\"attributes\":[{\"display_type\":\"date\",\"value\":1651010778000,\"trait_type\":\"Created Date\"},{\"display_type\":\"number\",\"value\":4,\"trait_type\":\"Length\"},{\"display_type\":\"number\",\"value\":4,\"trait_type\":\"Segment Length\"},{\"display_type\":\"string\",\"value\":\"digit\",\"trait_type\":\"Character Set\"},{\"display_type\":\"date\",\"value\":1651010778000,\"trait_type\":\"Registration Date\"},{\"display_type\":\"date\",\"value\":1682567730000,\"trait_type\":\"Expiration Date\"}],\"name_length\":4,\"version\":0,\"url\":\"https://app.ens.domains/name/0357.eth\"},\"timeLastUpdated\":\"2022-10-03T15:32:17.011Z\",\"contractMetadata\":{\"name\":\"\",\"symbol\":\"\",\"totalSupply\":\"\",\"tokenType\":\"UNKNOWN\"}}";
        EthEnsInfoModel ethEnsInfoModel = new EthEnsInfoModel();
        ethEnsInfoModel.setMeta(meta);
        dealEnsMeta(ethEnsInfoModel, meta);
        System.out.println(ethEnsInfoModel);
    }
}
