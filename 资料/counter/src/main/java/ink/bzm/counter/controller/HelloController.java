package ink.bzm.counter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: buzhengmiao    www.bzm.ink
 * @time: 2020/7/30 10:04
 */
@RestController
public class HelloController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping("/hello")
    public String hello() {
        Long views = redisTemplate.opsForValue().increment("views");
        return "hello Bzm" + views;
    }
}
