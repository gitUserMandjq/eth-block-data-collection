package com.eth.example.springbootweb3;

import com.eth.SpringbootWeb3Application;
import com.eth.block.dao.EthBlockDao;
import com.eth.block.service.IEthBlockService;
import com.eth.ens.dao.EthEnsInfoDao;
import com.eth.ens.model.EnsDomainsDTO;
import com.eth.ens.model.EnsDomainsQO;
import com.eth.etlTask.service.IEtlTaskService;
import com.eth.framework.base.model.PageParam;
import com.eth.framework.base.utils.JsonUtil;
import com.eth.framework.base.utils.PageUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

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
    etlTaskService.etlEthBlock(14669839L);
  }
  @Test
  void contextLoads2() throws Exception {
    EnsDomainsQO qo = new EnsDomainsQO();
    Integer page = 1;
    Integer size = 30;
    String sidx = "";
    String sord = "";
    PageParam pageParam = PageUtils.constructPageParam(page, size, 1, sidx, sord);
    List<EnsDomainsDTO> list = ethEnsInfoDao.listEnsDomain(qo, pageParam);
    System.out.println(JsonUtil.object2String(list));
  }

}
