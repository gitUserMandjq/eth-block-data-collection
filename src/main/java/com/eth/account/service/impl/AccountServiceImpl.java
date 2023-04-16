package com.eth.account.service.impl;

import com.eth.account.dao.EthAccountSmartContractDao;
import com.eth.account.dao.EthAccountSmartDao;
import com.eth.account.dao.EthContractsDao;
import com.eth.account.model.EthAccountSmartContractModel;
import com.eth.account.model.EthAccountSmartModel;
import com.eth.account.model.EthContractsModel;
import com.eth.account.service.IAccountService;
import com.eth.event.model.EthEventTransferSmartModel;
import com.eth.framework.base.common.constant.KeyConst;
import com.eth.framework.base.common.model.PageData;
import com.eth.framework.base.common.model.PageParam;
import com.eth.framework.base.common.utils.*;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.BatchRequest;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetCode;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements IAccountService {
    @Resource
    EthAccountSmartDao ethAccountSmartDao;
    @Resource
    EthContractsDao ethContractsDao;
    @Resource
    EthAccountSmartContractDao ethAccountSmartContractDao;
    /**
     * 获取账户代码，如果是非合约账户返回0x
     * @param address
     * @return
     */
    @Override
    public String getAccountCode(String address) throws Exception{
        Request<?, EthGetCode> request = Web3jUtil.getInstance().getWeb3j().ethGetCode(address, DefaultBlockParameterName.LATEST);
        String code = request.send().getCode();
        return code;
    }

    /**
     * 批量获取账户代码，如果是非合约账户返回0x
     * @param address
     * @return
     */
    @Override
    public List<String> getAccountCode(Iterable<String> address) throws Exception {
        if(address == null || !address.iterator().hasNext()){
            return new ArrayList<>();
        }
        BatchRequest batchRequest = Web3jUtil.getInstance().getWeb3j().newBatch();
        for (String addr : address) {
            Request<?, EthGetCode> request = Web3jUtil.getInstance().getWeb3j().ethGetCode(addr, DefaultBlockParameterName.LATEST);
            batchRequest.add(request);
        }
        List<? extends EthGetCode> responses = (List<? extends EthGetCode>) batchRequest.sendAsync().get().getResponses();
        return responses.stream().map(EthGetCode::getCode).collect(Collectors.toList());
    }
    /**
     * 批量导入聪明钱包
     * @param accountList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBatchAccountSmart(Iterable<EthAccountSmartModel> accountList) throws Exception {
        Iterator<EthAccountSmartModel> iterator = accountList.iterator();
        while(iterator.hasNext()){
            EthAccountSmartModel next = iterator.next();
            next.setCreatedAt(new Date());
            next.setUpdatedAt(new Date());
        }
        ethAccountSmartDao.batchIgnoreSave(accountList, 500);
    }

    /**
     * 删除聪明钱包
     * @param address
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccountSmart(String address) throws Exception{
        Optional<EthAccountSmartModel> option = ethAccountSmartDao.findById(address);
        if(option.isPresent()){
            EthAccountSmartModel smart = option.get();
            ethAccountSmartDao.delete(smart);
            List<EthAccountSmartContractModel> contractModelList = ethAccountSmartContractDao.findByAddress(address);
            for(EthAccountSmartContractModel sm:contractModelList){
                ethAccountSmartContractDao.delete(sm);
            }
        }
    }
    /**
     * 查询导入的聪明钱包
     * @param beginTime
     * @param pageInfo
     * @return
     */
    @Override
    public PageData<EthAccountSmartModel> listAccountSmart(String tokenName, Date beginTime, PageParam pageInfo) throws Exception {
        Specification<EthAccountSmartModel> specification = new Specification<EthAccountSmartModel>() {
            @Override
            public Predicate toPredicate(Root<EthAccountSmartModel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate= cb.conjunction();
                if(!StringUtils.isEmpty(tokenName)){
                    predicate.getExpressions().add(cb.like(root.get("contractName"), "%"+tokenName+"%"));
                }
                if(beginTime != null){
                    predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("createdAt"), beginTime));
                }
                return predicate;
            }
        };
        Page<EthAccountSmartModel> page = ethAccountSmartDao.findAll(specification, pageInfo);
        PageData<EthAccountSmartModel> pageData = PageUtils.convertPageData(page);
        return pageData;
    }
    /**
     * 批量导入聪明钱包和合约地址关联
     * @param accountContractList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBatchAccountSmartContract(Iterable<EthAccountSmartContractModel> accountContractList) throws Exception {
        Iterator<EthAccountSmartContractModel> iterator = accountContractList.iterator();
        while(iterator.hasNext()){
            EthAccountSmartContractModel next = iterator.next();
            next.setId(next.getAddress() + "_" + next.getTokenAddress());
            next.setCreatedAt(new Date());
            next.setUpdatedAt(new Date());
            //清除监听缓存
            String key = KeyConst.CONTRACTSMARTADDRESS + next.getTokenAddress();
            ObjectManageUtils.remove(key);
        }
        ethAccountSmartContractDao.batchIgnoreSave(accountContractList, 500);
    }
    /**
     * 根据合约地址获得监听的聪明钱包
     * @param tokenAddress
     * @return
     */
    @Override
    public Set<String> getSmartContractByTokenAddress(String tokenAddress) {
        String key = KeyConst.CONTRACTSMARTADDRESS + tokenAddress;
        Set<String> set = (Set<String>) ObjectManageUtils.getValue(key);
        if(set == null){
            set = new HashSet<>();
            List<EthAccountSmartContractModel> smartAddressList = ethAccountSmartContractDao.findByTokenAddress(tokenAddress);
            for(EthAccountSmartContractModel sa:smartAddressList){
                set.add(sa.getAddress());
            }
            ObjectManageUtils.setValue(key, set);
        }
        return set;
    }
    /**
     * 更新聪明钱包最后交易
     * @param transfer
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EthAccountSmartModel updateAddressTransfer(EthEventTransferSmartModel transfer) throws IOException {
        //监听地址
        String address = transfer.getListenAddress();
        Optional<EthAccountSmartModel> option = ethAccountSmartDao.findById(address);
        if(!option.isPresent()){
            return null;
        }
        EthAccountSmartModel smartAddress = option.get();
        //合约地址
        String tokenAddress = transfer.getTokenAddress();
        EthContractsModel contract = getContractByAddress(tokenAddress);
        if(contract != null){
            smartAddress.setContractAddress(contract.getAddress());
            smartAddress.setContractLogo(contract.getLogo());
            smartAddress.setContractName(contract.getName());
            smartAddress.setTimestamp(transfer.getTimestamp());
            smartAddress.setTransType(transfer.getTransType());
            ethAccountSmartDao.save(smartAddress);
        }
        return smartAddress;
    }
    /**
     * 更新聪明钱包最后交易（批量执行）
     * @param transfer
     * @return
     */
    @Override
    public EthAccountSmartModel updateAddressTransferWithOutSave(EthEventTransferSmartModel transfer) throws IOException {
        //监听地址
        String address = transfer.getListenAddress();
        Optional<EthAccountSmartModel> option = ethAccountSmartDao.findById(address);
        if(!option.isPresent()){
            return null;
        }
        EthAccountSmartModel smartAddress = option.get();
        //合约地址
        String tokenAddress = transfer.getTokenAddress();
        EthContractsModel contract = getContractByAddress(tokenAddress);
        if(contract != null){
            smartAddress.setContractAddress(contract.getAddress());
            smartAddress.setContractLogo(contract.getLogo());
            smartAddress.setContractName(contract.getName());
            smartAddress.setTimestamp(transfer.getTimestamp());
            smartAddress.setTransType(transfer.getTransType());
        }
        return smartAddress;
    }
    /**
     * 查询合约
     * @param address
     * @return
     */
    @Override
    public EthContractsModel getContractByAddress(String contractAddress) throws IOException {
        EthContractsModel contractsModel = contractMap.get(contractAddress);
        if(contractsModel == null){
            contractsModel = ethContractsDao.findTopByAddress(contractAddress);
            if(contractsModel == null){
                //nft合约，{"address":"0x67b36ba196804db198ac4c0a8359a1fafa5e5cff","contractMetadata":{"name":"Meta RPG","symbol":"META","totalSupply":"2500","tokenType":"ERC721"}}
                //非nft合约，{"address":"0xb8c77482e45f1f44de1745f52c74426c631bdd52","contractMetadata":{"name":"","symbol":"","totalSupply":"","tokenType":"UNKNOWN"}}
                //
                String contractMetadata = AlchemyUtils.getContractMetadata(contractAddress);
                contractsModel = initContractInfo(contractMetadata);
                ethContractsDao.save(contractsModel);
            }
            contractMap.put(contractAddress, contractsModel);
        }
        return contractsModel;
    }
    private static EthContractsModel initContractInfo(String contractAddress) throws IOException {
        String contractMetadata = AlchemyUtils.getContractMetadata(contractAddress);
        Map map = JsonUtil.string2Obj(contractMetadata);
        Map meta = (Map) map.get("contractMetadata");
        String tokenType = (String) meta.get("tokenType");
        EthContractsModel contractInfo = new EthContractsModel();
        contractInfo.setAddress(contractAddress);
        if("NO_SUPPORTED_NFT_STANDARD".equals(tokenType)){
            try {
                String tokenMetadata = AlchemyUtils.getTokenMetadata(Arrays.asList(contractAddress));
                //{"jsonrpc":"2.0","id":1,"result":{"decimals":18,"logo":"https://static.alchemyapi.io/images/assets/7278.png","name":"Aave","symbol":"AAVE"}}
                Map tokenMap = JsonUtil.string2Obj(tokenMetadata);
                Map result = (Map) tokenMap.get("result");
                String logo = (String) result.get("logo");
                String name = (String) result.get("name");
                String symbol = (String) result.get("symbol");
                Integer decimals = (Integer) result.get("decimals");
                Long deployedBlockNumber = NumberUtils.longValueOf(meta.get("deployedBlockNumber"));
                String contractDeployer = (String) meta.get("contractDeployer");
                contractInfo.setBlockNumber(deployedBlockNumber);
                contractInfo.setCreater(contractDeployer);
                contractInfo.setName(name);
                contractInfo.setSymbol(symbol);
                contractInfo.setLogo(logo);
                contractInfo.setDecimals(StringUtils.valueOf(decimals));
                contractInfo.setType(EthContractsModel.TYPE_ERC20);
            }catch (Exception e){
                contractInfo.setType(EthContractsModel.TYPE_ANY);
            }
        }else if("ERC721".equals(tokenType)){
            String name = (String) meta.get("name");
            String symbol = (String) meta.get("symbol");
            Long deployedBlockNumber = NumberUtils.longValueOf(meta.get("deployedBlockNumber"));
            String contractDeployer = (String) meta.get("contractDeployer");
            contractInfo.setBlockNumber(deployedBlockNumber);
            contractInfo.setCreater(contractDeployer);
            contractInfo.setName(name);
            contractInfo.setSymbol(symbol);
            contractInfo.setType(EthContractsModel.TYPE_ERC721);
        }else if("ERC1155".equals(tokenType)){
            String name = (String) meta.get("name");
            String symbol = (String) meta.get("symbol");
            Long deployedBlockNumber = NumberUtils.longValueOf(meta.get("deployedBlockNumber"));
            String contractDeployer = (String) meta.get("contractDeployer");
            contractInfo.setBlockNumber(deployedBlockNumber);
            contractInfo.setCreater(contractDeployer);
            contractInfo.setName(name);
            contractInfo.setSymbol(symbol);
            contractInfo.setType(EthContractsModel.TYPE_ERC1155);
        }else{
            contractInfo.setType(EthContractsModel.TYPE_ANY);
        }
        return contractInfo;
    }

    public static void main(String[] args) throws IOException {
        {//{"address":"0xb8c77482e45f1f44de1745f52c74426c631bdd52","contractMetadata":{"name":"BNB","symbol":"BNB","totalSupply":"2147483647","tokenType":"NO_SUPPORTED_NFT_STANDARD","contractDeployer":"0x00c5e04176d95a286fcce0e68c683ca0bfec8454","deployedBlockNumber":3978343,"openSea":{"lastIngestedAt":"2023-03-17T00:20:40.000Z"}}}
            String addressERC20 = "0xb8c77482e45f1f44de1745f52c74426c631bdd52";
            EthContractsModel tokenType = initContractInfo(addressERC20);
            System.out.println(JsonUtil.object2String(tokenType));
        }
        {//{"address":"0xb8c77482e45f1f44de1745f52c74426c631bdd52","contractMetadata":{"name":"BNB","symbol":"BNB","totalSupply":"2147483647","tokenType":"NO_SUPPORTED_NFT_STANDARD","contractDeployer":"0x00c5e04176d95a286fcce0e68c683ca0bfec8454","deployedBlockNumber":3978343,"openSea":{"lastIngestedAt":"2023-03-17T00:20:40.000Z"}}}
            String addressERC20 = "0x7fc66500c84a76ad7e9c93437bfc5ac33e2ddae9";
            EthContractsModel tokenType = initContractInfo(addressERC20);
            System.out.println(JsonUtil.object2String(tokenType));
        }
        {//{"address":"0x67b36ba196804db198ac4c0a8359a1fafa5e5cff","contractMetadata":{"name":"Meta RPG","symbol":"META","totalSupply":"2500","tokenType":"ERC721","contractDeployer":"0x199d345db07a59d7272225e9f17c45183ddf58f0","deployedBlockNumber":14415155,"openSea":{"collectionName":"The Garden - NFT","safelistRequestStatus":"not_requested","imageUrl":"https://i.seadn.io/gae/H8jOCJuQokNqGBpkBN5wk1oZwO7LM8bNnrHCaekV2nKjnCqw6UB5oaH8XyNeBDj6bA_n1mjejzhFQUP3O1NfjFLHr3FOaeHcTOOT?w=500&auto=format","description":"A brand for the metaverse. Built by the community. View the collection at azuki.com/gallery.\n\nAzuki starts with a collection of 10,000 avatars that give you membership access to The Garden: a corner of the internet where artists, builders, and web3 enthusiasts meet to create a decentralized future. Azuki holders receive access to exclusive drops, experiences, and more. Visit azuki.com for more details.\n\nWe rise together. We build together. We grow together.\n\nReady to take the red bean?","externalUrl":"http://azuki.com","lastIngestedAt":"2023-03-19T02:04:27.000Z"}}}
            String addressERC721 = "0x67b36ba196804db198ac4c0a8359a1fafa5e5cff";
            EthContractsModel tokenType = initContractInfo(addressERC721);
            System.out.println(JsonUtil.object2String(tokenType));
        }
        {//{"address":"0xc36cf0cfcb5d905b8b513860db0cfe63f6cf9f5c","contractMetadata":{"name":"raw contract","symbol":"RAW","tokenType":"ERC1155","contractDeployer":"0x0d24de9dccd263b310b7809283786c58d1f6a667","deployedBlockNumber":10198649,"openSea":{"floorPrice":1.75E-4,"collectionName":"Town Star","safelistRequestStatus":"verified","imageUrl":"https://i.seadn.io/gcs/files/ad2ed32f6e82fb823618ea282d4a7715.png?w=500&auto=format","description":"From one of the founders of Zynga and some of the creative minds behind FarmVille and Words With Friends comes Town Star. Blockchain is the next evolution of gaming and has the $148B industry buzzing with excitement. It’s gaming re-imagined to benefit creators and players, alike.\n\nLearn more at [TownStar.com](https://townstar.com/)","externalUrl":"https://www.gala.games/","twitterUsername":"GoGalaGames","discordUrl":"https://discord.gg/sMNTHukHpd","lastIngestedAt":"2023-03-21T05:33:39.000Z"}}}
            String addressERC1151 = "0xc36cf0cfcb5d905b8b513860db0cfe63f6cf9f5c";
            EthContractsModel tokenType = initContractInfo(addressERC1151);
            System.out.println(JsonUtil.object2String(tokenType));
        }
        {//{"address":"0x5cda60f5d69cbae1bc9b211494620270314f45f7","contractMetadata":{"name":"","symbol":"","totalSupply":"","tokenType":"UNKNOWN"}}
            String addressAny = "0x5cda60f5d69cbae1bc9b211494620270314f45f7";
            EthContractsModel tokenType = initContractInfo(addressAny);
            System.out.println(JsonUtil.object2String(tokenType));
        }
    }
    Map<String, EthContractsModel> contractMap = new HashMap<>();
    /**
     * 初始化合约map
     */
    @Override
    public void initContractMap() {
        List<String> type = new ArrayList<>();
        type.add("erc20");
        type.add("erc721");
        type.add("erc1155");
        List<EthContractsModel> contractList = ethContractsDao.listContractInType(type);
        for(EthContractsModel contractsModel:contractList){
            contractMap.put(contractsModel.getAddress(), contractsModel);
        }
    }
}
