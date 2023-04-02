package com.eth.account.service.impl;

import com.eth.account.dao.EthAccountSmartContractDao;
import com.eth.account.dao.EthAccountSmartDao;
import com.eth.account.model.EthAccountSmartContractModel;
import com.eth.account.model.EthAccountSmartModel;
import com.eth.account.service.IAccountService;
import com.eth.framework.base.common.constant.KeyConst;
import com.eth.framework.base.common.model.PageData;
import com.eth.framework.base.common.model.PageParam;
import com.eth.framework.base.common.utils.ObjectManageUtils;
import com.eth.framework.base.common.utils.PageUtils;
import com.eth.framework.base.common.utils.Web3jUtil;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements IAccountService {
    @Resource
    EthAccountSmartDao ethAccountSmartDao;
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
     * 查询导入的聪明钱包
     * @param beginTime
     * @param pageInfo
     * @return
     */
    @Override
    public PageData<EthAccountSmartModel> listAccountSmart(Date beginTime, PageParam pageInfo) throws Exception {
        Specification<EthAccountSmartModel> specification = new Specification<EthAccountSmartModel>() {
            @Override
            public Predicate toPredicate(Root<EthAccountSmartModel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate= cb.conjunction();
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
}
