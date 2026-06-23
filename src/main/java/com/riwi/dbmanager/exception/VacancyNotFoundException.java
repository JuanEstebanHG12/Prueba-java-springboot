package com.riwi.dbmanager.exception;

public class VacancyNotFoundException extends RuntimeException {
    public VacancyNotFoundException(String vacancyNotFound) {
        super(vacancyNotFound);
    }
}
