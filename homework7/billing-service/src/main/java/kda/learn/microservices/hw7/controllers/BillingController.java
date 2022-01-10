package kda.learn.microservices.hw7.controllers;

import kda.learn.microservices.hw7.dto.AccountReqDto;
import kda.learn.microservices.hw7.dto.AccountRespDto;
import kda.learn.microservices.hw7.dto.DebitReqDto;
import kda.learn.microservices.hw7.dto.DepositReqDto;
import kda.learn.microservices.hw7.services.BillingService;
import kda.learn.microservices.hw7.storage.entities.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/")
public class BillingController {
    private static final Logger log = LoggerFactory.getLogger(BillingController.class);
    private final BillingService service;

    public BillingController(BillingService service) {
        this.service = service;
    }

    @PostMapping(value = "/account")
    public ResponseEntity<AccountRespDto> createAccount(@RequestBody AccountReqDto accountReqDto) {
        log.info("CALL: createAccount");
        var accountId = service.createAccount(accountReqDto.getUserId());
        return new ResponseEntity<>(new AccountRespDto().accountId(accountId), HttpStatus.CREATED);
    }

    @PostMapping(value = "/deposit")
    public ResponseEntity<Void> depositAccount(@RequestBody DepositReqDto depositReqDto) {
        log.info("CALL: depositAccount");
        service.depositAccount(depositReqDto.getAccountId(), depositReqDto.getSum());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/debit")
    public ResponseEntity<Void> debitAccount(@RequestBody DebitReqDto debitReqDto) {
        log.info("CALL: debitAccount");
        service.debitAccount(debitReqDto.getAccountId(), debitReqDto.getSum());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/accounts")
    public ResponseEntity<List<Account>> getAccounts() {
        return ResponseEntity.ok(service.getAccounts());
    }
}
