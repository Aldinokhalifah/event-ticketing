package com.example.event_ticketing.models;

import java.util.UUID;

import com.example.event_ticketing.models.enums.TiketStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "tikets")
@Getter@Setter@NoArgsConstructor@AllArgsConstructor@Builder
public class Tiket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "kode_tiket", nullable = false, unique = true)
    private String kodeTiket;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TiketStatus status = TiketStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
