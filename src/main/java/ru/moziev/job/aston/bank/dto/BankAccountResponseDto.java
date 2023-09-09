package ru.moziev.job.aston.bank.dto;

import lombok.Data;

@Data
public class BankAccountResponseDto {
    private String name;
    private double amount;
}