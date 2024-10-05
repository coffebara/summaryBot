package com.coffebara.summaryBot.exception;

/**
 * 디바이스가 꺼져있을 때 생기는 오류
 */
public class DeviceException extends ADBException{

    public DeviceException(String message) {
        super(message);
    }
}
