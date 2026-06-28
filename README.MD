# 🎫 Event Ticketing API

REST API untuk sistem penjualan tiket event, dibangun dengan Spring Boot. Project ini dibuat sebagai sarana belajar konsep **concurrency handling**, **status machine**, **cascade operation**, dan **JPQL aggregation query** dalam konteks aplikasi backend yang nyata.

---

## 📋 Daftar Isi

- [Tech Stack](#-tech-stack)
- [Fitur Utama](#-fitur-utama)
- [Domain Model](#-domain-model)
- [Konsep Teknis yang Dipelajari](#-konsep-teknis-yang-dipelajari)
- [Struktur Project](#-struktur-project)
- [Cara Setup](#-cara-setup)
- [API Documentation](#-api-documentation)
- [Status Machine](#-status-machine)
- [Catatan Desain & Trade-off](#-catatan-desain--trade-off)
- [Pengembangan Selanjutnya](#-pengembangan-selanjutnya)

---

## 🛠️ Tech Stack

| Kategori | Teknologi |
|---|---|
| Bahasa | Java 21 |
| Framework | Spring Boot 3.5.x |
| Database | PostgreSQL |
| ORM | Spring Data JPA (Hibernate) |
| Security | Spring Security + JWT (JJWT 0.12.x) |
| Build Tool | Maven |
| Library Tambahan | Lombok |

---

## ✨ Fitur Utama

### Autentikasi
- Register & login dengan JWT
- Role-based access control (`USER`, `ADMIN`)

### Manajemen Event (ADMIN)
- CRUD event lengkap
- Status machine: `DRAFT → PUBLISHED → CANCELLED`
- Validasi transisi status (event yang sudah `PUBLISHED` tidak bisa diedit/dihapus)

### Manajemen Kategori Tiket (ADMIN)
- CRUD kategori tiket per event (misal: VIP, Regular)
- Kategori hanya bisa ditambahkan ke event yang sudah `PUBLISHED`
- Tracking kuota dan jumlah terjual secara real-time

### Pemesanan Tiket (USER)
- Buat order untuk multiple kategori tiket sekaligus dalam satu transaksi
- **Concurrency-safe** — mencegah oversold tiket saat banyak user memesan bersamaan
- Auto-generate tiket dengan kode unik setiap order berhasil dibuat
- Pembayaran order (`PENDING → PAID`)
- Pembatalan order dengan otomatis mengembalikan kuota (`PENDING → CANCELLED`)

### Tiket & Check-in
- USER dapat melihat semua tiket miliknya
- ADMIN dapat melakukan check-in tiket (validasi kehadiran di lokasi event)
- Status machine tiket: `ACTIVE → USED` / `CANCELLED`

### Laporan Penjualan (ADMIN)
- Ringkasan penjualan per event menggunakan JPQL aggregation
- Breakdown total tiket terjual dan pendapatan per kategori tiket
- Hanya menghitung order dengan status `PAID` (transaksi yang benar-benar sukses)

### Error Handling
- Global Exception Handler untuk response error yang konsisten
- Custom handler untuk error otorisasi (403) dan autentikasi (401) dari Security Filter Chain

---

## 🗂️ Domain Model

```
User ──< Order ──< OrderItem >── TiketKategori >── Event
                      │
                      └──< Tiket
```

### Entity

| Entity | Deskripsi |
|---|---|
| `User` | id, nama, email, password, role (`USER`/`ADMIN`) |
| `Event` | id, nama, deskripsi, lokasi, tanggal, waktuMulai, waktuSelesai, status |
| `TiketKategori` | id, nama, harga, kuota, terjual, relasi ke Event |
| `Order` | id, kodeOrder, tanggalOrder, totalHarga, status, relasi ke User |
| `OrderItem` | id, jumlah, hargaSatuan, relasi ke Order dan TiketKategori |
| `Tiket` | id, kodeTiket (unik), status, relasi ke OrderItem dan User |

### Relasi

| Dari | Ke | Tipe |
|---|---|---|
| Event | TiketKategori | One-to-Many |
| Order | OrderItem | One-to-Many |
| OrderItem | TiketKategori | Many-to-One |
| OrderItem | Tiket | One-to-Many (via generate saat order) |
| User | Order | One-to-Many |
| User | Tiket | One-to-Many |

---

## 🎓 Konsep Teknis yang Dipelajari

### 1. Concurrency Handling — Mencegah Oversold Tiket

Saat banyak user memesan tiket dengan kuota terbatas secara bersamaan, tanpa penanganan khusus bisa terjadi **race condition**:

```
Sisa kuota = 1
User A: cek kuota → sisa 1 ✓
User B: cek kuota → sisa 1 ✓ (belum tau A sudah booking)
User A: kurangi kuota → terjual = 1
User B: kurangi kuota → terjual = 2 ← OVERSOLD!
```

**Solusi:** Pessimistic Lock di level database.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT t FROM TiketKategori t WHERE t.id = :id")
TiketKategori findByIdWithLock(UUID id);
```

Row `TiketKategori` yang sedang diproses akan terkunci selama transaksi berjalan. User lain yang mencoba memesan kategori tiket yang sama harus menunggu sampai transaksi sebelumnya selesai (commit/rollback), sehingga validasi kuota selalu dilakukan terhadap data yang paling akurat.

Dikombinasikan dengan `@Transactional` agar seluruh proses (validasi kuota → kurangi kuota → buat order → generate tiket) bersifat atomik — jika ada langkah yang gagal, semua perubahan di-rollback.

### 2. Status Machine

Tiga entity di project ini punya status yang berubah berdasarkan aturan bisnis tertentu, tidak bisa diubah sembarangan:

- **Event**: `DRAFT → PUBLISHED → CANCELLED`
- **Order**: `PENDING → PAID` / `PENDING → CANCELLED`
- **Tiket**: `ACTIVE → USED` / `ACTIVE → CANCELLED`

Setiap transisi divalidasi di service layer sebelum diizinkan.

### 3. Cascade Operation

Saat satu order dibuat, sistem otomatis men-generate beberapa entity terkait dalam satu rangkaian transaksi:

```
1 Order
  └── N OrderItem (sesuai jumlah kategori tiket yang dipesan)
        └── N Tiket (sesuai jumlah tiket per kategori)
```

### 4. JPQL Aggregation dengan Constructor Expression

Laporan penjualan dihitung langsung di level database menggunakan JPQL `SUM()` dan `GROUP BY`, lalu hasilnya langsung dipetakan ke DTO menggunakan constructor expression:

```java
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
```

Laporan hanya menghitung order dengan status `PAID`, sehingga transaksi yang belum dibayar atau sudah dibatalkan tidak ikut terhitung sebagai pendapatan.

---

## 📁 Struktur Project

```
src/main/java/com/example/event_ticketing/
├── controllers/      → REST endpoint
├── services/         → Business logic
├── repositories/      → Data access layer (Spring Data JPA)
├── models/            → Entity JPA
│   └── enums/         → EventStatus, OrderStatus, TiketStatus, Role
├── dto/
│   ├── request/        → Request body DTO
│   └── response/       → Response DTO
├── security/          → JWT filter, SecurityConfig, custom handlers
├── exception/         → Global Exception Handler
└── utils/              → TiketCodeGenerator
```

---

## ⚙️ Cara Setup

### Prasyarat
- Java 21
- Maven
- PostgreSQL

### Langkah Instalasi

1. Clone repository ini

2. Buat database PostgreSQL:
```sql
CREATE DATABASE event_ticketing;
```

3. Konfigurasi `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/event_ticketing
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

app.jwt.secret=GANTI_DENGAN_SECRET_KEY_PANJANG_DAN_AMAN
app.jwt.expiration=86400000
```

4. Jalankan aplikasi:
```bash
mvn spring-boot:run
```

5. Aplikasi berjalan di `http://localhost:8080`

---

## 📡 API Documentation

Semua response mengikuti format standar:
```json
{
  "success": true,
  "message": "...",
  "data": { ... }
}
```

### Auth

| Method | Endpoint | Akses | Keterangan |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Registrasi user baru |
| POST | `/api/auth/login` | Public | Login, mengembalikan JWT token |

### Event

| Method | Endpoint | Akses | Keterangan |
|---|---|---|---|
| POST | `/api/events` | ADMIN | Buat event baru (status `DRAFT`) |
| GET | `/api/events` | Semua user | Lihat semua event |
| GET | `/api/events/{id}` | Semua user | Lihat detail event |
| PUT | `/api/events/{id}` | ADMIN | Update event (hanya saat `DRAFT`) |
| PATCH | `/api/events/{id}/publish` | ADMIN | Publikasikan event |
| PATCH | `/api/events/{id}/cancel` | ADMIN | Batalkan event |
| DELETE | `/api/events/{id}` | ADMIN | Hapus event (hanya saat `DRAFT`) |

### Kategori Tiket

| Method | Endpoint | Akses | Keterangan |
|---|---|---|---|
| POST | `/api/events/{eventId}/tiket-kategori` | ADMIN | Tambah kategori tiket (event harus `PUBLISHED`) |
| GET | `/api/events/{eventId}/tiket-kategori` | Semua user | Lihat semua kategori milik satu event |
| PUT | `/api/tiket-kategori/{id}` | ADMIN | Update kategori tiket |
| DELETE | `/api/tiket-kategori/{id}` | ADMIN | Hapus kategori tiket |

### Order

| Method | Endpoint | Akses | Keterangan |
|---|---|---|---|
| POST | `/api/orders` | USER | Buat order (bisa multiple kategori sekaligus) |
| GET | `/api/orders` | USER | Lihat semua order milik sendiri |
| GET | `/api/orders/{id}` | USER | Lihat detail order milik sendiri |
| PATCH | `/api/orders/{id}/pay` | USER | Bayar order (`PENDING → PAID`) |
| PATCH | `/api/orders/{id}/cancel` | USER | Batalkan order (`PENDING → CANCELLED`), kuota dikembalikan |

**Contoh request body — Create Order:**
```json
{
  "items": [
    { "tiketKategoriId": "uuid-vip", "jumlah": 2 },
    { "tiketKategoriId": "uuid-regular", "jumlah": 3 }
  ]
}
```

### Tiket

| Method | Endpoint | Akses | Keterangan |
|---|---|---|---|
| GET | `/api/tikets` | USER | Lihat semua tiket milik sendiri |
| PATCH | `/api/tikets/{id}/checkin` | ADMIN | Check-in tiket (validasi kehadiran) |

### Laporan

| Method | Endpoint | Akses | Keterangan |
|---|---|---|---|
| GET | `/api/laporan/events/{eventId}` | ADMIN | Ringkasan penjualan per event |

**Contoh response — Laporan:**
```json
{
  "success": true,
  "message": "Laporan summary event berhasil diambil",
  "data": {
    "eventId": "...",
    "namaEvent": "Java Developer Conference 2026",
    "totalTiketTerjual": 150,
    "totalPendapatan": 75000000,
    "rincianKategori": [
      { "nama": "VIP", "terjual": 50, "pendapatan": 25000000 },
      { "nama": "Regular", "terjual": 100, "pendapatan": 50000000 }
    ]
  }
}
```

---

## 🔄 Status Machine

### Event
```
DRAFT ──publish──> PUBLISHED ──cancel──> CANCELLED
  │
  └──delete (hanya saat DRAFT)
```

### Order
```
PENDING ──pay────> PAID
   │
   └──cancel──> CANCELLED (kuota dikembalikan, tiket terkait di-CANCELLED)
```

### Tiket
```
ACTIVE ──checkin──> USED
   │
   └── (mengikuti CANCELLED jika order dibatalkan)
```

---

## 💡 Catatan Desain & Trade-off

- **Kode unik tiket** menggunakan kombinasi UUID + timestamp (`TKT-{8 karakter UUID}-{timestamp}`) untuk memastikan keunikan tanpa perlu sequence tambahan di database.
- **Laporan penjualan** sengaja query langsung ke `OrderItem` dengan filter `status = PAID`, bukan memakai field `terjual` di `TiketKategori`. Field `terjual` menghitung semua tiket yang sudah dipesan (termasuk yang masih `PENDING`), sementara laporan keuangan harus mencerminkan transaksi yang benar-benar sukses dibayar.
- **Validasi kepemilikan order** dilakukan dengan membandingkan UUID (`order.getUser().getId()`), bukan membandingkan object `User` secara langsung — karena entity tidak mengimplementasikan `equals()`/`hashCode()` custom (untuk menghindari masalah lazy loading Hibernate).
- **Role ADMIN** pada endpoint check-in tiket merepresentasikan peran panitia/petugas di lokasi event, bukan harus super-admin tunggal.

---

## 🚀 Pengembangan Selanjutnya

- [ ] Pagination & filtering pada endpoint list (Event, Order)
- [ ] Role tambahan (`STAFF`) khusus untuk operasional check-in
- [ ] Integrasi payment gateway untuk proses `payOrder` yang sesungguhnya
- [ ] Notifikasi email/WhatsApp saat order berhasil dibuat atau dibayar
- [ ] Generate QR Code untuk kode tiket
- [ ] Unit test (JUnit + Mockito) khususnya untuk logic concurrency di `OrderService`
- [ ] Integrasi dengan frontend (Next.js)
- [ ] Swagger/OpenAPI documentation

---

## 👤 Author

Dikembangkan oleh **Aldino Khalifah** sebagai project pembelajaran backend development dengan Spring Boot.