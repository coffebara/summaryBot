package com.coffebara.summaryBot.utils;

import com.coffebara.summaryBot.SummaryBotRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ClipboardUtilTest {

    @MockBean
    private SummaryBotRunner summaryBotRunner;  // SummaryBotRunner 빈을 Mock으로 대체

    @Test
    @DisplayName("클립보드 접근 테스트")
    void getClipboardTest() throws Exception {
        //given
        String text = "ᴰᴹ Jake";

        //when
        String clipboardText = ClipboardUtil.getClipboard();

        //then
        assertEquals(text, clipboardText);

    }
}

