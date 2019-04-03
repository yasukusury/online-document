package org.yasukusury.onlinedocument.commons.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.yasukusury.onlinedocument.biz.service.BookService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author 30254
 * creadtedate: 2019/3/4
 */
@Configuration
public class RedisConf {
    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    RedisMessageListenerContainer redisListenerContainer(RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
//        container.addMessageListener(new RedisExpiredListener(), new PatternTopic("__keyevent@0__:expired"));
        return container;
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public RedisKeyExpirationListener redisKeyExpirationListener(RedisMessageListenerContainer listenerContainer, BookService bookService){
        RedisKeyExpirationListener listener = new RedisKeyExpirationListener(listenerContainer);
        listener.register(new RedisExpirationHandler(BookService.BOOK_READ_MAP_KEY, bookService::redisBookReadSetback));
        return listener;
    }
    /**
     * 监听所有db的过期事件__keyevent@*__:expired"
     */
    public static class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

        public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
            super(listenerContainer);
        }

        private List<RedisExpirationHandler> handlerList = new ArrayList<>();

        @Override
        public void onMessage(Message message, byte[] pattern) {
            // 用户做自己的业务处理即可,注意message.toString()可以获取失效的key
            String expiredKey = message.toString();
            for (RedisExpirationHandler handler : handlerList){
                if (expiredKey.startsWith(handler.prefix)){
                    handler.doHandle.accept(message);
                }
            }
        }

        public boolean register(RedisExpirationHandler redisExpirationHandler) {
            return handlerList.add(redisExpirationHandler);
        }
    }


    public static class RedisExpirationHandler{
        public RedisExpirationHandler(String prefix, Consumer<Message> doHandle) {
            this.prefix = prefix;
            this.doHandle = doHandle;
        }

        @Getter
        private String prefix;

        @Getter
        private Consumer<Message> doHandle;
    }
}
