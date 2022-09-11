package com.eth.example.springbootweb3;

import com.eth.SpringbootWeb3Application;
import com.eth.block.dao.EthBlockDao;
import com.eth.block.model.EthBlockModel;
import com.eth.block.service.IEthBlockService;
import com.eth.etlTask.service.IEtlTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.protocol.core.methods.response.EthBlock;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Optional;

//@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootWeb3Application.class)
class SpringbootWeb3ApplicationTests {
  @Resource
  EthBlockDao ethBlockDao;
  @Resource
  IEthBlockService ethBlockService;
  @Resource
  IEtlTaskService etlTaskService;
  @Test
  void contextLoads() throws Exception {
    etlTaskService.etlEthBlock(14669839L);
  }

}
