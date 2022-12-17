package com.eth.example.springbootweb3;

import com.eth.SpringbootWeb3Application;
import com.eth.block.dao.EthBlockDao;
import com.eth.block.service.IEthBlockService;
import com.eth.ens.dao.EthEnsInfoDao;
import com.eth.etlTask.service.IEtlTaskService;
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
  @Test
  void contextLoads() throws Exception {
//    etlTaskService.etlEthBlock(14669839L, 3);
//    etlTaskService.etlEns(14669839L, 3);
    Long high = 14000000L;
//    Long start = 9380422L;
    Long start = 10000000L;
//    Long start = 14669839L;
//    Long high = start + 5000L;
    Integer batchNumber = 10;
//    dealEtlTask(start, high, batchNumber);
//    etlTaskService.dealErrorEth();
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
