package com.eth.example.springbootweb3;

import com.eth.SpringbootWeb3Application;
import com.eth.block.dao.EthBlockDao;
import com.eth.block.service.IEthBlockService;
import com.eth.ens.dao.EthEnsInfoDao;
import com.eth.etlTask.service.IEtlTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

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
//    etlTaskService.etlEthBlock(14669839L);
    Long high = 14669839L;
    for(long i = 14660638L;i<high;i++){
      Date beginTime = new Date();
      etlTaskService.etlEthBlock(i);
      System.out.println("costTime:"+(new Date().getTime() - beginTime.getTime()));
    }
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
