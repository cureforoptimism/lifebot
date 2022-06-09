package com.cureforoptimism.lifebot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Slf4j
@Configuration
@EnableScheduling
@EnableTransactionManagement
@RequiredArgsConstructor
public class SpringConfiguration {
  @Bean
  public Web3j web3j() {
    return Web3j.build(new HttpService("https://arb1.arbitrum.io/rpc"));
  }
}
