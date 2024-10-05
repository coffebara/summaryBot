package com.coffebara.summaryBot.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class MyFileUtil {

    public static void createDirectoryIfNotExists(String directory) {
        Path path = Paths.get(directory);
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path); // 모든 중간 디렉토리도 함께 생성
                log.info("디렉토리가 생성되었습니다: " + directory);
            } catch (IOException e) {
                log.info("디렉토리 생성 실패: " + e.getMessage());
            }
        } else {
            log.info("디렉토리가 이미 존재합니다: " + directory);
        }
    }
}
