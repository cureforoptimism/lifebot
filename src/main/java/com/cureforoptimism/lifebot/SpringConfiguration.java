package com.cureforoptimism.lifebot;

import com.cureforoptimism.lifebot.domain.MarketPrice;
import com.cureforoptimism.lifebot.service.MarketPriceMessageSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
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

  @Bean
  public RedisMessageListenerContainer listenerContainer(
      MessageListenerAdapter listenerAdapter, RedisConnectionFactory connectionFactory) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.addMessageListener(listenerAdapter, new PatternTopic("market-price"));
    return container;
  }

  @Bean
  public MessageListenerAdapter listenerAdapter(MarketPriceMessageSubscriber subscriber) {
    MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(subscriber);
    messageListenerAdapter.setSerializer(new Jackson2JsonRedisSerializer<>(MarketPrice.class));
    return messageListenerAdapter;
  }

  @Bean
  RedisTemplate<String, MarketPrice> redisTemplate(
      RedisConnectionFactory connectionFactory,
      Jackson2JsonRedisSerializer<MarketPrice> serializer) {
    RedisTemplate<String, MarketPrice> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(connectionFactory);
    redisTemplate.setDefaultSerializer(serializer);
    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }

  @Bean
  public Jackson2JsonRedisSerializer<MarketPrice> jackson2JsonRedisSerializer() {
    return new Jackson2JsonRedisSerializer<>(MarketPrice.class);
  }
}
