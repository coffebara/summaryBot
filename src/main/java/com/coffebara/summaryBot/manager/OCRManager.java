package com.coffebara.summaryBot.manager;

import com.coffebara.summaryBot.config.OCRConfig;
import com.coffebara.summaryBot.utils.OpenCVUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class OCRManager {

    private final OCRConfig ocrConfig;

    public int getUserDeath(String imagePath) {
        final int[] deathCoords = new int[]{1148, 436, 1338, 484}; //이미지속 가져올 좌표
        Mat cropImg = OpenCVUtil.cropSquare(imagePath, deathCoords);
        Mat zoomImage = OpenCVUtil.zoomImage(cropImg);
        BufferedImage bufferedImage = OpenCVUtil.matToBufferedImage(zoomImage);
        String data = readData(bufferedImage).trim();

        return Integer.parseInt(data.replace(",", ""));
    }

    public String getUserIdCode(String imagePath) {
        final int[] memberIdCoords = new int[]{700, 186, 840, 232}; // 이미지속 가져올 좌표
        Mat cropImg = OpenCVUtil.cropSquare(imagePath, memberIdCoords);
        Mat zoomImage = OpenCVUtil.zoomImage(cropImg);
//        Imgcodecs.imwrite("C:\\summaryBot\\20240922\\asdsa.png", zoomImage);
        BufferedImage bufferedImage = OpenCVUtil.matToBufferedImage(zoomImage);
        String data = readData(bufferedImage).trim();

        return data.substring(0, data.length() - 2); // ")" 제거
    }

    public List<Object> getUserMainDataList(String imagePath) {
        final int[] allyCoords = new int[]{594, 328, 689, 370}; // 이미지속 가져올 좌표
        final int[] powerCoords = new int[]{877, 327, 1093, 365}; // 이미지속 가져올 좌표
        final int[] killPointCoords = new int[]{1110, 330, 1294, 363}; // 이미지속 가져올 좌표

        List<Object> list = new ArrayList<>();

        Mat cropAllyImg = OpenCVUtil.cropSquare(imagePath, allyCoords);
        Mat cropPowerImg = OpenCVUtil.cropSquare(imagePath, powerCoords);
        Mat cropKillPointImg = OpenCVUtil.cropSquare(imagePath, killPointCoords);

        Mat zoomAllyImg = OpenCVUtil.zoomImage(cropAllyImg);
        Mat zoomPowerImg = OpenCVUtil.zoomImage(cropPowerImg);
        Mat zoomKillPointImg = OpenCVUtil.zoomImage(cropKillPointImg);

//        Imgcodecs.imwrite("C:\\summaryBot\\20240922\\p.png", zoomPowerImg);
//        Imgcodecs.imwrite("C:\\summaryBot\\20240922\\k.png", zoomKillPointImg);

        BufferedImage bufferedAllyImg = OpenCVUtil.matToBufferedImage(zoomAllyImg);
        BufferedImage bufferedPowerImg = OpenCVUtil.matToBufferedImage(zoomPowerImg);
        BufferedImage bufferedKillPointImg = OpenCVUtil.matToBufferedImage(zoomKillPointImg);

        String allyData = readData(bufferedAllyImg).trim();
        String powerData = readData(bufferedPowerImg).trim();
        String killPointData = readData(bufferedKillPointImg).trim();

        String allyTrimmed = allyData.trim();
        int lastIndex = allyTrimmed.indexOf(']');
        String ally = allyTrimmed.substring(1, lastIndex - 1);
        list.add(ally);

        list.add(Integer.parseInt(powerData.trim().replace(",", "")));
        if (killPointData.isEmpty()) {
            list.add(0);
        } else {
            list.add(Integer.parseInt(killPointData.trim().replace(",", "")));
        }

        return list;
    }

    public List<Integer> getUserDetailDataList(String imagePath) {
        final int[] memberIdCoords = new int[]{860, 551, 983, 640}; // 이미지속 가져올 좌표
        List<Integer> list = new ArrayList<>();

        Mat cropImg = OpenCVUtil.cropSquare(imagePath, memberIdCoords);
        Mat zoomImage = OpenCVUtil.zoomImage(cropImg);

//        Imgcodecs.imwrite("C:\\summaryBot\\20240922\\dddd.png", zoomImage);

        BufferedImage bufferedImage = OpenCVUtil.matToBufferedImage(zoomImage);
        String data = readData(bufferedImage).trim();
        System.out.println("data = " + data);
        String[] split = data.split("\\n");
        for (String s : split) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) {
                list.add(Integer.valueOf(trimmed.replace(",", "")));
            }
        }

        return list;
    }


    // 이미지에서 게임 유저Id를 가져온다.
    public String readData(BufferedImage bufferedImage) {
        // Tesseract 인스턴스 생성
        Tesseract tesseract = new Tesseract();

        // Tesseract 데이터 경로 설정
        tesseract.setDatapath(ocrConfig.getTesseractDataPath());
        tesseract.setLanguage("kor_lstm_best+eng_lstm_best");
        tesseract.setPageSegMode(4);
        tesseract.setOcrEngineMode(1);

        try {
            String result = tesseract.doOCR(bufferedImage);
            log.info("인식한 데이터 " + result);

            return result;
        } catch (TesseractException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

}
