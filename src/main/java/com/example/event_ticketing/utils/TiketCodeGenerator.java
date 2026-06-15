package com.example.event_ticketing.utils;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class TiketCodeGenerator {
    public String generate() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        long timestamp = System.currentTimeMillis();
        return "TKT-" + uuid + "-" + timestamp;
    }
}
