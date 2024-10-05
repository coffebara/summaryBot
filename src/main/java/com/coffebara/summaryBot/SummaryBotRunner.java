package com.coffebara.summaryBot;

import com.coffebara.summaryBot.service.ADBService;
import com.coffebara.summaryBot.service.ExcelService;
import com.coffebara.summaryBot.service.OCRService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryBotRunner implements ApplicationRunner {

    private final ADBService adbService;
    private final OCRService ocrService;
    private final ExcelService excelService;

    //공통 예외 처리
    private static void exceptionHandler(Exception e) {
        //공통 처리
        System.out.println("사용자 메세지: 죄송합니다. 알 수 없는 문제가 발생했습니다.");
        System.out.println("===개발자용 디버깅 메세지===");
        e.printStackTrace(System.out); //스택 트레이스 출력

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("메크로 실행중...");
        long startTime = System.currentTimeMillis();
        long endTime;

        try {
            adbService.takeScreenShot();
            log.info("스크린샷 완료.");

            ocrService.ExtractData();
            log.info("OCR 데이터 추출 완료.");

            excelService.saveMembersToExcel();
            log.info("엑셀에 데이터 저장 완료.");

            endTime = System.currentTimeMillis();
            log.info("메크로 실행 완료! 소요 시간: " + (endTime - startTime) + "ms");

        } catch (Exception e) {
            exceptionHandler(e);
        }

    }


}
