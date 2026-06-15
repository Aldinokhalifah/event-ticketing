package com.example.event_ticketing.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemRequest {
    @NotNull(message="Tiket kategori tidak boleh kosong")
    UUID tiketKategoriId;

    @NotNull(message="Jumlah tidak boleh kosong")
    @Min(1)
    Integer jumlah;
}
