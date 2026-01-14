package id.ac.ubharajaya.sistemakademik.utils

/**
 * FILE: Constants.kt
 * LOKASI: utils/Constants.kt
 *
 * Deskripsi:
 * File ini berisi konstanta-konstanta yang digunakan di seluruh aplikasi
 * seperti koordinat kampus, radius validasi, URL webhook, dll.
 */

object Constants {

    // ==================== DATA MAHASISWA ====================
    const val NPM = "202310715176"
    const val NAMA = "HAGA DALPINTO GINTING"

    // ==================== KOORDINAT KAMPUS ====================
    // Universitas Bhayangkara Jakarta Raya - Bekasi Utara
    // Jl. Raya Perjuangan No.81, Bekasi Utara
    const val KAMPUS_LATITUDE = -6.224190375334469
    const val KAMPUS_LONGITUDE = 107.00928773168418
    const val KAMPUS_NAMA = "Universitas Bhayangkara Jakarta Raya"
    const val KAMPUS_ALAMAT = "Jl. Raya Perjuangan No.81, Bekasi Utara"

    // ==================== VALIDASI LOKASI ====================
    // Radius dalam METER untuk validasi absensi
    // Mahasiswa harus berada dalam radius ini dari koordinat kampus
    const val RADIUS_METER = 100.0  // 100 meter

    // Untuk testing, bisa diubah jadi lebih besar:
    // const val RADIUS_METER = 5000.0  // 5 km untuk testing

    // ==================== WEBHOOK URLs ====================
    // URL untuk mengirim data absensi ke server N8N
    const val WEBHOOK_URL_PRODUCTION = "https://n8n.lab.ubharajaya.ac.id/webhook/23c6993d-1792-48fb-ad1c-ffc78a3e6254"
    const val WEBHOOK_URL_TEST = "https://n8n.lab.ubharajaya.ac.id/webhook-test/23c6993d-1792-48fb-ad1c-ffc78a3e6254"

    // Pilih URL yang aktif (ubah ke TEST untuk testing)
    const val WEBHOOK_URL_ACTIVE = WEBHOOK_URL_PRODUCTION

    // ==================== PREFERENCES KEYS ====================
    // Key untuk menyimpan data di SharedPreferences/DataStore
    const val PREF_NPM = "pref_npm"
    const val PREF_NAMA = "pref_nama"
    const val PREF_IS_LOGGED_IN = "pref_is_logged_in"

    // ==================== DATABASE ====================
    const val DATABASE_NAME = "absensi_database"
    const val DATABASE_VERSION = 1

    // ==================== MESSAGES ====================
    const val MSG_LOKASI_VALID = "✓ Lokasi Valid - Dalam Area Kampus"
    const val MSG_LOKASI_INVALID = "✗ Lokasi Tidak Valid - Di Luar Area Kampus"
    const val MSG_MENUNGGU_LOKASI = "⌛ Menunggu Data Lokasi..."
    const val MSG_FOTO_BELUM_DIAMBIL = "Silakan ambil foto terlebih dahulu"
    const val MSG_ABSENSI_BERHASIL = "Absensi Berhasil Dicatat!"
    const val MSG_ABSENSI_GAGAL = "Absensi Gagal!"

    // ==================== PERMISSIONS ====================
    val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CAMERA
    )
}