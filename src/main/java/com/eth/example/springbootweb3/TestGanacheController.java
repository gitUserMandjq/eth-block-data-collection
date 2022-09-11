package com.eth.example.springbootweb3;

import cn.hutool.json.JSONUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
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
public class TestGanacheController {

  @Autowired
  private Web3j web3j;
  private String fromAddr = "0x9bB1BB52f78B4AA7d913D3afA9F64565407F8103";
  private String fromPrivateKey = "5ee21f66d20976a428e7cd242df1d7775eca1179ad0df6530f7a0ed00f15ac3d";
  private String toAddr = "0xd59728Da828F587196c8e5d58437C7c76c6Fc888";
  private static BigDecimal GAS_LIMIT = BigDecimal.valueOf(21000L);

  @RequestMapping("/ganache")
  public String index() {
    try {
      Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
      log.info("web3 version: {}", web3ClientVersion.getWeb3ClientVersion());

      EthBlockNumber blockNumber = web3j.ethBlockNumber().send();
      log.info("web3 blockNumber: {}", blockNumber.getBlockNumber().longValue());

      getGasPrice();

      getNonce(fromAddr);

      BigInteger balance = getBalance(fromAddr);
      // 转为以太坊ETH
      BigDecimal bigEther = Convert
          .fromWei(String.valueOf(balance), Unit.ETHER);
      log.info("web3 ethGetBalance:{},ether:{}", balance.longValue(), bigEther);

      // 构建交易
      // 1 转账
      createTransaction(BigDecimal.ONE);

      // 2 私钥签名转账
      signTransactionAndSend(toAddr, BigDecimal.ONE, fromPrivateKey, null);

    } catch (IOException e) {
      e.printStackTrace();
    }
    return "index";
  }

  public BigInteger getNonce(String fromAddr) throws IOException {
    EthGetTransactionCount transactionCount = web3j
        .ethGetTransactionCount(fromAddr, DefaultBlockParameterName.LATEST).send();
    BigInteger nonce = transactionCount.getTransactionCount();
    log.info("web3 nonce:{}", nonce.longValue());
    return nonce;
  }

  public BigInteger getGasPrice() throws IOException {
    EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
    BigInteger gasFee = ethGasPrice.getGasPrice();
    log.info("web3 ethGasPrice: {}", ethGasPrice.getGasPrice().longValue());
    return gasFee;
  }

  public BigInteger getBalance(String fromAddr) throws IOException {
    EthGetBalance ethGetBalance = web3j.ethGetBalance(fromAddr, DefaultBlockParameterName.LATEST)
        .send();
    BigInteger balance = ethGetBalance.getBalance();
    log.info("web3 balance: {}", balance.longValue());
    return balance;
  }

  /**
   * 非私钥签名转账，需账户被解锁，否则回报异常
   * {"error":{"code":-32000,"message":"only replay-protected (EIP-155) transactions allowed over RPC"},"id":10,"jsonrpc":"2.0"}
   * @param amount 转账数量 多少ETH
   * @throws IOException
   */
  public void createTransaction(BigDecimal amount) throws IOException {
    BigInteger nonce = getNonce(fromAddr);
    BigInteger ethGasPrice = getGasPrice();
    BigInteger transferAmount = Convert.toWei(amount, Unit.ETHER).toBigInteger();
    log.info("transferAmount:{} = amount:{}", transferAmount, amount);
    Transaction etherTransaction = Transaction
        .createEtherTransaction(fromAddr, nonce, ethGasPrice,
            null, toAddr, transferAmount);
    // 估算gasLimit
    EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(etherTransaction).send();
    log.info("EthEstimateGas: {}", ethEstimateGas.getAmountUsed());
    // 调用查看余额 wei为单位
    BigInteger biBalance = getBalance(fromAddr);
    log.info("balance:{}", biBalance);
    // total amount
    BigInteger totalAmount = transferAmount.add(ethEstimateGas.getAmountUsed());
    log.info("totalAmount:{}", totalAmount);
    if (totalAmount.compareTo(biBalance) >= 0) {
      log.error("余额不足！！");
      return;
    }
    EthSendTransaction send = web3j.ethSendTransaction(etherTransaction).send();
    log.info("no sign result: {}", JSONUtil.toJsonStr(send));

  }

  /**
   * 私钥签名转账
   * @param to             接收账户
   * @param transferAmount 转账多少ETH
   * @param privateKey     发起者私钥
   * @param chainId        链ID
   * @throws IOException
   */
  public void signTransactionAndSend(String to,
      BigDecimal transferAmount, String privateKey, Long chainId)
      throws IOException {
    BigInteger nonce = getNonce(fromAddr);
    BigInteger gasPrice = getGasPrice();
    BigInteger gasLimit = Convert.toWei(GAS_LIMIT, Unit.WEI).toBigInteger();
    BigInteger value = Convert.toWei(transferAmount, Unit.ETHER).toBigInteger();
    String txHash = "";
    RawTransaction transaction = RawTransaction
        .createTransaction(nonce, gasPrice, gasLimit, to, value, "");
    // 私钥进行判断 去掉0x
    if (privateKey.startsWith("0x")) {
      privateKey = privateKey.substring(2);
    }

    ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(privateKey, 16));
    Credentials credentials = Credentials.create(ecKeyPair);
    byte[] signMessage;
    if (!Objects.isNull(chainId)) {
      signMessage = TransactionEncoder.signMessage(transaction, chainId, credentials);
    } else {
      signMessage = TransactionEncoder.signMessage(transaction, credentials);
    }

    String signData = Numeric.toHexString(signMessage);
    log.info("signData: {}", signData);
    if (!"".equals(signData)) {
      EthSendTransaction send = web3j.ethSendRawTransaction(signData).send();
      log.info("result: {}", JSONUtil.toJsonStr(send));
      txHash = send.getTransactionHash();
    }
    log.info("txHash: {}", txHash);
  }
}
