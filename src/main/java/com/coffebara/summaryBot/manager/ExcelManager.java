package com.coffebara.summaryBot.manager;

import com.coffebara.summaryBot.config.ExcelConfig;
import com.coffebara.summaryBot.entity.Member;
import com.coffebara.summaryBot.exception.ExcelException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelManager {

    private final ExcelConfig excelConfig;

    // 엑셀 파일에서 name과 idCode를 찾아 데이터를 업데이트하거나 새로 추가
    public void handleMemberData(List<Member> memberList) {
        File excelFile = new File(excelConfig.getExcelPath());

        try {
            // 파일 잠금 여부를 확인하고, 파일이 잠겨 있다면 대기 후 재시도
            if (!waitForFileToBeAvailable(excelFile, 5, 2000)) {
                log.error("파일이 계속 사용 중이므로 엑셀 데이터 업데이트를 수행할 수 없습니다.");
                return; // 파일 잠금 해제가 실패했으므로 메서드 종료
            }

            try (FileInputStream fis = new FileInputStream(excelFile); Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet targetSheet;

                // 1. 시트가 하나인 경우
                if (workbook.getNumberOfSheets() == 1) {
                    // 첫 번째 시트에 name과 idCode를 입력
                    Sheet firstSheet = workbook.getSheetAt(0);
                    log.info("첫 번째 시트에 name과 idCode 입력.");
                    insertFirstMemberDataToSheet(firstSheet, memberList); // 첫 번째 시트에 name과 idCode 입력

                    // 'before'라는 이름의 시트를 생성
                    targetSheet = workbook.createSheet("before");
                    log.info("'before' 시트를 생성했습니다.");
                    createHeaderRow(targetSheet); // 헤더 생성
                    insertMemberDataToSheet(targetSheet, memberList); // 데이터 입력

                    // 2. 시트가 2개 이상인 경우
                } else if (workbook.getNumberOfSheets() >= 2) {
                    // 시트 개수 - 2를 사용하여 'after'라는 이름의 시트 생성
                    String sheetName = "after" + (workbook.getNumberOfSheets() - 1);
                    targetSheet = workbook.createSheet(sheetName);
                    log.info("'{}' 시트를 생성했습니다.", sheetName);
                    createHeaderRow(targetSheet); // 헤더 생성
                    insertMemberDataToSheet(targetSheet, memberList); // 데이터 입력
                }

                // 변경된 데이터를 엑셀 파일에 저장
                try (FileOutputStream fos = new FileOutputStream(excelFile)) {
                    workbook.write(fos);
                }

            } catch (IOException e) {
                throw new ExcelException("엑셀 데이터를 파일에 쓰는중 오류가 발생했습니다.", e);
            }

        } catch (InterruptedException e) {
            throw new ExcelException("파일 잠금 해제를 기다리는 동안 인터럽트가 발생했습니다.", e);
        }
    }

    // 첫 번째 시트에 name과 idCode를 입력하는 메서드
    private void insertFirstMemberDataToSheet(Sheet sheet, List<Member> memberList) {
        try {
            int rowNum = 3; // 데이터는 두 번째 행부터 시작
            for (Member member : memberList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(member.getName()); // Name 입력
                row.createCell(1).setCellValue(member.getIdCode()); // ID Code 입력
                row.createCell(2).setCellValue(member.getAlly()); // Ally 입력
            }
        } catch (Exception e) {
            throw new ExcelException("첫 번째 시트에 데이터를 입력하는 중 오류가 발생했습니다.", e);
        }
    }

    // 헤더 행을 만드는 메서드
    private void createHeaderRow(Sheet sheet) {
        try {
            Row headerRow = sheet.createRow(0); // 첫 번째 행 생성
            String[] headers = {"Name", "IdCode", "Ally", "Power", "Kill Points 4T", "Kill Points 5T", "Death"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]); // 헤더를 입력
            }
        } catch (Exception e) {
            throw new ExcelException("엑셀 헤더 행을 생성하는 중 오류가 발생했습니다.", e);
        }
    }

    // 새 행에 Member 데이터를 입력하는 메서드
    private void insertMemberDataToSheet(Sheet sheet, List<Member> memberList) {
        try {
            int rowNum = 1; // 데이터는 두 번째 행부터 시작
            for (Member member : memberList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(member.getName());
                row.createCell(1).setCellValue(member.getIdCode());
                row.createCell(2).setCellValue(member.getAlly());
                row.createCell(3).setCellValue(member.getPower());
                row.createCell(4).setCellValue(member.getKillPoint4T());
                row.createCell(5).setCellValue(member.getKillPoint5T());
                row.createCell(6).setCellValue(member.getDeath());
            }
        } catch (Exception e) {
            throw new ExcelException("엑셀 시트에 데이터를 입력하는 중 오류가 발생했습니다.", e);
        }

    }

    public boolean isFileLocked(File file) {
        // 파일이 쓰기 가능한지 체크
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            // 파일이 정상적으로 열리고 쓰기 가능하면 잠겨있지 않음
            return false;
        } catch (IOException e) {
            // IOException 발생 시 파일이 잠겨있음
            System.out.println("파일이 사용 중입니다: " + e.getMessage());
            return true;
        }
    }

    public boolean waitForFileToBeAvailable(File file, int retryCount, int waitTimeMs) throws InterruptedException {
        for (int i = 0; i < retryCount; i++) {
            if (!isFileLocked(file)) {
                return true;
            }
            System.out.println("파일이 사용 중입니다. " + waitTimeMs + "ms 후 다시 시도합니다.");
            Thread.sleep(waitTimeMs); // 지정된 시간 동안 대기
        }
        return false; // 재시도 횟수를 초과하면 false 반환
    }
}