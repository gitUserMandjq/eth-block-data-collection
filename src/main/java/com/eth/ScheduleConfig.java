package com.eth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling  //扫描定时器
@ConditionalOnProperty(prefix = "scheduled", name = "enabled", havingValue = "true")
public class ScheduleConfig {
}
