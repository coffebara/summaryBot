package com.coffebara.summaryBot.service;

import com.coffebara.summaryBot.manager.ExcelManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.coffebara.summaryBot.service.ADBService.memberList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {

    private final ExcelManager excelManager;

    public void saveMembersToExcel() {
        excelManager.handleMemberData(memberList);
    }
}
