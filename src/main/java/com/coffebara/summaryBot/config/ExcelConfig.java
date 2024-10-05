package com.coffebara.summaryBot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ExcelConfig {

    @Value("${excel.path}")
    private String excelPath;
}
