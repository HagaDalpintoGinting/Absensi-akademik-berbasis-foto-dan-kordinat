package id.ac.ubharajaya.sistemakademik.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

/**
 * FILE: SuccessScreen.kt
 * LOKASI: screens/SuccessScreen.kt
 *
 * Deskripsi:
 * Screen konfirmasi absensi berhasil
 * Menampilkan:
 * - Icon checklist
 * - Pesan sukses
 * - Waktu absensi
 * - Tombol navigasi
 */

@Composable
fun SuccessScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val primaryGreen = Color(0xFF2E7D32)
    val lightGreen = Color(0xFF66BB6A)

    // Get waktu saat ini
    val currentTime = remember {
        val calendar = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))

        Pair(
            timeFormat.format(calendar.time),
            dateFormat.format(calendar.time)
        )
    }

    // Auto navigate after 3 seconds (optional)
    var countdown by remember { mutableStateOf(5) }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
        // Bisa uncomment ini kalau mau auto navigate
        // onNavigateToDashboard()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Success Icon
            Card(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(60.dp),
                colors = CardDefaults.cardColors(
                    containerColor = lightGreen.copy(alpha = 0.2f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        modifier = Modifier.size(80.dp),
                        tint = primaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Absensi Berhasil!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = primaryGreen,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Kehadiran Anda telah tercatat",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = lightGreen.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Checklist items
                    CheckItem("✓ Lokasi Tervalidasi", primaryGreen)
                    Spacer(modifier = Modifier.height(8.dp))
                    CheckItem("✓ Foto Terekam", primaryGreen)

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Waktu
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Waktu: ${currentTime.first} WIB",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = currentTime.second,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Status
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = primaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Berhasil tercatat!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = primaryGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Button Lihat Riwayat
            Button(
                onClick = onNavigateToHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryGreen
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("LIHAT RIWAYAT")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Button Kembali ke Dashboard
            OutlinedButton(
                onClick = onNavigateToDashboard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = primaryGreen
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("KEMBALI KE MENU")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Countdown info (optional)
            if (countdown > 0) {
                Text(
                    text = "Otomatis kembali dalam $countdown detik",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun CheckItem(text: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * CATATAN IMPLEMENTASI:
 *
 * 1. Screen ini menampilkan konfirmasi bahwa absensi berhasil
 *
 * 2. Menampilkan waktu absensi yang tepat
 *
 * 3. Ada 2 tombol navigasi:
 *    - Lihat Riwayat: ke HistoryScreen
 *    - Kembali ke Menu: ke Dashboard
 *
 * 4. Optional: Auto navigate setelah beberapa detik
 */