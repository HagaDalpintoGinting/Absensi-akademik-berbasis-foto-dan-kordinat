package id.ac.ubharajaya.sistemakademik.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * FILE: AbsensiDao.kt
 * LOKASI: data/AbsensiDao.kt
 *
 * Deskripsi:
 * Data Access Object (DAO) untuk operasi database absensi
 * Berisi query-query untuk insert, update, delete, dan get data
 */

@Dao
interface AbsensiDao {

    /**
     * Insert data absensi baru ke database
     * @return ID dari record yang baru diinsert
     */
    @Insert
    suspend fun insert(absensi: AbsensiEntity): Long

    /**
     * Update data absensi yang sudah ada
     */
    @Update
    suspend fun update(absensi: AbsensiEntity)

    /**
     * Delete data absensi
     */
    @Delete
    suspend fun delete(absensi: AbsensiEntity)

    /**
     * Get semua data absensi, diurutkan dari yang terbaru
     * Menggunakan Flow agar otomatis update UI ketika ada perubahan data
     */
    @Query("SELECT * FROM absensi ORDER BY timestamp DESC")
    fun getAllAbsensi(): Flow<List<AbsensiEntity>>

    /**
     * Get data absensi berdasarkan NPM
     */
    @Query("SELECT * FROM absensi WHERE npm = :npm ORDER BY timestamp DESC")
    fun getAbsensiByNpm(npm: String): Flow<List<AbsensiEntity>>

    /**
     * Get data absensi berdasarkan ID
     */
    @Query("SELECT * FROM absensi WHERE id = :id")
    suspend fun getAbsensiById(id: Int): AbsensiEntity?

    /**
     * Get jumlah total absensi
     */
    @Query("SELECT COUNT(*) FROM absensi")
    suspend fun getTotalAbsensi(): Int

    /**
     * Get jumlah absensi hari ini
     * @param tanggal Format: "14 Jan 2026"
     */
    @Query("SELECT COUNT(*) FROM absensi WHERE tanggal = :tanggal")
    suspend fun getAbsensiHariIniCount(tanggal: String): Int

    /**
     * Get list absensi hari ini (BARU untuk filter MK sudah absen)
     * @param tanggal Format: "14 Jan 2026"
     */
    @Query("SELECT * FROM absensi WHERE tanggal = :tanggal ORDER BY timestamp DESC")
    fun getAbsensiHariIni(tanggal: String): Flow<List<AbsensiEntity>>

    /**
     * Get absensi yang belum terkirim ke server
     */
    @Query("SELECT * FROM absensi WHERE statusKirim = 0 ORDER BY timestamp ASC")
    suspend fun getAbsensiPending(): List<AbsensiEntity>

    /**
     * Update status kirim absensi
     */
    @Query("UPDATE absensi SET statusKirim = :status WHERE id = :id")
    suspend fun updateStatusKirim(id: Int, status: Boolean)

    /**
     * Delete semua data absensi (untuk testing/reset)
     */
    @Query("DELETE FROM absensi")
    suspend fun deleteAll()
}