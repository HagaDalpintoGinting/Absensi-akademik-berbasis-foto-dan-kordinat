package id.ac.ubharajaya.sistemakademik.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.ac.ubharajaya.sistemakademik.data.UserPreferences
import id.ac.ubharajaya.sistemakademik.utils.Constants
import kotlinx.coroutines.launch

/**
 * FILE: DashboardScreen.kt
 * LOKASI: screens/DashboardScreen.kt
 *
 * Deskripsi:
 * Screen menu utama setelah login
 * Menampilkan:
 * - Header dengan nama user dan lokasi kampus
 * - Menu navigasi: Jadwal Kuliah, Mulai Absensi, Riwayat Absensi
 * - Map preview (optional)
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAbsensi: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferences = remember { UserPreferences(context) }

    // Get user data
    val userData by userPreferences.userData.collectAsStateWithLifecycle(
        initialValue = UserPreferences.UserData("", "", false)
    )

    // Warna tema
    val primaryGreen = Color(0xFF2E7D32)
    val lightGreen = Color(0xFF66BB6A)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Menu Absensi",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryGreen,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            userPreferences.logout()
                            onLogout()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            // Header Card - Info User
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = lightGreen.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Selamat Datang, Budi!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = userData.nama.ifEmpty { Constants.NAMA },
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "NPM: ${userData.npm.ifEmpty { Constants.NPM }}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Menu Cards
            // Menu 1: Jadwal Kuliah
            MenuCard(
                title = "Jadwal Kuliah",
                icon = Icons.Default.Schedule,
                backgroundColor = Color(0xFFE8F5E9),
                iconColor = primaryGreen,
                onClick = onNavigateToSchedule
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Menu 2: Mulai Absensi (Primary Action)
            MenuCard(
                title = "MULAI ABSENSI",
                icon = Icons.Default.CameraAlt,
                backgroundColor = primaryGreen,
                iconColor = Color.White,
                textColor = Color.White,
                isPrimary = true,
                onClick = onNavigateToAbsensi
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Menu 3: Riwayat Absensi
            MenuCard(
                title = "Riwayat Absensi",
                icon = Icons.Default.History,
                backgroundColor = Color(0xFFE8F5E9),
                iconColor = primaryGreen,
                onClick = onNavigateToHistory
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Info Lokasi Kampus
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFFF6F00),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Lokasi: ${Constants.KAMPUS_NAMA}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = Constants.KAMPUS_ALAMAT,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer info
            Text(
                text = "📍 Pastikan lokasi GPS aktif untuk absensi",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

/**
 * Reusable Menu Card Component
 */
@Composable
fun MenuCard(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    textColor: Color = Color.Black,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isPrimary) 70.dp else 65.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPrimary) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(if (isPrimary) 32.dp else 28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = if (isPrimary) 18.sp else 16.sp,
                fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        }
    }
}

/**
 * CATATAN IMPLEMENTASI:
 *
 * 1. Dashboard menampilkan 3 menu utama:
 *    - Jadwal Kuliah (untuk melihat jadwal - bisa dikembangkan)
 *    - Mulai Absensi (menu utama - navigasi ke AbsensiScreen)
 *    - Riwayat Absensi (melihat history absensi)
 *
 * 2. Header menampilkan nama dan NPM dari UserPreferences
 *
 * 3. Tombol logout ada di TopBar kanan atas
 *
 * 4. Untuk production:
 *    - Tambahkan fitur jadwal kuliah yang proper
 *    - Tambahkan notifikasi jika ada jadwal kuliah hari ini
 *    - Tambahkan statistik kehadiran
 */