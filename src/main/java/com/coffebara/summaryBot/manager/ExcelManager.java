package com.coffebara.summaryBot.manager;

import com.coffebara.summaryBot.config.ExcelConfig;
import com.coffebara.summaryBot.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
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
    public void updateOrInsertMemberData(List<Member> memberList) {
        File excelFile = new File(excelConfig.getExcelPath());

        try {
            // 파일 잠금 여부를 확인하고, 파일이 잠겨 있다면 대기 후 재시도
            if (!waitForFileToBeAvailable(excelFile, 5, 2000)) {
                log.error("파일이 계속 사용 중이므로 엑셀 데이터 업데이트를 수행할 수 없습니다.");
                return; // 파일 잠금 해제가 실패했으므로 메서드 종료
            }

            try (FileInputStream fis = new FileInputStream(excelFile);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 선택

                for (Member member : memberList) {
                    boolean isUpdated = false; // 멤버별로 isUpdated 상태 관리

                    // 첫 번째 행부터 name과 idCode가 있는지 확인
                    for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                        Row row = sheet.getRow(rowIndex);
                        if (row != null) {
                            Cell nameCell = row.getCell(0); // Name 열 (0번 열)
                            Cell idCodeCell = row.getCell(1); // ID Code 열 (1번 열)

                            // Name과 ID가 모두 일치하는 경우 데이터 업데이트
                            if (nameCell != null && idCodeCell != null && nameCell.getStringCellValue().equals(member.getName()) && (int) idCodeCell.getNumericCellValue() == member.getIdCode()) {

                                updateMemberData(row, member);
                                isUpdated = true; // 업데이트 되었으므로 새로 추가하지 않음
                                break; // 업데이트 후 루프 탈출
                            }
                        }
                    }

                    // 일치하는 데이터가 없으면 새 행 추가
                    if (!isUpdated) {
                        int newRowNum = sheet.getLastRowNum() + 1;
                        Row newRow = sheet.createRow(newRowNum);
                        insertNewMemberData(newRow, member);
                        log.info("새로운 행에 데이터를 추가했습니다. (행 번호: {})", newRowNum);
                    }
                }

                // 변경된 데이터를 엑셀 파일에 저장
                try (FileOutputStream fos = new FileOutputStream(excelFile)) {
                    workbook.write(fos);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (InterruptedException e) {
            log.error("파일 잠금 해제를 기다리는 동안 인터럽트가 발생했습니다.", e);
        }
    }

    // 기존 행에 Member 데이터를 업데이트하는 메서드
    private void updateMemberData(Row row, Member member) {
        int currentColumn = 13; //시작 인덱스
        boolean isDataInserted = false;

        // 13열부터 데이터가 있는지 확인하고, 없으면 데이터를 추가
        while (!isDataInserted) {
            Cell targetCell = row.getCell(currentColumn);

            if (targetCell == null || targetCell.getCellType() == CellType.BLANK) {
                // 13열 혹은 다음 4열에서 빈 셀을 찾아 데이터를 추가
                row.createCell(currentColumn).setCellValue(member.getKillPoint4T()); // Kill Point 4T (6번 열)
                row.createCell(currentColumn + 1).setCellValue(member.getKillPoint5T()); // Kill Point 5T (7번 열)
                row.createCell(currentColumn + 2).setCellValue(member.getDeath()); // death Point

                log.info("{}열에 데이터 추가됨", currentColumn + 1);
                isDataInserted = true; // 데이터 추가 완료
            } else {
                // 해당 열에 데이터가 이미 있는 경우 4열 뒤로 이동
                currentColumn += 4;
            }
        }
    }

    // 새 행에 Member 데이터를 입력하는 메서드
    private void insertNewMemberData(Row row, Member member) {
        row.createCell(0).setCellValue(member.getName()); // Name 열 (1번 열)
        row.createCell(1).setCellValue(member.getIdCode()); // ID Code 열 (0번 열)
        row.createCell(2).setCellValue(member.getAlly()); // Ally 열 (3번 열)
        row.createCell(3).setCellValue(member.getPower()); // Power 열 (4번 열)
        row.createCell(4).setCellValue(member.getKillPoint4T()); // Kill Point 4T (6번 열)
        row.createCell(5).setCellValue(member.getKillPoint5T()); // Kill Point 5T (7번 열)
        row.createCell(6).setCellValue(member.getDeath()); // Kill death  (7번 열)
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