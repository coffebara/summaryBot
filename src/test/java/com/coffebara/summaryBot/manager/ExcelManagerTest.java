package com.coffebara.summaryBot.manager;

import com.coffebara.summaryBot.SummaryBotRunner;
import com.coffebara.summaryBot.config.ExcelConfig;
import com.coffebara.summaryBot.entity.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "excel.path=C:/summaryBot/summaryBot.xlsx")
class ExcelManagerTest {

    @Autowired
    ExcelManager excelManager;
    @Autowired
    ExcelConfig excelConfig;
    @MockBean
    private SummaryBotRunner summaryBotRunner;  // SummaryBotRunner 빈을 Mock으로 대체
    @MockBean
    private ADBManager adbManager;  // ADBManager를 목 객체로 대체

    @Test
    @DisplayName("엑셀 파일 경로 가져오기")
    void getExcelPath() throws Exception {
        //given
        String localPath = "C:/summaryBot/summaryBot.xlsx";

        //when
        String excelPath = excelConfig.getExcelPath();

        //then
        assertEquals(localPath, excelPath);
    }

    @Test
    @DisplayName("엑셀 파일 실행여부 확인")
    void checkFileLockedTest() throws Exception {
        //given
        File excelFile = new File(excelConfig.getExcelPath());
        //when
        boolean fileLocked = excelManager.isFileLocked(excelFile);

        //then
        assertTrue(fileLocked); //현재 엑셀파일을 열고있다면 성공
    }

    @Test
    @DisplayName("")
    void writeToExcel() throws Exception {
        //given
        List<Member> memberList = new ManagedList<>();
        for (int i = 0; i < 10; i++) {
            String name = "날새" + i;
            int ranking = i + 1;
            int idCode = 1000 + i;
            String ally = (i > 5) ? "DG" : "GR";
            int power = 400000 + (80 - i) * 100;
            int death = 1000 + (100 - i) * 10;
            int killPoint4T = 10000 * (50 - i);
            int killPoint5T = 5000 * (50 - i);
            int totalKillPoint = killPoint4T * 4 + killPoint5T * 10;

            Member member = new Member(name, ranking, "ㅁㄴㅇ", "ㅁㄴㅇ", "dasd");
            member.setMemberMainData(idCode, ally, power, death, totalKillPoint, killPoint4T, killPoint5T);
            memberList.add(member);
        }


        //when
        excelManager.updateOrInsertMemberData(memberList);

        //then

    }

    @Test
    @DisplayName("")
    void writeToExcelUpdate() throws Exception {
        //given
        List<Member> memberList = new ManagedList<>();
        for (int i = 0; i < 10; i++) {
            String name = "날새" + i;
            int ranking = i + 1;
            int idCode = 1000 + i;
            String ally = (i > 5) ? "DG" : "GR";
            int power = 300000 + (80 - i) * 100;
            int death = 2000 + (100 - i) * 10;
            int killPoint4T = 40000 * (50 - i);
            int killPoint5T = 9000 * (50 - i);
            int totalKillPoint = killPoint4T * 4 + killPoint5T * 10;

            Member member = new Member(name, ranking, "ㅁㄴㅇ", "ㅁㄴㅇ", "dasd");
            member.setMemberMainData(idCode, ally, power, death, totalKillPoint, killPoint4T, killPoint5T);
            memberList.add(member);
        }


        //when
        excelManager.updateOrInsertMemberData(memberList);

        //then

    }
}