package com.eth.ens.service.impl;

import com.eth.ens.dao.EthEnsInfoDao;
import com.eth.ens.model.EthEnsDTO;
import com.eth.ens.model.EthEnsInfoModel;
import com.eth.ens.service.IEthEnsInfoService;
import com.eth.framework.base.utils.JsonUtil;
import com.eth.framework.base.utils.StringUtils;
import com.eth.transaction.model.EthTxnModel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
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
        Long timestamp = txn.getTimestamp() * 1000L;
        if(ethEnsInfoModel.getLastTxnTime() == null || ethEnsInfoModel.getLastTxnTime().getTime() <= timestamp){
            ethEnsInfoModel.setLastTxnTime(new Date(timestamp));
            ethEnsInfoModel.setLastTxnHash(txn.getTxnHash());
            ethEnsInfoModel.setLastTxnFee(txn.getEthValue().longValue());
            ethEnsInfoModel.setOwner(ensDTO.getTo());
        }
        ethEnsInfoDao.save(ethEnsInfoModel);
    }

    private static void dealEnsMeta(EthEnsInfoModel ethEnsInfoModel, String meta) throws IOException {
        Map<String, Object> map = JsonUtil.string2Obj(meta);
        Map contract = (Map) map.get("contract");
        String address = (String) contract.get("address");
        Map metadata = (Map) map.get("metadata");
        String domain = (String) metadata.get("name");
        String image = (String) metadata.get("image");
        String backgroundImage = (String) metadata.get("background_image");
        String url = (String) metadata.get("url");
        Integer length = (Integer) metadata.get("segment_length");
        List<Map> attributes = (List<Map>) metadata.get("attributes");
        Date createDate = null;
        Date expirationDate = null;
        Date registrationDate = null;
        String characterSet = "";
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
