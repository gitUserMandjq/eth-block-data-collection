package com.eth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling  //扫描定时器
@ConditionalOnProperty(prefix = "scheduled", name = "enabled", havingValue = "true")
public class ScheduleConfig {
}
