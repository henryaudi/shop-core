package org.supershop.shopcore.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Collections;

@Slf4j
@Service
public class RedisService {

    @Resource
    private JedisPool jedisPool;

    public RedisService setValue(String key, Long value) {
        Jedis client = jedisPool.getResource();
        client.set(key, value.toString());
        client.close();

        return this;
    }

    public void setValue(String key, String value) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.set(key, value);
        jedisClient.close();
    }

    public String getValue(String key) {
        Jedis client = jedisPool.getResource();
        String value = client.get(key);
        client.close();

        return value;
    }

    public boolean stockDeductValidation(String key) {
        try (Jedis client = jedisPool.getResource()) {
            String script = "if redis.call('exists', KEYS[1]) == 1 then\n" +
                    "    local stock = tonumber(redis.call('get', KEYS[1]))\n" +
                    "    if (stock <= 0) then\n" +
                    "        return -1\n" +
                    "    end;\n" +
                    "    \n" +
                    "    redis.call('decr', KEYS[1]);\n" +
                    "    return stock - 1;\n" +
                    "end;\n" +
                    "\n" +
                    "return -1;";

            long stock = (Long) client.eval(script, Collections.singletonList(key), Collections.emptyList());

            if (stock < 0) {
                System.out.println("Item is out of stock!");
                return false;
            }

            System.out.println("Order has been created successfully!");
            return true;
        } catch (Throwable throwable) {
            System.out.println("Order was failed to be added - " + throwable.toString());
            return false;
        }
    }

    public void revertStock(String key) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.incr(key);
        jedisClient.close();
    }

    public void addLimitMember(long activityId, long userId) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.sadd("seckillActivity_users:" + activityId, String.valueOf(userId));
    }

    public void removeLimitMember(long activityId, long userId) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.srem("seckillActivity_users:" + activityId, String.valueOf(userId));
        jedisClient.close();
    }

    public boolean isInLimitMember(long activityId, long userId) {
        Jedis jedisClient = jedisPool.getResource();
        boolean sismember = jedisClient.sismember("seckillActivity_users:" + activityId, String.valueOf(userId));
        jedisClient.close();
        log.info("userId:{}  activityId:{}  already_ordered:{}", activityId, userId, sismember);
        return sismember;
    }

    public boolean tryGetDistributedLock(String lockKey, String requestId, int expTime) {
        Jedis jedisClient = jedisPool.getResource();
        String result = jedisClient.set(lockKey, requestId, "NX", "PX", expTime);
        jedisClient.close();

        return result.equals("OK");
    }

    public boolean releaseDistributedLock(String lockKey, String requestId) {
        Jedis jedisClient = jedisPool.getResource();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        long result = (long) jedisClient.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

        return result == 1L;
    }
}
