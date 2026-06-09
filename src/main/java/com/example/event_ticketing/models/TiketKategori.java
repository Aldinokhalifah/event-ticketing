package com.example.event_ticketing.models;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tiket_kategories")
@Getter@Setter@NoArgsConstructor@AllArgsConstructor@Builder
public class TiketKategori {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 256)
    private String nama;

    @Column(nullable = false)
    private BigDecimal harga;

    @Column(nullable = false)
    private Integer kuota;

    
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // Field 2: kolom biasa dengan default
    @Column(nullable = false)
    @Builder.Default
    private Integer terjual = 0;
}
