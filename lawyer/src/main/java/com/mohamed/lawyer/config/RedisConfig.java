package com.mohamed.lawyer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Create ObjectMapper with proper configuration
     * This ensures all serializers use the same mapper
     */
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Configure Jackson for serialization
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // Register modules for Java 8+ and JSR310 (LocalDate, LocalDateTime, etc)
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());

        // Configure features
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        objectMapper.deactivateDefaultTyping();
        return objectMapper;
    }

    /**
     * Configure RedisTemplate with String serialization
     * This is required for proper key-value storage
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper redisObjectMapper) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // String serializer for keys
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // Generic JSON serializer with configured ObjectMapper
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        // Configure serialization
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Configure CacheManager with matching serialization
     * IMPORTANT: Must use same serialization as RedisTemplate
     */
    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            ObjectMapper redisObjectMapper) {

        // Create the same JSON serializer
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        // Configure cache serialization context
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // Set TTL to 10 minutes
                .entryTtl(Duration.ofMinutes(10))
                // Don't cache null values
                .disableCachingNullValues()
                // Set key prefix
                // Configure serialization for keys
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                // Configure serialization for values
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                jackson2JsonRedisSerializer
                        )
                );

        return RedisCacheManager.create(connectionFactory);
    }
}
