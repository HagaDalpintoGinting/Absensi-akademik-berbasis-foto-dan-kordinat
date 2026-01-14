package id.ac.ubharajaya.sistemakademik.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * FILE: ScheduleScreen.kt
 * LOKASI: screens/ScheduleScreen.kt
 *
 * Deskripsi:
 * Screen untuk menampilkan jadwal kuliah (placeholder)
 * Bisa dikembangkan lebih lanjut sesuai kebutuhan
 */

// Data class untuk jadwal
data class JadwalKuliah(
    val hari: String,
    val waktu: String,
    val mataKuliah: String,
    val dosen: String,
    val ruangan: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onNavigateBack: () -> Unit
) {
    val primaryGreen = Color(0xFF2E7D32)
    val lightGreen = Color(0xFF66BB6A)

    // Data jadwal real dari tabel (bisa diganti dengan data real)
    val jadwalList = remember {
        listOf(
            JadwalKuliah(
                hari = "Senin",
                waktu = "10:45 - 13:15",
                mataKuliah = "Interaksi Manusia dan Komputer",
                dosen = "Dian Hartanti, S.Kom., MMSI",
                ruangan = "UBJ-BKS || R. Said Soekanto || SS-409"
            ),
            JadwalKuliah(
                hari = "Senin",
                waktu = "13:30 - 16:00",
                mataKuliah = "Kecerdasan Buatan",
                dosen = "Hendarman Lubis, S.Kom., M.Kom.",
                ruangan = "UBJ-BKS || R. Said Soekanto || SS-419"
            ),
            JadwalKuliah(
                hari = "Selasa",
                waktu = "10:45 - 13:15",
                mataKuliah = "Pembelajaran Mesin",
                dosen = "Mukhlis, S.Kom, MT",
                ruangan = "UBJ-BKS || R. Said Soekanto || SS-408"
            ),
            JadwalKuliah(
                hari = "Rabu",
                waktu = "08:00 - 10:30",
                mataKuliah = "Keamanan Siber",
                dosen = "Asep Ramdhani Mahbub, S.Kom., M.Kom.",
                ruangan = "UBJ-BKS || R. Said Soekanto || SS-412"
            ),
            JadwalKuliah(
                hari = "Kamis",
                waktu = "08:00 - 09:40",
                mataKuliah = "Manajemen Sekuriti",
                dosen = "Ratna Salkiawati, S.T., M.Kom",
                ruangan = "UBJ-BKS || Grha Tanoto || W-105"
            ),
            JadwalKuliah(
                hari = "Kamis",
                waktu = "13:30 - 16:00",
                mataKuliah = "Pemrograman Perangkat Bergerak",
                dosen = "Arif Rifai Dwiyanto, ST., MTI",
                ruangan = "UBJ-BKS || Grha Tanoto || W-104"
            ),
            JadwalKuliah(
                hari = "Jumat",
                waktu = "08:00 - 10:30",
                mataKuliah = "Manajemen Proyek Perangkat Lunak",
                dosen = "M. Hadi Prayitno, S.Kom, M.Kom.",
                ruangan = "UBJ-BKS || R. Said Soekanto || SS-412"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jadwal Kuliah") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = lightGreen.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = primaryGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Semester Ganjil 2025/2026",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Total ${jadwalList.size} Mata Kuliah",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // List Jadwal
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(jadwalList) { jadwal ->
                    JadwalCard(jadwal)
                }
            }
        }
    }
}

@Composable
private fun JadwalCard(jadwal: JadwalKuliah) {
    val primaryGreen = Color(0xFF2E7D32)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header - Hari & Waktu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = primaryGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = jadwal.hari,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = primaryGreen
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = primaryGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = jadwal.waktu,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))

            // Mata Kuliah
            Text(
                text = jadwal.mataKuliah,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dosen
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = jadwal.dosen,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Ruangan
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = jadwal.ruangan,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * CATATAN IMPLEMENTASI:
 *
 * 1. Ini adalah screen placeholder untuk jadwal kuliah
 *
 * 2. Menggunakan dummy data static
 *
 * 3. Untuk production, bisa dikembangkan dengan:
 *    - Ambil data dari API/Database
 *    - Filtering berdasarkan hari
 *    - Notifikasi jadwal kuliah hari ini
 *    - Link ke Google Calendar
 *
 * 4. Bisa juga ditambahkan fitur:
 *    - Absensi langsung dari jadwal
 *    - Informasi tugas/quiz
 *    - Materi kuliah
 */