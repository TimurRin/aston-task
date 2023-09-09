package ru.moziev.job.aston.bank.utils;

public enum BankResponseCode {
    SUCCESS(),
    NO_ACCOUNT(),
    NO_SOURCE_ACCOUNT(),
    NO_TARGET_ACCOUNT(),
    INCORRECT_PIN(),
    EMPTY_AMOUNT(),
    NOT_ENOUGH_BALANCE();
}
