package com.coffebara.summaryBot.manager;

import com.coffebara.summaryBot.SummaryBotRunner;
import com.coffebara.summaryBot.config.OCRConfig;
import com.coffebara.summaryBot.utils.OpenCVUtil;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OCRManagerTest {
    @Autowired
    OCRConfig ocrConfig;
    @Autowired
    OCRManager ocrManager;
    @MockBean
    private SummaryBotRunner summaryBotRunner;  // SummaryBotRunner 빈을 Mock으로 대체


    @Test
    @DisplayName("연맹 가져오기")
    void getAllyTest() throws Exception {
        //given
        String imgPath = "C:\\summaryBot\\20241006\\20241006_6_m.png";
        String ally = "";
        //when
        List<Object> userMainDataList = ocrManager.getUserMainDataList(imgPath);
        System.out.println("userMainDataList.get(0) = " + userMainDataList.get(0));
        String findedAlly = userMainDataList.get(0)==null? null : (String) userMainDataList.get(0);
        //then
        assertEquals(ally, findedAlly);

    }
    @Test
    @DisplayName("데스 가져오기")
    void getDeath() throws Exception {
        //given
        String imgPath = "C:\\summaryBot\\20241006\\20241006_12_d2.png";
        int death = 40847; //실제 데이터

        //when
        int deathOCR = ocrManager.getUserDeath(imgPath);

        //then
        assertEquals(deathOCR, death);

    }

    @Test
    @DisplayName("킬 토탈포인트")
    void getTotalKillPointsTest() throws Exception {
        //given
        String imgPath = "C:\\summaryBot\\20241006\\20241006_12_m.png";
        //when
        List<Object> userMainDataList = ocrManager.getUserMainDataList(imgPath);
        String ally = "DM23";
        int power = 8467030;
        int totalKillPoint = 966636;
        //then
        assertEquals(userMainDataList.get(0), ally);
        assertEquals(userMainDataList.get(1), power);
        assertEquals(userMainDataList.get(2), totalKillPoint);

    }

    @Test
    @DisplayName("킬 포인트 가져오기")
    void getKillPoint() throws Exception {
        //given
        String imgPath = "C:\\summaryBot\\20241006\\20241006_1_d1.png";
        int kill4T = 89213;
        int kill5T = 0;

        //when
        List<Integer> userDetailDataList = ocrManager.getUserDetailDataList(imgPath);
        //then
        assertEquals(kill4T, userDetailDataList.get(0));
        assertEquals(kill5T, userDetailDataList.get(1));
    }

    @Test
    @DisplayName("")
    void getTesseractDataPathTest() throws Exception {
        //given
        String imgPath = "C:\summaryBot\20240927\20240927_1_d.png";
        final int[] memberIdCoords = new int[]{700, 186, 840, 232};
        Mat cropImg = OpenCVUtil.cropSquare(imgPath, memberIdCoords);
        BufferedImage bufferedImage = OpenCVUtil.matToBufferedImage(cropImg);

        //when
        Tesseract tesseract = new Tesseract();

        System.out.println("TesseractDataPath = " + ocrConfig.getTesseractDataPath());
        tesseract.setDatapath(ocrConfig.getTesseractDataPath());
        tesseract.setLanguage("kor_lstm_best+eng_lstm_best");
        tesseract.setOcrEngineMode(2);
        try {
            String result = tesseract.doOCR(bufferedImage);
            System.out.println("인식한 데이터 " + result);
        } catch (TesseractException e) {
            System.out.println(e.getMessage());
        }
        //then

    }
}