package com.example.event_ticketing.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.event_ticketing.dto.response.KategoriSummaryResponse;
import com.example.event_ticketing.models.OrderItem;
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID>{
    List<OrderItem> findByOrderId(UUID orderId);

    @Query("""
        SELECT new com.example.event_ticketing.dto.response.KategoriSummaryResponse(
            oi.tiketKategori.nama,
            SUM(oi.jumlah),
            SUM(oi.jumlah * oi.hargaSatuan)
        )
        FROM OrderItem oi
        WHERE oi.tiketKategori.event.id = :eventId
        AND oi.order.status = 'PAID'
        GROUP BY oi.tiketKategori.nama
    """)
    List<KategoriSummaryResponse> getSalesSummaryByEvent(@Param("eventId") UUID eventId);
}
