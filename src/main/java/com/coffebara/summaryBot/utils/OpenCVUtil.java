package com.coffebara.summaryBot.utils;

import com.coffebara.summaryBot.exception.OpenCVException;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;

@Slf4j
public class OpenCVUtil {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static Mat binaryImage(Mat image) {
        Mat binary = new Mat();
        try {
            Imgproc.threshold(image, binary, 150, 255, Imgproc.THRESH_BINARY);
        } catch (Exception e) {
            throw new OpenCVException("이미지 처리 중 오류가 발생했습니다.", e);
        }

        return binary;
    }


    public static Mat cropSquare(String imagePath, int[] coords) {
        Mat image = Imgcodecs.imread(imagePath);
        int x = coords[0];
        int y = coords[1];
        int w = coords[2] - x;
        int h = coords[3] - y;

        if (x + w > image.cols() || y + h > image.rows()) {
            log.info("ROI가 이미지 범위를 초과합니다.");
            throw new OpenCVException("ROI가 이미지 범위를 초과했습니다.");
        }

        Rect roi = new Rect(x, y, w, h);
        return new Mat(image, roi);
    }

    public static Mat zoomImage(Mat image) {
        Mat zoomedImage = new Mat();
        try {
            Imgproc.resize(image, zoomedImage, new Size(image.cols() * 1.5, image.rows() * 1.5));
        } catch (Exception e) {
            throw new OpenCVException("이미지 처리 중 오류가 발생했습니다.", e);
        }

        return zoomedImage;
    }

    public static BufferedImage matToBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] bytes = new byte[bufferSize];
        matrix.get(0, 0, bytes); // get all the pixels
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        image.getRaster().setDataElements(0, 0, matrix.cols(), matrix.rows(), bytes);
        return image;
    }
}

