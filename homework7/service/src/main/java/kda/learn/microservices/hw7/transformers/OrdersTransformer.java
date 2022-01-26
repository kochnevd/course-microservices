package kda.learn.microservices.hw7.transformers;

import kda.learn.microservices.hw7.dto.OrderReqDto;
import kda.learn.microservices.hw7.dto.OrderRespDto;
import kda.learn.microservices.hw7.storage.entities.Order;

public class OrdersTransformer {
    public static Order transformFromDto(OrderReqDto orderDto) {
        return new Order()
                .setUserId(orderDto.getUserId())
                .setUuid(orderDto.getUuid())
                .setCost(orderDto.getCost())
                .setOrderContent(orderDto.getOrderContent());
    }

    public static OrderRespDto transformToDto(Order order) {
        return new OrderRespDto()
                .id(order.getId());
    }
}
