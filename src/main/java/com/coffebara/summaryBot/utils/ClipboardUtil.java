package com.coffebara.summaryBot.utils;

import com.coffebara.summaryBot.exception.ClipboardException;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

@Slf4j
public class ClipboardUtil {

    /**
     * 디바이스에서 copy한 userId를 가져오는 메서드
     *
     * @return clipboardText
     */
    public static String getClipboard() {
        // Headless 모드인지 확인
        if (GraphicsEnvironment.isHeadless()) {
            log.error("클립보드 접근이 불가능한 환경입니다.");
            throw new ClipboardException("클립보드 접근이 불가능한 환경입니다.");
//            return ""; // 빈 문자열 반환
        }

        // 클립보드 객체 가져오기
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 클립보드에서 데이터 가져오기
        Transferable contents = clipboard.getContents(null);
        String clipboardText = "";

        try {
            // 텍스트 데이터가 있는지 확인
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                clipboardText = (String) contents.getTransferData(DataFlavor.stringFlavor);
                log.info("클립보드 내용: " + clipboardText);
                return clipboardText;
            } else {
                log.error("클립보드에 텍스트가 없습니다.");
            }
        } catch (Exception e) {
            throw new ClipboardException("클립보드에 접근할 수 없습니다.");
        }

        return clipboardText;
    }
}
