package com.eth;

import com.eth.block.dao.EthBlockDao;
import com.eth.framework.base.common.repository.impl.BatchSaveRepositoryImpl;
import okhttp3.OkHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
        "com.eth"},repositoryBaseClass = BatchSaveRepositoryImpl.class)
@RestController
public class SpringbootWeb3Application {

  public static void main(String[] args) {
    SpringApplication.run(SpringbootWeb3Application.class, args);
  }
  @Resource
  EthBlockDao ethBlockDao;
  @Bean
  public Web3j web3j() {
    String ip  = "https://eth-mainnet.alchemyapi.io/v2/J4ipt__b_exS1cez4CO9KhRkXEYWxUcJ";
    OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
    builder.connectTimeout(30*1000, TimeUnit.MILLISECONDS);
    builder.writeTimeout(30*1000, TimeUnit.MILLISECONDS);
    builder.readTimeout(30*1000, TimeUnit.MILLISECONDS);
    OkHttpClient httpClient = builder.build();
    return Web3j.build(new HttpService(ip,httpClient,false));
  }
  @RequestMapping("/")
  public String greeting() {
    return "Hello,World!";
  }

}
