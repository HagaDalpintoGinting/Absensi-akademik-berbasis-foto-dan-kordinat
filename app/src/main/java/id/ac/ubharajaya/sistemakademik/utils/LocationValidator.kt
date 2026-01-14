package id.ac.ubharajaya.sistemakademik.utils

import kotlin.math.*

/**
 * FILE: LocationValidator.kt
 * LOKASI: utils/LocationValidator.kt
 *
 * Deskripsi:
 * Class untuk menghitung jarak antara 2 koordinat menggunakan Haversine Formula
 * dan memvalidasi apakah lokasi mahasiswa berada dalam radius kampus
 */

object LocationValidator {

    // Radius bumi dalam meter
    private const val EARTH_RADIUS_METER = 6371000.0

    /**
     * Menghitung jarak antara 2 koordinat menggunakan Haversine Formula
     *
     * Formula ini digunakan untuk menghitung jarak terpendek antara 2 titik
     * di permukaan bumi (great-circle distance)
     *
     * @param lat1 Latitude titik 1 (dalam derajat)
     * @param lon1 Longitude titik 1 (dalam derajat)
     * @param lat2 Latitude titik 2 (dalam derajat)
     * @param lon2 Longitude titik 2 (dalam derajat)
     * @return Jarak dalam METER
     */
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        // Konversi derajat ke radian
        val lat1Rad = Math.toRadians(lat1)
        val lon1Rad = Math.toRadians(lon1)
        val lat2Rad = Math.toRadians(lat2)
        val lon2Rad = Math.toRadians(lon2)

        // Selisih koordinat
        val dLat = lat2Rad - lat1Rad
        val dLon = lon2Rad - lon1Rad

        // Haversine Formula
        val a = sin(dLat / 2).pow(2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        // Jarak dalam meter
        return EARTH_RADIUS_METER * c
    }

    /**
     * Validasi apakah lokasi mahasiswa berada dalam radius kampus
     *
     * @param userLat Latitude mahasiswa
     * @param userLon Longitude mahasiswa
     * @param campusLat Latitude kampus
     * @param campusLon Longitude kampus
     * @param radiusMeter Radius validasi dalam meter
     * @return Pair(isValid, distance)
     *         - isValid: true jika dalam radius, false jika di luar
     *         - distance: jarak dalam meter
     */
    fun isLocationValid(
        userLat: Double,
        userLon: Double,
        campusLat: Double = Constants.KAMPUS_LATITUDE,
        campusLon: Double = Constants.KAMPUS_LONGITUDE,
        radiusMeter: Double = Constants.RADIUS_METER
    ): Pair<Boolean, Double> {

        val distance = calculateDistance(userLat, userLon, campusLat, campusLon)
        val isValid = distance <= radiusMeter

        return Pair(isValid, distance)
    }

    /**
     * Format jarak ke string yang mudah dibaca
     *
     * @param distanceMeter Jarak dalam meter
     * @return String format "X.XX m" atau "X.XX km"
     */
    fun formatDistance(distanceMeter: Double): String {
        return if (distanceMeter < 1000) {
            "%.0f m".format(distanceMeter)
        } else {
            "%.2f km".format(distanceMeter / 1000)
        }
    }

    /**
     * Get status message berdasarkan validasi lokasi
     *
     * @param isValid Apakah lokasi valid
     * @param distance Jarak dari kampus
     * @return Pesan status
     */
    fun getStatusMessage(isValid: Boolean, distance: Double): String {
        return if (isValid) {
            "${Constants.MSG_LOKASI_VALID}\nJarak: ${formatDistance(distance)}"
        } else {
            "${Constants.MSG_LOKASI_INVALID}\nJarak: ${formatDistance(distance)}"
        }
    }
}