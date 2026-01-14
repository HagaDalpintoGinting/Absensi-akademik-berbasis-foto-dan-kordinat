package id.ac.ubharajaya.sistemakademik.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import id.ac.ubharajaya.sistemakademik.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * FILE: UserPreferences.kt
 * LOKASI: data/UserPreferences.kt
 *
 * Deskripsi:
 * Class untuk menyimpan dan mengambil data user (login state)
 * Menggunakan DataStore Preferences (pengganti SharedPreferences yang lebih modern)
 */

// Extension property untuk DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    // Keys untuk menyimpan data
    private object PreferenceKeys {
        val NPM = stringPreferencesKey(Constants.PREF_NPM)
        val NAMA = stringPreferencesKey(Constants.PREF_NAMA)
        val IS_LOGGED_IN = booleanPreferencesKey(Constants.PREF_IS_LOGGED_IN)
    }

    /**
     * Simpan data login user
     */
    suspend fun saveUserData(npm: String, nama: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.NPM] = npm
            preferences[PreferenceKeys.NAMA] = nama
            preferences[PreferenceKeys.IS_LOGGED_IN] = true
        }
    }

    /**
     * Get NPM user
     */
    val npm: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.NPM] ?: ""
        }

    /**
     * Get nama user
     */
    val nama: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.NAMA] ?: ""
        }

    /**
     * Get status login
     */
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.IS_LOGGED_IN] ?: false
        }

    /**
     * Logout user (hapus semua data)
     */
    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Get semua data user sekaligus
     */
    data class UserData(
        val npm: String,
        val nama: String,
        val isLoggedIn: Boolean
    )

    val userData: Flow<UserData> = context.dataStore.data
        .map { preferences ->
            UserData(
                npm = preferences[PreferenceKeys.NPM] ?: "",
                nama = preferences[PreferenceKeys.NAMA] ?: "",
                isLoggedIn = preferences[PreferenceKeys.IS_LOGGED_IN] ?: false
            )
        }
}

/**
 * CARA PAKAI:
 *
 * // Di Activity/Screen:
 * val userPreferences = UserPreferences(context)
 *
 * // Simpan data login:
 * lifecycleScope.launch {
 *     userPreferences.saveUserData("202310715176", "HAGA DALPINTO GINTING")
 * }
 *
 * // Baca data:
 * val userData by userPreferences.userData.collectAsState(
 *     initial = UserPreferences.UserData("", "", false)
 * )
 *
 * // Logout:
 * lifecycleScope.launch {
 *     userPreferences.logout()
 * }
 */