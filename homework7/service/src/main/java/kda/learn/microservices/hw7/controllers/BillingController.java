package kda.learn.microservices.hw7.controllers;

import kda.learn.microservices.hw7.dto.AccountReqDto;
import kda.learn.microservices.hw7.dto.AccountRespDto;
import kda.learn.microservices.hw7.dto.DebitReqDto;
import kda.learn.microservices.hw7.dto.DepositReqDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/")
public class BillingController {

    private static final Logger log = LoggerFactory.getLogger(BillingController.class);

    @PostMapping(value = "/account")
    public ResponseEntity<AccountRespDto> createAccount(@RequestBody AccountReqDto accountReqDto) {
        log.info("CALL: createAccount");
        return new ResponseEntity<AccountRespDto>(HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping(value = "/deposit")
    public ResponseEntity<Void> depositAccount(@RequestBody DepositReqDto depositReqDto) {
        log.info("CALL: depositAccount");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping(value = "/debit")
    public ResponseEntity<Void> debitAccount(@RequestBody DebitReqDto debitReqDto) {
        log.info("CALL: debitAccount");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }
}
