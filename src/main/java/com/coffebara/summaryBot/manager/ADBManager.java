package com.coffebara.summaryBot.manager;

import com.coffebara.summaryBot.config.ADBConfig;
import com.coffebara.summaryBot.exception.DeviceException;
import com.coffebara.summaryBot.utils.MyFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class ADBManager {

    private static final String[] SWIPE_COORDINATE = {"300", "800", "300", "549"}; // startX, startY, endX, endY
    private final String targetPackage;
    private final String localDirectory;
    private final String deviceDirectory;
    private String deviceId;

    public ADBManager(ADBConfig adbConfig) throws IOException {
        this.targetPackage = adbConfig.getTargetPackage();
        this.localDirectory = adbConfig.getLocalDirectory();
        this.deviceDirectory = adbConfig.getDeviceDirectory();
        this.deviceId = getDeviceId(); // deviceId를 초기화
    }

    public String getLocalDirectory() {
        return localDirectory;
    }

    private void deleteFileOnDevice(String trimmedFilePath) throws IOException, InterruptedException {
        ProcessBuilder deleteBuilder = new ProcessBuilder("adb", "shell", "rm", trimmedFilePath);
        Process deleteProcess = deleteBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(deleteProcess.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            log.info(line);
        }

        deleteProcess.waitFor();
    }

    public String takeScreen(String ranking) throws IOException {
        //현재 시간 기준 파일명 생성
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName = deviceDirectory + timestamp + "_" + ranking + ".png";
        //화면 캡쳐후 저장
        ProcessBuilder processBuilder = new ProcessBuilder("adb", "shell", "screencap", "-p", fileName);
        Process process = processBuilder.start();

        log.info(fileName + " 캡쳐");

        // 명령어 결과 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        return timestamp + "_" + ranking + ".png";
    }

    public void pullScreenshotsAndDelete() throws IOException, InterruptedException {
        //현재 날짜 기준 로컬 디렉터리명 생성
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String localTimestampDirectory = localDirectory + timestamp;
        MyFileUtil.createDirectoryIfNotExists(localTimestampDirectory);

        //ADB 쉘에서 PNG 파일 목록 가져오기
        ProcessBuilder listBuilder = new ProcessBuilder("adb", "shell", "ls", deviceDirectory + "*.png");
        Process listProcess = listBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(listProcess.getInputStream()));
        String filePath;

        // 파일 목록을 읽어 각 파일을 로컬로 복사
        while ((filePath = reader.readLine()) != null) {
            String trimmedFilePath = filePath.trim();
            // 각 PNG 파일을 로컬로 복사
            ProcessBuilder pullBuilder = new ProcessBuilder("adb", "pull", trimmedFilePath, localTimestampDirectory);
            Process pullProcess = pullBuilder.start();

            // 명령어 결과 읽기
            BufferedReader pullReader = new BufferedReader(new InputStreamReader(pullProcess.getInputStream()));
            String line;
            while ((line = pullReader.readLine()) != null) {
                log.info(line); // 로그로 출력
            }
            try {
                pullProcess.waitFor(); // 복사 완료 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //파일 삭제
            deleteFileOnDevice(trimmedFilePath);
        }
    }


    private void swipeScreen() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("adb", "-s", deviceId, "shell", "input", "swipe", SWIPE_COORDINATE[0], SWIPE_COORDINATE[1], SWIPE_COORDINATE[2], SWIPE_COORDINATE[3]);
        Process process = processBuilder.start();

        // 명령어 결과 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        Thread.sleep(1000);
    }

    public void tabScreen(int x, int y) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("adb", "-s", deviceId, "shell", "input", "touchscreen", "tap", String.valueOf(x), String.valueOf(y));
        Process process = processBuilder.start();

        // 명령어 결과 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        Thread.sleep(220);
    }

    public void startApp() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("adb", "shell", "monkey", "-p", targetPackage, "1");
        Process process = processBuilder.start();

        // 명령어 결과 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        log.info("패키지 실행");
        Thread.sleep(20_000);

        //프로세스 종료 대기
        process.waitFor();
    }

    public void oldStartApp(String packageAndActivity) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("adb", "-s", deviceId, "shell", "am", "start", "-n", packageAndActivity);
        Process process = processBuilder.start();
        log.info("실행");

        // 명령어 결과 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        log.info("실행");
        Thread.sleep(20_000);

        //프로세스 종료 대기
        process.waitFor();
    }

    public boolean checkRunningNow() throws IOException {
        boolean isRunningNow = false;

        // 현재 실행중인 프로세스 확인
        ProcessBuilder processBuilder = new ProcessBuilder("adb", "-s", deviceId, "shell", "ps");
        Process process = processBuilder.start();

        // 명령어 결과 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(targetPackage)) {
                isRunningNow = true;
                log.info(targetPackage + "가 현재 실행중...");
                break;
            }
        }

        return isRunningNow;
    }

    public String getPackageAndActivity() throws IOException {
        String packageName = "";
        String activityName = "";

        // 디바이스에 설치된 패키지 조회
        ProcessBuilder processBuilder = new ProcessBuilder("adb", "-s", deviceId, "shell", "pm", "list", "package");
        Process process = processBuilder.start();

        // 명령어 결과 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(targetPackage)) {
                // 패키지명만 추출
                packageName = line.replace("package:", "").trim();
                log.info("packageName = " + packageName);
                break;
            }
        }

        // 액티비티명 가져오기
        if (!packageName.isEmpty()) {
            // dumpsys 명령어
            processBuilder = new ProcessBuilder("adb", "-s", deviceId, "shell", "dumpsys", "package", packageName);
            process = processBuilder.start();

            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = reader.readLine()) != null) {
                if (line.contains("MainActivity")) {
                    int lastSlashIndex = line.lastIndexOf('/');
                    int filterIndex = line.indexOf("filter");
                    activityName = line.substring(lastSlashIndex + 1, filterIndex).trim(); //액티비티명만 추출
                    log.info("activityName = " + activityName);
                    break;
                }
            }
        }

        return packageName + "/" + activityName;
    }


    private String getDeviceId() throws IOException {
        // adb에 등록된 디바이스 조회
        ProcessBuilder processBuilder = new ProcessBuilder("adb", "devices");
        Process process = processBuilder.start();

        // 명령어 결과 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        String deviceId = "";
        while ((line = reader.readLine()) != null) {
            if (line.endsWith("device")) { //현재 실행중인 디바이스 가져오기
                deviceId = line.split("\t")[0];
                break;
            }
        }

        //실행중인 디바이스가 없다면
        if (deviceId.isEmpty()) {
            throw new DeviceException("디바이스가 꺼져있거나 연결되지 않았습니다.");
        }
        log.info("deviceId = " + deviceId);

        return deviceId;
    }
}
