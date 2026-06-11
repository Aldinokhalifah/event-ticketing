package com.example.event_ticketing.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class CreateEventRequest {
    @NotBlank(message="Nama wajib diisi")
    @Size(max = 256, message = "Nama maksimal 50 karakter")
    String nama;

    @NotBlank(message="Deskripsi wajib diisi")
    String deskripsi;

    @NotBlank(message="Lokasi wajib diisi")
    String lokasi;

    @NotNull(message="Tanggal wajib diisi")
    LocalDate tanggal;

    @NotNull(message="Waktu mulai wajib diisi")
    LocalTime waktuMulai;

    @NotNull(message="Waktu selesai wajib diisi")
    LocalTime waktuSelesai;
}
