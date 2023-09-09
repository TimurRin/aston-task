package ru.moziev.job.aston.bank.service;

import org.springframework.stereotype.Service;
import ru.moziev.job.aston.bank.dto.BankAccountResponseDto;
import ru.moziev.job.aston.bank.model.BankAccount;
import ru.moziev.job.aston.bank.repository.BankAccountRepository;
import ru.moziev.job.aston.bank.utils.BankResponseCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SplittableRandom;
import java.util.stream.Collectors;

@Service
public class BankAccountService {

    private final BankAccountRepository accountRepository;

    public BankAccountService(BankAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public String createAccount(String name, String pin) {
        String accountNumber = generateAccountNumber();

        BankAccount account = new BankAccount();
        account.setAccountNumber(accountNumber);
        account.setName(name);
        account.setPin(pin);
        account.setBalance(0.0);

        accountRepository.save(account);

        return accountNumber;
    }

    private BankResponseCode checkForPinAndAmount(BankAccount account, double amount, String pin) {
        if (!account.getPin().equals(pin)) {
            return BankResponseCode.INCORRECT_PIN;
        }

        if (amount <= 0) {
            return BankResponseCode.EMPTY_AMOUNT;
        }
        return null;
    }

    public BankResponseCode deposit(String accountNumber, double amount, String pin) {
        Optional<BankAccount> optionalAccount = accountRepository.findByAccountNumber(accountNumber);

        if (!optionalAccount.isPresent()) {
            return BankResponseCode.NO_ACCOUNT;
        }

        BankAccount account = optionalAccount.get();

        BankResponseCode code = checkForPinAndAmount(account, amount, pin);
        if (code != null) {
            return code;
        }

        double newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);
        accountRepository.save(account);

        return BankResponseCode.SUCCESS;
    }

    public BankResponseCode withdraw(String accountNumber, double amount, String pin) {
        Optional<BankAccount> optionalAccount = accountRepository.findByAccountNumber(accountNumber);

        if (!optionalAccount.isPresent()) {
            return BankResponseCode.NO_ACCOUNT;
        }

        BankAccount account = optionalAccount.get();

        BankResponseCode code = checkForPinAndAmount(account, amount, pin);
        if (code != null) {
            return code;
        }

        if (account.getBalance() < amount) {
            return BankResponseCode.NOT_ENOUGH_BALANCE;
        }

        double newBalance = account.getBalance() - amount;
        account.setBalance(newBalance);
        accountRepository.save(account);

        return BankResponseCode.SUCCESS;
    }

    public BankResponseCode transfer(String sourceAccountNumber, String targetAccountNumber, double amount, String pin) {
        Optional<BankAccount> optionalSourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber);

        if (!optionalSourceAccount.isPresent()) {
            return BankResponseCode.NO_SOURCE_ACCOUNT;
        }

        BankAccount sourceAccount = optionalSourceAccount.get();

        BankResponseCode code = checkForPinAndAmount(sourceAccount, amount, pin);
        if (code != null) {
            return code;
        }

        if (sourceAccount.getBalance() < amount) {
            return BankResponseCode.NOT_ENOUGH_BALANCE;
        }

        Optional<BankAccount> optionalTargetAccount = accountRepository.findByAccountNumber(targetAccountNumber);

        if (!optionalTargetAccount.isPresent()) {
            return BankResponseCode.NO_TARGET_ACCOUNT;
        }

        BankAccount targetAccount = optionalTargetAccount.get();

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        targetAccount.setBalance(targetAccount.getBalance() + amount);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        return BankResponseCode.SUCCESS;
    }

    public Optional<BankAccount> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public List<BankAccount> getAllAccounts() {
        return new ArrayList<>(accountRepository.findAll());
    }

    public List<BankAccountResponseDto> getAllAccountsAsDto() {
        List<BankAccount> accounts = accountRepository.findAll();

        return accounts.stream()
                .map(account -> {
                    BankAccountResponseDto dto = new BankAccountResponseDto();
                    dto.setName(account.getName());
                    dto.setAmount(account.getBalance());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private String generateAccountNumber() {
        return String.format("%012d", new SplittableRandom().nextLong(0, 1000000000000L));
    }
}