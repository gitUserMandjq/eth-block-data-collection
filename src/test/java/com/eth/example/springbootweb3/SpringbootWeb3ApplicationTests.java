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
//  @Autowired
//  private Web3j web3j;
  @Test
  void contextLoads() throws Exception {
//    etlTaskService.etlEthBlock(14669839L, 3);
//    etlTaskService.etlEns(14669839L, 3);
    Date beginTime = new Date();
    //2023-3-1
    Long high = 16727705L;
//    Long start = 9380422L;
    //2022-8-1

    Long start = 15492201L;
//    Long start = 14669839L;
//    Long high = start + 5000L;
    Integer batchNumber = 10;
//    ids.add(10000001L);
//    ids.add(10000002L);
//    ids.add(10000003L);
//    ids.add(10000004L);
//    etlTaskService.etlBl, falseock(ids, 3);
//    etlTaskService.etlCommonBlock(ids, 3);
//    timerService.dealErrorEthTask(100);
//    timerService.dealEtlTask(20);
//    dealEtlTask(start, high, batchNumber);
    etlTaskService.etlCommonBlock(start, start + 1000, 20, false);
//    etlTaskService.etlCommonBlock(20, 10000L, false);
//    for(int i=0;i<10;i++){
//      etlTaskService.dealErrorComtask(100);
//    }
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
