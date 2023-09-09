package ru.moziev.job.aston.bank.dto;

import lombok.Data;

@Data
public class TransferRequestDto {
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private double amount;
    private String pin;
}