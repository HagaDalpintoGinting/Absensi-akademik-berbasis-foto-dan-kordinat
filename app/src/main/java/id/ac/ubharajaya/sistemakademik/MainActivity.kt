package id.ac.ubharajaya.sistemakademik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import id.ac.ubharajaya.sistemakademik.data.MataKuliah
import id.ac.ubharajaya.sistemakademik.screens.*
import id.ac.ubharajaya.sistemakademik.ui.theme.SistemAkademikTheme
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * FILE: MainActivity.kt (UPDATED dengan SelectMatakuliah)
 *
 * Deskripsi:
 * Main Activity dengan Navigation Compose
 * FITUR BARU: Pilih mata kuliah sebelum absen
 *
 * Flow Baru:
 * Dashboard → SelectMatakuliah → Absensi → Success
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SistemAkademikTheme {
                AppNavigation()
            }
        }
    }
}

/**
 * Navigation Routes
 */
object Routes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val SELECT_MATAKULIAH = "select_matakuliah"
    const val ABSENSI = "absensi/{matakuliahJson}"
    const val SUCCESS = "success"
    const val HISTORY = "history"
    const val SCHEDULE = "schedule"

    fun createAbsensiRoute(mataKuliah: MataKuliah): String {
        val json = Gson().toJson(mataKuliah)
        val encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.toString())
        return "absensi/$encodedJson"
    }
}

/**
 * App Navigation
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val gson = Gson()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        // Login Screen
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Dashboard Screen
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onNavigateToAbsensi = {
                    // Navigasi ke SelectMatakuliah dulu (BARU!)
                    navController.navigate(Routes.SELECT_MATAKULIAH)
                },
                onNavigateToHistory = {
                    navController.navigate(Routes.HISTORY)
                },
                onNavigateToSchedule = {
                    navController.navigate(Routes.SCHEDULE)
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Select Mata Kuliah Screen (BARU!)
        composable(Routes.SELECT_MATAKULIAH) {
            SelectMatakuliahScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onMatakuliahSelected = { mataKuliah ->
                    // Navigasi ke Absensi dengan data MK
                    val route = Routes.createAbsensiRoute(mataKuliah)
                    navController.navigate(route)
                }
            )
        }

        // Absensi Screen (UPDATED - terima parameter MK)
        composable(
            route = Routes.ABSENSI,
            arguments = listOf(
                navArgument("matakuliahJson") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val matakuliahJson = backStackEntry.arguments?.getString("matakuliahJson")
            val decodedJson = URLDecoder.decode(matakuliahJson, StandardCharsets.UTF_8.toString())
            val mataKuliah = gson.fromJson(decodedJson, MataKuliah::class.java)

            AbsensiScreen(
                mataKuliah = mataKuliah,
                onNavigateToSuccess = {
                    navController.navigate(Routes.SUCCESS) {
                        popUpTo(Routes.DASHBOARD)
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Success Screen
        composable(Routes.SUCCESS) {
            SuccessScreen(
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
                onNavigateToHistory = {
                    navController.navigate(Routes.HISTORY) {
                        popUpTo(Routes.DASHBOARD)
                    }
                }
            )
        }

        // History Screen
        composable(Routes.HISTORY) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Schedule Screen
        composable(Routes.SCHEDULE) {
            ScheduleScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

/**
 * PENJELASAN FLOW BARU:
 *
 * 1. LOGIN → DASHBOARD
 *    - Sama seperti sebelumnya
 *
 * 2. DASHBOARD → SELECT_MATAKULIAH → ABSENSI → SUCCESS
 *    - User klik "Mulai Absensi"
 *    - Masuk ke screen pilih mata kuliah (filter hari ini)
 *    - User pilih MK
 *    - Data MK di-pass ke AbsensiScreen via navigation argument (JSON)
 *    - Absensi berhasil → Success screen
 *
 * 3. Passing MataKuliah Object:
 *    - Convert MataKuliah → JSON → URL Encode
 *    - Pass via navigation argument
 *    - Di destination: URL Decode → JSON → MataKuliah object
 *
 * 4. Database Update:
 *    - AbsensiEntity sekarang punya field mata kuliah
 *    - History menampilkan nama mata kuliah
 *    - Filter MK yang sudah diabsen hari ini
 */