//    public static void main(String[] args) {
//        //OpenCV 라이브러리 로드
//
//        // 이미지 파일 경로
//        String imagePath = "C:\\summaryBot\\20240922\\20240922_190038.png";
//
//        // 이미지 읽기
//        Mat image = Imgcodecs.imread(imagePath);
//        if (image.empty()) {
//            log.info("Image not found!");
//            return;
//        }
//
//        // 여백 자르기1 (테두리)
//        int outSideX = 176; // 시작 x 좌표
//        int outSideY = 242; // 시작 y 좌표
//        int outSideW = 1434 - outSideX; // 잘라낼 너비
//        int outSideH = 864 - outSideY; // 잘라낼 높이
//        // ROI가 이미지 범위를 초과하지 않는지 확인
//        if (outSideX + outSideW > image.cols() || outSideY + outSideH > image.rows()) {
//            System.out.println("ROI가 이미지 범위를 초과합니다.");
//            return;
//        }
//        Rect roi = new Rect(outSideX, outSideY, outSideW, outSideH);
//        Mat croppedImage = new Mat(image, roi);
//
//        // 잘라낸 이미지 1.5배 확대
//        Mat zoomedImage = new Mat();
//        Imgproc.resize(croppedImage, zoomedImage, new Size(croppedImage.cols() * 1.5, croppedImage.rows() * 1.5));
//
//        // 여백 자르기 2 (문자 사이를 가르고 양옆을 합침)
//        int middleX = 104;
//        int middleY = 8;
//        int middleW = 230 - middleX;
//        int middleH = 916 - middleY;
//        // 잘라낼 부분 생성
//        Rect cropRect = new Rect(middleX, middleY, middleW, middleH);
//        Mat croppedImage2 = new Mat(zoomedImage, cropRect);
//        // 나머지 이미지 부분 생성
//        Mat leftImage = new Mat(zoomedImage, new Rect(0, 0, middleX, zoomedImage.rows())); // 왼쪽 이미지
//        Mat rightImage = new Mat(zoomedImage, new Rect(middleX + middleW, 0, zoomedImage.cols() - (middleX + middleW), zoomedImage.rows())); // 오른쪽 이미지
//        // 양쪽 이미지 합치기
//        Mat mergedImage = new Mat();
//        Core.hconcat(Arrays.asList(leftImage, rightImage), mergedImage);
//
//
//        // 이미지를 HSV 색상 공간으로 변환
//        Mat hsvImage = new Mat();
//        Imgproc.cvtColor(mergedImage, hsvImage, Imgproc.COLOR_BGR2HSV);
//        // 흰색의 범위 설정 (HSV)
//        Scalar lowerWhite = new Scalar(0, 0, 200);  // 흰색의 하한값
//        Scalar upperWhite = new Scalar(180, 50, 255);  // 흰색의 상한값
//        // 노란색의 범위 설정 (HSV)
//        Scalar lowerYellow = new Scalar(20, 100, 100);  // 노란색의 하한값
//        Scalar upperYellow = new Scalar(30, 255, 255);  // 노란색의 상한값
//        // 흰색 마스크 생성
//        Mat whiteMask = new Mat();
//        Core.inRange(hsvImage, lowerWhite, upperWhite, whiteMask);
//        // 노란색 마스크 생성
//        Mat yellowMask = new Mat();
//        Core.inRange(hsvImage, lowerYellow, upperYellow, yellowMask);
//        // 두 마스크를 합침 (흰색과 노란색만 남기기)
//        Mat mask = new Mat();
//        Core.bitwise_or(whiteMask, yellowMask, mask);
//        // 원본 이미지에서 마스크 적용 (흰색과 노란색 글자만 남김)
//        Mat resultImg = new Mat();
//        Core.bitwise_and(mergedImage, mergedImage, resultImg, mask);
//
////        // 가우시안 블러 적용
////        Mat blurredImage  = new Mat();
////        Imgproc.GaussianBlur(resultImg, blurredImage, new Size(5, 5), 0);
////
//        // 이미지 윤곽선 강조
//        Mat edges = new Mat();
//        Imgproc.Canny(resultImg, edges, 100, 200);
//        // 윤곽선을 3채널로 변환
//        Mat channelsEdges = new Mat();
//        Imgproc.cvtColor(edges, channelsEdges, Imgproc.COLOR_GRAY2BGR);
//        // 윤곽선을 원본 이미지에 덧씌우기
//        // 윤곽선 이미지를 원본 이미지와 같은 크기로 조정
//        Mat resizedEdges = new Mat();
//        Imgproc.resize(channelsEdges, resizedEdges, new Size(resultImg.cols(), resultImg.rows()));
//        // 윤곽선을 원본 이미지에 덧씌우기
//        Mat thickEdges = new Mat();
//        Core.addWeighted(resizedEdges, 0.8, resizedEdges, 1.0, 0, thickEdges);
////
////        // 여백 추가
////        int borderSize = 10;
////        Mat finalImage = new Mat();
////        Core.copyMakeBorder(thickEdges, finalImage, borderSize, borderSize, borderSize, borderSize, Core.BORDER_CONSTANT, new Scalar(255, 255, 255));
////
////        // 전처리: 이진화 (Thresholding)
////        Mat binary = new Mat();
////        Imgproc.threshold(resultImg, binary, 150, 255, Imgproc.THRESH_BINARY);
//
////        // Mat을 BufferedImage로 변환
//        BufferedImage bufferedImage = matToBufferedImage(thickEdges);
//
//        // Tesseract
//        Tesseract tesseract = new Tesseract();
//        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata/");
//        tesseract.setLanguage("kor_lstm_best+eng_lstm_best");
//        tesseract.setPageSegMode(4);
//        tesseract.setOcrEngineMode(1);
//
//        // 결과 이미지 저장 (원하는 경로에 저장)
//        Imgcodecs.imwrite("C:\\summaryBot\\20240922\\cut.png", thickEdges);
//
//        try {
//            String result = tesseract.doOCR(bufferedImage);
//            System.out.println("인식된 텍스트: " + result);
//        } catch (TesseractException e) {
//            e.printStackTrace();
//        }
//    }
