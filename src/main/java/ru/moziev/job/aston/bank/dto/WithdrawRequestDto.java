package ru.moziev.job.aston.bank.dto;

import lombok.Data;

@Data
public class WithdrawRequestDto {
    private String accountNumber;
    private double amount;
    private String pin;
}