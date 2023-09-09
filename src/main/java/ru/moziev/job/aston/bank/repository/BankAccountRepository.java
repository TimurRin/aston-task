package ru.moziev.job.aston.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moziev.job.aston.bank.model.BankAccount;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByAccountNumber(String accountNumber);
}