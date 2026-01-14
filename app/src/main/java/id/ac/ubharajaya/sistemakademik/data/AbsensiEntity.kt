package id.ac.ubharajaya.sistemakademik.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * FILE: AbsensiEntity.kt
 * LOKASI: data/AbsensiEntity.kt
 *
 * Deskripsi:
 * Entity class untuk tabel absensi di Room Database
 * Setiap object AbsensiEntity merepresentasikan 1 record absensi
 */

@Entity(tableName = "absensi")
data class AbsensiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Data Mahasiswa
    val npm: String,
    val nama: String,

    // Data Mata Kuliah (BARU!)
    val kodeMatakuliah: String,     // Contoh: "INFO-3527"
    val namaMatakuliah: String,     // Contoh: "Pemrograman Mobile"
    val dosenMatakuliah: String,    // Contoh: "Dr. Ahmad Wijaya"
    val ruanganMatakuliah: String,  // Contoh: "Lab Komputer 1"
    val waktuMatakuliah: String,    // Contoh: "08:00 - 10:00"

    // Data Lokasi
    val latitude: Double,
    val longitude: Double,
    val jarak: Double,          // Jarak dari kampus dalam meter
    val lokasiValid: Boolean,   // Apakah lokasi dalam radius

    // Data Waktu
    val timestamp: Long,        // Timestamp dalam milliseconds
    val tanggal: String,        // Format: "14 Jan 2026"
    val waktu: String,          // Format: "09:15 WIB"
    val hari: String,           // Format: "Rabu"

    // Data Foto (Base64)
    val fotoBase64: String,

    // Status
    val statusKirim: Boolean = false,  // Apakah sudah terkirim ke webhook
    val pesanError: String? = null     // Pesan error jika gagal kirim
)