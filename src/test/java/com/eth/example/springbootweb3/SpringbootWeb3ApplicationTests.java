package com.eth.example.springbootweb3;

import com.eth.SpringbootWeb3Application;
import com.eth.account.service.IAccountService;
import com.eth.block.dao.EthBlockDao;
import com.eth.block.service.IEthBlockService;
import com.eth.ens.dao.EthEnsInfoDao;
import com.eth.etlTask.service.IEtlTaskService;
import com.eth.timer.service.ITimerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.utils.Numeric;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

//@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootWeb3Application.class)
class SpringbootWeb3ApplicationTests {
  @Resource
  EthBlockDao ethBlockDao;
  @Resource
  IEthBlockService ethBlockService;
  @Resource
  IEtlTaskService etlTaskService;
  @Resource
  EthEnsInfoDao ethEnsInfoDao;
  @Resource
  ITimerService timerService;
  @Resource
  IAccountService accountService;
  @Test
  void contextLoads() throws Exception {
//    Long high = 14000000L;
//    Long start = 10000000L;
//    Integer batchNumber = 10;
//    timerService.dealErrorEthTask(100);
    testAccountCode();
  }
  private void testAccountCode() throws Exception {
    String address1 = "0xe785E82358879F061BC3dcAC6f0444462D4b5330";//ERC721
    String address2 = "0xbc4ca0eda7647a8ab7c2061c2e118a18a936f13d";//ERC721
    String address3 = "0x8D57dAc649760e11660B38f201292095f0000eA8";//普通账户
    String address4 = "0x57f1887a8BF19b14fC0dF6Fd9B2acc9Af147eA85";//ens账号
    String address5 = "0xB8c77482e45F1F44dE1745F52C74426C631bDD52";//ERC20
    String code1 = accountService.getAccountCode(address1);
    String code2 = accountService.getAccountCode(address2);
    String code3 = accountService.getAccountCode(address3);
    String code4 = accountService.getAccountCode(address4);
    String code5 = accountService.getAccountCode(address5);
    System.out.println("code1:"+code1);
//    System.out.println(new String(Numeric.hexStringToByteArray(code1)));
    System.out.println("*****************************************************");
    System.out.println(FunctionReturnDecoder.decodeAddress(code1));
    System.out.println(new String(Numeric.hexStringToByteArray(FunctionReturnDecoder.decodeAddress(code1)), "utf8"));
//    System.out.println(code2);
//    System.out.println(new String(StringUtils.hexStringToBytes(code2.substring(2))));
//    System.out.println(code3);
//    System.out.println(new String(StringUtils.hexStringToBytes(code3.substring(2))));
//    System.out.println(code4);
//    System.out.println(new String(StringUtils.hexStringToBytes(code4.substring(2))));
//    System.out.println(code5);
//    System.out.println(new String(StringUtils.hexStringToBytes(code5.substring(2))));
  }
  private void dealEtlTask(Long start, Long high, Integer batchNumber) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch((int)(high - start + 1));
    Semaphore lock = new Semaphore(20);
    Date startTime = new Date();
    for(long i = start; i<= high; i = i+batchNumber){
      long begin = i;
      long end = i+batchNumber - 1;
      if(end > high){
        end = high;
      }
      List<Long> blockNumber = new ArrayList<>();
      for(long j=begin;j<=end;j++){
        blockNumber.add(j);
      }
      etlTaskService.etlEns(blockNumber, 0, latch, lock);
//      etlTaskService.etlEthBlock(i, 0, latch, lock);
      System.out.println("allTime"+i+":"+(new Date().getTime() - startTime.getTime()));
    }
    latch.await();
    System.out.println("totalAllTime:"+(new Date().getTime() - startTime.getTime()));
  }
//  @Test
//  void contextLoads2() throws Exception {
//    EnsDomainsQO qo = new EnsDomainsQO();
//    Integer page = 1;
//    Integer size = 30;
//    String sidx = "";
//    String sord = "";
//    PageParam pageParam = PageUtils.constructPageParam(page, size, 1, sidx, sord);
//    List<EnsDomainsDTO> list = ethEnsInfoDao.listEnsDomain(qo, pageParam);
//    System.out.println(JsonUtil.object2String(list));
//  }

}
