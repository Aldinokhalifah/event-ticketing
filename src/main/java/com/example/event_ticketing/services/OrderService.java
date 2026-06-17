package com.example.event_ticketing.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.event_ticketing.dto.request.CreateOrderRequest;
import com.example.event_ticketing.dto.request.OrderItemRequest;
import com.example.event_ticketing.dto.response.OrderItemResponse;
import com.example.event_ticketing.dto.response.OrderResponse;
import com.example.event_ticketing.models.OrderItem;
import com.example.event_ticketing.models.Tiket;
import com.example.event_ticketing.models.Order;
import com.example.event_ticketing.models.TiketKategori;
import com.example.event_ticketing.models.User;
import com.example.event_ticketing.models.enums.OrderStatus;
import com.example.event_ticketing.models.enums.TiketStatus;
import com.example.event_ticketing.repositories.OrderItemRepository;
import com.example.event_ticketing.repositories.OrderRepository;
import com.example.event_ticketing.repositories.TiketKategoriRepository;
import com.example.event_ticketing.repositories.TiketRepository;
import com.example.event_ticketing.utils.TiketCodeGenerator;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TiketKategoriRepository tiketKategoriRepository;
    private final TiketRepository tiketRepository;
    private final TiketCodeGenerator tiketCodeGenerator;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, TiketKategoriRepository tiketKategoriRepository, TiketRepository tiketRepository, TiketCodeGenerator tiketCodeGenerator) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.tiketKategoriRepository = tiketKategoriRepository;
        this.tiketRepository = tiketRepository;
        this.tiketCodeGenerator = tiketCodeGenerator;
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
            .id(item.getId())
            .jumlah(item.getJumlah())
            .hargaSatuan(item.getHargaSatuan())
            .tiketKategoriId(item.getTiketKategori().getId())
            .build();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = orderItemRepository.findByOrderId(order.getId())
            .stream()
            .map(this::toOrderItemResponse)
            .collect(Collectors.toList());

        return OrderResponse.builder()
            .id(order.getId())
            .kodeOrder(order.getKodeOrder())
            .tanggalOrder(order.getTanggalOrder())
            .totalHarga(order.getTotalHarga())
            .status(order.getStatus())
            .userId(order.getUser().getId())
            .items(items)
            .build();
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, User currentUser) {
        BigDecimal totalHarga = BigDecimal.ZERO;
        Map<UUID, TiketKategori> kategoriMap = new HashMap<>();

        for(OrderItemRequest i : request.getItems()) {
            TiketKategori tiketKategori = tiketKategoriRepository.findByIdWithLock(i.getTiketKategoriId());

            if(tiketKategori == null) {
                throw new RuntimeException("Tiket kategori tidak ditemukan");
            }

            int sisaKuota = tiketKategori.getKuota() - tiketKategori.getTerjual();

            if(sisaKuota < i.getJumlah()) {
                throw new RuntimeException("Kuota tiket " + tiketKategori.getNama() + " tidak mencukupi");
            }

            kategoriMap.put(i.getTiketKategoriId(), tiketKategori);

            BigDecimal itemTotal = tiketKategori.getHarga().multiply(BigDecimal.valueOf(i.getJumlah()));
            totalHarga = totalHarga.add(itemTotal);
        }

        Order order = Order.builder()
            .kodeOrder(UUID.randomUUID().toString())
            .tanggalOrder(LocalDateTime.now())
            .totalHarga(totalHarga)
            .status(OrderStatus.PENDING)
            .user(currentUser)
            .build();

        order = orderRepository.save(order);

        for(OrderItemRequest item : request.getItems()) {
            TiketKategori tiketKategori = kategoriMap.get(item.getTiketKategoriId());

            OrderItem orderItem = OrderItem.builder()
                .order(order)
                .tiketKategori(tiketKategori)
                .jumlah(item.getJumlah())
                .hargaSatuan(tiketKategori.getHarga())
                .build();
            orderItem = orderItemRepository.save(orderItem);

            tiketKategori.setTerjual(tiketKategori.getTerjual() + item.getJumlah());
            tiketKategoriRepository.save(tiketKategori);

            for (int i = 0; i < item.getJumlah(); i++) {
                Tiket tiket = Tiket.builder()
                    .kodeTiket(tiketCodeGenerator.generate())
                    .status(TiketStatus.ACTIVE)
                    .orderItem(orderItem)
                    .user(currentUser)
                    .build();
                tiketRepository.save(tiket);
            }
        }
        return toResponse(order);
    }

    public List<OrderResponse> getUserOrders(User currentUser) {
        List<Order> orders = orderRepository.findByUserId(currentUser.getId());
        return orders.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(UUID id, User currentUser) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan"));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Order bukan milik user saat ini.");
        }
        return toResponse(order);
    }

    @Transactional
    public OrderResponse payOrder(UUID id, User currentUser) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan"));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Order bukan milik user saat ini.");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order tidak dapat dibayar karena status bukan PENDING");
        }

        order.setStatus(OrderStatus.PAID);
        order = orderRepository.save(order);
        return toResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(UUID id, User currentUser) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan"));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Order bukan milik user saat ini.");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order tidak dapat dibatalkan karena status bukan PENDING");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem item : items) {
            TiketKategori kategori = item.getTiketKategori();
            kategori.setTerjual(kategori.getTerjual() - item.getJumlah());
            tiketKategoriRepository.save(kategori);

            List<Tiket> tickets = tiketRepository.findByOrderItemId(item.getId());
            for (Tiket tiket : tickets) {
                tiket.setStatus(TiketStatus.CANCELLED);
                tiketRepository.save(tiket);
            }
        }

        return toResponse(order);
    }
}
