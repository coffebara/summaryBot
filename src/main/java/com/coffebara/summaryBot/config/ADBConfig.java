package com.coffebara.summaryBot.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@Configuration
public class ADBConfig {

    @Value("${adb.targetPackage}")
    private String targetPackage;

    @Value("${adb.localDirectory}")
    private String localDirectory;

    @Value("${adb.deviceDirectory}")
    private String deviceDirectory;

}
