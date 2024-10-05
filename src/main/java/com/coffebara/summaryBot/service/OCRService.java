package com.coffebara.summaryBot.service;

import com.coffebara.summaryBot.entity.Member;
import com.coffebara.summaryBot.manager.ADBManager;
import com.coffebara.summaryBot.manager.OCRManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.coffebara.summaryBot.service.ADBService.memberList;

@Slf4j
@Service
@RequiredArgsConstructor
public class OCRService {

    private final OCRManager ocrManager;
    private final ADBManager adbManager;

    public void ExtractData() {


        for (Member member : memberList) {
            log.info(member.getRanking() + " 위 데이터 추출중...");

            String mainImg = member.getMainImg();
            String detailImg1 = member.getDetailImg1();
            String detailImg2 = member.getDetailImg2();
            String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String localDirectory = adbManager.getLocalDirectory();
            String mainImagePath = "C:" + localDirectory + timestamp + "/" + mainImg;
            String detailImagePath1 = "C:" + localDirectory + timestamp + "/" + detailImg1;
            String detailImagePath2 = "C:" + localDirectory + timestamp + "/" + detailImg2;

            int userIdCode = Integer.parseInt(ocrManager.getUserIdCode(mainImagePath));
            List<Object> userDataList = ocrManager.getUserMainDataList(mainImagePath);
            String ally = "";
            int power = 0;
            int totalKillPoint = 0;
            for (int i = 0; i < userDataList.size(); i++) {
                ally = userDataList.get(0) == null ? "" : (String) userDataList.get(0);
                power = (int) userDataList.get(1);
                totalKillPoint = (int) userDataList.get(2);
            }
            List<Integer> userDetailDataList = ocrManager.getUserDetailDataList(detailImagePath1);
            Integer killPoint4t = userDetailDataList.get(0);
            Integer killPoint5t = userDetailDataList.get(1);

            int death = ocrManager.getUserDeath(detailImagePath2);


            member.setMemberMainData(userIdCode, ally, power, death, totalKillPoint, killPoint4t, killPoint5t);
        }
    }


}