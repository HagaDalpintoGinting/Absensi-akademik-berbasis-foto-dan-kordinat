package id.ac.ubharajaya.sistemakademik.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * FILE: MataKuliah.kt
 * LOKASI: data/MataKuliah.kt
 *
 * Deskripsi:
 * Data class untuk Mata Kuliah
 * Digunakan untuk passing data antar screen
 */

@Parcelize
data class MataKuliah(
    val kode: String,           // Kode MK: "INFO-3527"
    val nama: String,           // Nama MK: "Pemrograman Mobile"
    val dosen: String,          // Nama Dosen: "Dr. Ahmad Wijaya"
    val ruangan: String,        // Ruangan: "Lab Komputer 1"
    val waktu: String,          // Waktu: "08:00 - 10:00"
    val hari: String,           // Hari: "Senin"
    val sks: Int = 3            // SKS (default 3)
) : Parcelable

/**
 * CATATAN:
 * - @Parcelize digunakan agar bisa di-pass lewat Navigation argument
 * - Parcelable lebih efisien daripada Serializable untuk Android
 */