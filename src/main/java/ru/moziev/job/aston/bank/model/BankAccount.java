package ru.moziev.job.aston.bank.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class BankAccount {
    @Id
    private String accountNumber;
    private String name;
    private double balance; // better use BigDecimal in production apps
    private String pin;
}