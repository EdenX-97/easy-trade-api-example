package com.easytrade.easytradeapi.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component("keyGenerator")
public class KeyGeneratorConfig implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return "";
    }
}
