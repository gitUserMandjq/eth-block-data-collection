package com.example.springbootweb3;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@SpringBootApplication
public class SpringbootWeb3Application {

  public static void main(String[] args) {
    SpringApplication.run(SpringbootWeb3Application.class, args);
  }

  @Bean
  public Web3j web3j() {
    String ip  = "http://localhost:9545";
    OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
    builder.connectTimeout(30*1000, TimeUnit.MILLISECONDS);
    builder.writeTimeout(30*1000, TimeUnit.MILLISECONDS);
    builder.readTimeout(30*1000, TimeUnit.MILLISECONDS);
    OkHttpClient httpClient = builder.build();
    return Web3j.build(new HttpService(ip,httpClient,false));
  }

}
