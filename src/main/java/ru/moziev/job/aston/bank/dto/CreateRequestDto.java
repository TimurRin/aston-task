package ru.moziev.job.aston.bank.dto;

import lombok.Data;

@Data
public class CreateRequestDto {
    private String name;
    private String pin;
}
