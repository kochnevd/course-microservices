package kda.learn.microservices.hw7.controllers;

import kda.learn.microservices.hw7.dto.OrderRespDto;
import kda.learn.microservices.hw7.dto.OrderReqDto;
import kda.learn.microservices.hw7.dto.UserReqDto;
import kda.learn.microservices.hw7.dto.UserRespDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/")
public class OrdersController {

    private static final Logger log = LoggerFactory.getLogger(OrdersController.class);

    @PostMapping(value = "/order")
    public ResponseEntity<OrderRespDto> createOrder(@RequestBody OrderReqDto body) {
        log.info("CALL: createOrder");
        return new ResponseEntity<OrderRespDto>(HttpStatus.PAYMENT_REQUIRED);
    }

    @PostMapping(value = "/user")
    public ResponseEntity<UserRespDto> createUser(@RequestBody UserReqDto user) {
        log.info("CALL: createUser");
        return new ResponseEntity<UserRespDto>(HttpStatus.NOT_IMPLEMENTED);
    }
}
