package com.perfact.be.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.password}")
  private String password;

  @Value("${spring.data.redis.port}")
  private int port;

  private LettuceConnectionFactory createConnectionFactory(int dbIndex) {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
    config.setDatabase(dbIndex);
    if (!password.isEmpty()) {
      config.setPassword(RedisPassword.of(password));
    }
    LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
    factory.afterPropertiesSet();
    return factory;
  }

  private RedisTemplate<String, String> createRedisTemplate(LettuceConnectionFactory factory) {
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    template.afterPropertiesSet();
    return template;
  }

  @Bean
  public RedisTemplate<String, String> rtRedisTemplate() {
    return createRedisTemplate(createConnectionFactory(0));
  }


}