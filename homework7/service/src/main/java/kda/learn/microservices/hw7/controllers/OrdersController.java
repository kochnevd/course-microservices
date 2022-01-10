package kda.learn.microservices.hw7.controllers;

import kda.learn.microservices.hw7.dto.OrderRespDto;
import kda.learn.microservices.hw7.dto.OrderReqDto;
import kda.learn.microservices.hw7.dto.UserReqDto;
import kda.learn.microservices.hw7.dto.UserRespDto;
import kda.learn.microservices.hw7.services.OrdersService;
import kda.learn.microservices.hw7.transformers.OrdersTransformer;
import kda.learn.microservices.hw7.transformers.UsersTransformer;
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

    private final OrdersService service;

    public OrdersController(OrdersService service) {
        this.service = service;
    }

    @PostMapping(value = "/order")
    public ResponseEntity<OrderRespDto> createOrder(@RequestBody OrderReqDto order) {
        log.info("CALL: createOrder");

        OrderRespDto res = OrdersTransformer.transformToDto(
                service.createOrder(
                        OrdersTransformer.transformFromDto(order)
                )
        );

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping(value = "/user")
    public ResponseEntity<UserRespDto> createUser(@RequestBody UserReqDto user) {
        log.info("CALL: createUser");

        UserRespDto res = UsersTransformer.transformToDto(
                service.createUser(
                        UsersTransformer.transformFromDto(user)
                )
        );

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}
