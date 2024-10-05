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
    @DisplayName("데스 가져오기")
    void getDeath() throws Exception {
        //given
        String imgPath = "C:\\summaryBot\\Screenshots\\death.png";
        int death = 479_093; //실제 데이터

        //when
        int deathOCR = ocrManager.getUserDeath(imgPath);

        //then
        assertEquals(deathOCR, death);

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