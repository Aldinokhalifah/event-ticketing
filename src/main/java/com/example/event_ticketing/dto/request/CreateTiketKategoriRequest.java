package com.example.event_ticketing.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class CreateTiketKategoriRequest {

    @NotBlank(message = "Nama wajib diisi")
    @Size(max = 256, message = "Nama maksimal 50 karakter")
    String nama;

    @NotNull(message = "harga wajib diisi")
    @DecimalMin(value = "0.0", message = "Harga tidak boleh negatif")
    BigDecimal harga;

    @NotNull(message = "kuota wajib diisi")
    @Min(1)
    Integer kuota;
}
