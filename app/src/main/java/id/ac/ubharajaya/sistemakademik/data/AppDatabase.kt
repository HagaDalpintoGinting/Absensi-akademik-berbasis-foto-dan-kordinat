package id.ac.ubharajaya.sistemakademik.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * FILE: AppDatabase.kt
 * LOKASI: data/AppDatabase.kt
 *
 * Deskripsi:
 * Konfigurasi Room Database untuk aplikasi
 * Menggunakan Singleton pattern agar hanya ada 1 instance database
 */

@Database(
    entities = [AbsensiEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Abstract function untuk mendapatkan DAO
    abstract fun absensiDao(): AbsensiDao

    companion object {
        // Singleton instance
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Get atau create database instance
         * Menggunakan synchronized untuk thread-safety
         *
         * @param context Application context
         * @return Database instance
         */
        fun getDatabase(context: Context): AppDatabase {
            // Jika instance sudah ada, return instance tersebut
            return INSTANCE ?: synchronized(this) {
                // Double-check locking untuk memastikan hanya 1 instance
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sistem_akademik_database"  // ✅ DIPERBAIKI: Gunakan string langsung
                )
                    // Fallback strategy jika ada perubahan schema
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }

        /**
         * Destroy database instance (untuk testing)
         */
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}

/**
 * CARA PAKAI:
 *
 * // Di Activity/Screen:
 * val database = AppDatabase.getDatabase(context)
 * val dao = database.absensiDao()
 *
 * // Insert data:
 * lifecycleScope.launch {
 *     val id = dao.insert(absensiEntity)
 * }
 *
 * // Get data:
 * val allAbsensi = dao.getAllAbsensi().collectAsState(initial = emptyList())
 */