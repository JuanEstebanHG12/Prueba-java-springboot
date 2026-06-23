package com.riwi.dbmanager.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userNotFound) {
        super(userNotFound);
    }
}
