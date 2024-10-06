package com.coffebara.summaryBot.exception;

public class FileAndDirectoryException extends RuntimeException {

    public FileAndDirectoryException(String message) {
        super(message);
    }
    public FileAndDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
