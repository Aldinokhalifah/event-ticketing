package com.example.event_ticketing.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateOrderRequest {
    @NotEmpty(message="Order tidak boleh kosong")
    @Valid
    List<OrderItemRequest> items;
}
