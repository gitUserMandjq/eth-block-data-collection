package com.eth.example.springbootweb3;

import cn.hutool.json.JSONUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;

/**
 * @Description
 * @Author: 张小白
 * @Date: 2022/1/28 15:09
 */
@RestController
@Slf4j
public class TestLocalController {

  private String fromAddr = "0xd0Ab406A90B9C545B7f1d54c299318062875d0B0";
  private String privateKey = "0x9c5a2329a366f60832e8728658ddef7958318e2e708856b67d0bdb643bf47d48";
  private String toAddr = "0xef4E8ED043267276a75B17C7d9C481F3127D42e2";
  String serverHost = "http://localhost:8545";

//  private String fromAddr = "0x9bB1BB52f78B4AA7d913D3afA9F64565407F8103";
//  private String privateKey = "5ee21f66d20976a428e7cd242df1d7775eca1179ad0df6530f7a0ed00f15ac3d";
//  private String toAddr = "0xd59728Da828F587196c8e5d58437C7c76c6Fc888";
//  String serverHost = "http://localhost:9545";

  @RequestMapping("/local")
  public String index() {
    Web3j web3j = Web3j.build(new HttpService(serverHost));
    try {
      EthGetBalance fromBalance = web3j.ethGetBalance(fromAddr, DefaultBlockParameterName.LATEST)
          .send();
      log.info("From Account Balance: {}", fromBalance.getBalance());
      EthBlockNumber ethBlockNumber = web3j.ethBlockNumber().send();
      log.info("ethBlockNumber: {}", ethBlockNumber.getBlockNumber());
      EthGetTransactionCount transactionCount = web3j
          .ethGetTransactionCount(fromAddr, DefaultBlockParameterName.LATEST).send();

      EthGasPrice gasPrice = web3j.ethGasPrice().send();
      // todo 注意 Error: Number can only safely store up to 53 bits  这里必须使用WEI作为单位，进行转换，数字不要太大。
      // "message":"Number can only safely store up to 53 bits"},"id":10,"jsonrpc":"2.0"
      // rlp: input string too long for uint64, decoding into (types.LegacyTx).Gas
      BigInteger gasLimit = Convert.toWei(BigDecimal.valueOf(21000), Unit.WEI).toBigInteger();

      BigInteger transferAmount = Convert.toWei("0.01", Unit.ETHER).toBigInteger();
      log.info("transferAmount: {}", transferAmount);

      BigInteger nonce = transactionCount.getTransactionCount();
      String txHash = "";
      RawTransaction transaction = RawTransaction
          .createTransaction(nonce, gasPrice.getGasPrice(), gasLimit, toAddr, transferAmount, "");
      // 私钥进行判断 去掉0x
      if (privateKey.startsWith("0x")) {
        privateKey = privateKey.substring(2);
      }
      ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(privateKey, 16));
      Credentials credentials = Credentials.create(ecKeyPair);

      // todo {"error":{"code":-32000,"message":"only replay-protected (EIP-155) transactions allowed over RPC"},"id":10,"jsonrpc":"2.0"}
      // 追加 chainId
      byte[] signMessage = TransactionEncoder.signMessage(transaction, 348681503L, credentials);

      String signData = Numeric.toHexString(signMessage);
      log.info("signData: {}", signData);
      if (!"".equals(signData)) {
        EthSendTransaction send = web3j.ethSendRawTransaction(signData).send();
        log.info("result: {}", JSONUtil.toJsonStr(send));
        txHash = send.getTransactionHash();
      }
      log.info("txHash: {}", txHash);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "running";
  }
}
