package ru.moziev.job.aston.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.moziev.job.aston.bank.dto.*;
import ru.moziev.job.aston.bank.service.BankAccountService;
import ru.moziev.job.aston.bank.utils.BankResponseCode;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    private final BankAccountService accountService;

    public BankAccountController(BankAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody CreateRequestDto dto) {
        String accountNumber = accountService.createAccount(dto.getName(), dto.getPin());

        if (accountNumber != null) {
            return new ResponseEntity<>("Account '" + accountNumber + "' has been created", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to create account", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody DepositRequestDto dto) {
        BankResponseCode code = accountService.deposit(dto.getAccountNumber(), dto.getAmount(), dto.getPin());

        return new ResponseEntity<>(code.toString(), HttpStatus.OK);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody WithdrawRequestDto dto) {
        BankResponseCode code = accountService.withdraw(dto.getAccountNumber(), dto.getAmount(), dto.getPin());

        return new ResponseEntity<>(code.toString(), HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequestDto dto) {
        BankResponseCode code = accountService.transfer(
                dto.getSourceAccountNumber(),
                dto.getTargetAccountNumber(),
                dto.getAmount(),
                dto.getPin()
        );

        return new ResponseEntity<>(code.toString(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BankAccountResponseDto>> getAllAccounts() {
        return new ResponseEntity<>(accountService.getAllAccountsAsDto(), HttpStatus.OK);
    }
}