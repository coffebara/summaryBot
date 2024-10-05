package com.coffebara.summaryBot.service;

import com.coffebara.summaryBot.entity.Member;
import com.coffebara.summaryBot.manager.ADBManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.coffebara.summaryBot.utils.ClipboardUtil.getClipboard;

@Slf4j
@Service
@RequiredArgsConstructor
public class ADBService {

    private final ADBManager adbManager;
    public static List<Member> memberList = new ArrayList<>();

    public void takeScreenShot() throws IOException, InterruptedException {
        boolean isRunning = false;

        // ADB 명령어 실행
        String packageAndActivity = adbManager.getPackageAndActivity(); // 패키지명/액티비티명

        // 앱 실행여부 확인
        isRunning = adbManager.checkRunningNow();
        if (!isRunning) {
            adbManager.startApp();
            adbManager.tabScreen(0, 0); // 게임 진입
            Thread.sleep(30_000); // 첫 화면 로딩 대기
        }

        //랭킹으로 이동
        adbManager.tabScreen(0, 0); //내 위치
        adbManager.tabScreen(490, 700); //랭킹
        adbManager.tabScreen(200, 500); //개인 투력 랭킹


        for (int i = 0; i < 10; i++) {
            int ranking = (i + 1);

            if (i < 4) {
                adbManager.tabScreen(218, 250 + 100 * i);
            } else {
                adbManager.tabScreen(218, 650);
            }

            Thread.sleep(100);
            adbManager.tabScreen(620, 236); // 닉네임 복사
            String name = getClipboard();
            Thread.sleep(50);
            String mainImgName = adbManager.takeScreen(ranking + "_m");//메인 화면 캡쳐
            adbManager.tabScreen(1108, 304); // 처치 통계
            String detailImg1Name = adbManager.takeScreen(ranking + "_d1");// 세부 화면1 캡쳐
            adbManager.tabScreen(350, 700); // 세부 정보로 나가기
            String detailImg2Name = adbManager.takeScreen(ranking + "_d2");// 세부 화면1 캡쳐
            adbManager.tabScreen(100, 100); // 외부로 나가기
            adbManager.tabScreen(100, 100); // 외부로 나가기

            memberList.add(new Member(name, ranking, mainImgName, detailImg1Name, detailImg2Name));
        }

        adbManager.pullScreenshotsAndDelete();

        log.info("=========스크린샷 저장 완료========");
    }
}
