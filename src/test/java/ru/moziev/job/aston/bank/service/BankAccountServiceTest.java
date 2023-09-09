package ru.moziev.job.aston.bank.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.moziev.job.aston.bank.model.BankAccount;
import ru.moziev.job.aston.bank.utils.BankResponseCode;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class BankAccountServiceTest {
    @Autowired
    private BankAccountService service;

    private void createAccountA() {
        service.createAccount("Test Account A", "0012");
    }

    private void createAccountB() {
        service.createAccount("Test Account B", "5678");
    }

    private BankAccount getAccountByNumber(String accountNumber) {
        Optional<BankAccount> accountByNumberOptional = service.getAccountByNumber(accountNumber);

        assertTrue(accountByNumberOptional.isPresent());

        return accountByNumberOptional.get();
    }

    @Test
    @DirtiesContext
    public void createAccountTest() {
        createAccountA();

        List<BankAccount> bankAccountList = service.getAllAccounts();
        assertEquals(1, bankAccountList.size());

        BankAccount account = bankAccountList.get(0);

        assertEquals(account.getName(), "Test Account A");
        assertEquals(account.getPin(), "0012");
    }

    @Test
    @DirtiesContext
    public void createMultipleAccountsTest() {
        createAccountA();
        createAccountB();

        List<BankAccount> bankAccountList = service.getAllAccounts();
        assertEquals(2, bankAccountList.size());

        BankAccount accountA = bankAccountList.get(0);
        BankAccount accountB = bankAccountList.get(1);

        assertEquals("Test Account A", accountA.getName());
        assertEquals("0012", accountA.getPin());
        assertEquals("Test Account B", accountB.getName());
        assertEquals("5678", accountB.getPin());
    }

    @Test
    @DirtiesContext
    public void getAccountByNumberTest() {
        createAccountA();

        List<BankAccount> bankAccountList = service.getAllAccounts();
        assertEquals(1, bankAccountList.size());

        BankAccount accountFromList = bankAccountList.get(0);
        BankAccount accountByNumber = getAccountByNumber(accountFromList.getAccountNumber());

        assertEquals(accountFromList.getAccountNumber(), accountByNumber.getAccountNumber());
    }

    @Test
    @DirtiesContext
    public void depositTest() {
        createAccountA();

        List<BankAccount> bankAccountList = service.getAllAccounts();
        BankAccount account = bankAccountList.get(0);

        service.deposit(account.getAccountNumber(), 100.0, "0012");

        BankAccount accountByNumber = getAccountByNumber(account.getAccountNumber());
        assertEquals(100.0, accountByNumber.getBalance());
    }

    @Test
    @DirtiesContext
    public void depositAndWithdrawTest() {
        createAccountA();

        List<BankAccount> bankAccountList = service.getAllAccounts();
        BankAccount account = bankAccountList.get(0);

        assertEquals(BankResponseCode.SUCCESS, service.deposit(account.getAccountNumber(), 100.0, "0012"));

        assertEquals(BankResponseCode.NOT_ENOUGH_BALANCE, service.withdraw(account.getAccountNumber(), 300.0, "0012"));

        assertEquals(BankResponseCode.SUCCESS, service.withdraw(account.getAccountNumber(), 75.0, "0012"));

        assertEquals(BankResponseCode.INCORRECT_PIN, service.deposit(account.getAccountNumber(), 30.0, "8765"));

        assertEquals(BankResponseCode.INCORRECT_PIN, service.deposit(account.getAccountNumber(), 25.0, "8765"));

        BankAccount accountByNumber = getAccountByNumber(account.getAccountNumber());
        assertEquals(25.0, accountByNumber.getBalance());
    }

    @Test
    @DirtiesContext
    public void transferTest() {
        createAccountA();
        createAccountB();

        List<BankAccount> bankAccountList = service.getAllAccounts();
        assertEquals(2, bankAccountList.size());

        BankAccount accountA = bankAccountList.get(0);
        BankAccount accountB = bankAccountList.get(1);

        assertEquals(BankResponseCode.SUCCESS, service.deposit(accountA.getAccountNumber(), 3200.0, "0012"));

        assertEquals(BankResponseCode.INCORRECT_PIN, service.transfer(accountA.getAccountNumber(), accountB.getAccountNumber(), 2400.0, "0120"));

        assertEquals(BankResponseCode.SUCCESS, service.transfer(accountA.getAccountNumber(), accountB.getAccountNumber(), 2400.0, "0012"));

        assertEquals(BankResponseCode.NOT_ENOUGH_BALANCE, service.transfer(accountA.getAccountNumber(), accountB.getAccountNumber(), 2400.0, "0012"));

        assertEquals(BankResponseCode.INCORRECT_PIN, service.withdraw(accountB.getAccountNumber(), 2000.0, "5677"));

        assertEquals(BankResponseCode.SUCCESS, service.withdraw(accountB.getAccountNumber(), 2000.0, "5678"));

        assertEquals(BankResponseCode.NOT_ENOUGH_BALANCE, service.withdraw(accountB.getAccountNumber(), 2000.0, "5678"));


        BankAccount accountByNumberA = getAccountByNumber(accountA.getAccountNumber());
        assertEquals(800.0, accountByNumberA.getBalance());
        BankAccount accountByNumberB = getAccountByNumber(accountB.getAccountNumber());
        assertEquals(400.0, accountByNumberB.getBalance());
    }

    @Test
    @DirtiesContext
    public void badOperationsTest() {
        createAccountA();
        createAccountB();

        List<BankAccount> bankAccountList = service.getAllAccounts();
        assertEquals(2, bankAccountList.size());

        BankAccount accountA = bankAccountList.get(0);
        BankAccount accountB = bankAccountList.get(1);

        assertEquals(BankResponseCode.NO_ACCOUNT, service.deposit("O", 0, "0000"));
        assertEquals(BankResponseCode.INCORRECT_PIN, service.deposit(accountA.getAccountNumber(), 0, "0000"));
        assertEquals(BankResponseCode.EMPTY_AMOUNT, service.deposit(accountA.getAccountNumber(), 0, "0012"));
        assertEquals(BankResponseCode.SUCCESS, service.deposit(accountA.getAccountNumber(), 1, "0012"));

        BankAccount accountAfterDeposit = getAccountByNumber(accountA.getAccountNumber());
        assertEquals(1, accountAfterDeposit.getBalance());

        assertEquals(BankResponseCode.NO_ACCOUNT, service.withdraw("O", 0, "0000"));
        assertEquals(BankResponseCode.INCORRECT_PIN, service.withdraw(accountA.getAccountNumber(), 0, "0000"));
        assertEquals(BankResponseCode.EMPTY_AMOUNT, service.withdraw(accountA.getAccountNumber(), 0, "0012"));
        assertEquals(BankResponseCode.SUCCESS, service.withdraw(accountA.getAccountNumber(), 0.01, "0012"));

        BankAccount accountAfterWithdraw = getAccountByNumber(accountA.getAccountNumber());
        assertEquals(0.99, accountAfterWithdraw.getBalance());

        assertEquals(BankResponseCode.NO_SOURCE_ACCOUNT, service.transfer("O", "0", 0, "0000"));
        assertEquals(BankResponseCode.INCORRECT_PIN, service.transfer(accountA.getAccountNumber(), "0", 0, "0000"));
        assertEquals(BankResponseCode.EMPTY_AMOUNT, service.transfer(accountA.getAccountNumber(), "0", 0, "0012"));
        assertEquals(BankResponseCode.NO_TARGET_ACCOUNT, service.transfer(accountA.getAccountNumber(), "0", 0.49, "0012"));
        assertEquals(BankResponseCode.SUCCESS, service.transfer(accountA.getAccountNumber(), accountB.getAccountNumber(), 0.49, "0012"));

        BankAccount accountAAfterTransfer = getAccountByNumber(accountA.getAccountNumber());
        assertEquals(0.50, accountAAfterTransfer.getBalance());
        BankAccount accountBAfterTransfer = getAccountByNumber(accountB.getAccountNumber());
        assertEquals(0.49, accountBAfterTransfer.getBalance());
    }
}
