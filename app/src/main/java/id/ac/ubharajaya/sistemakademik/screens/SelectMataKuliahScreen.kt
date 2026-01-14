package id.ac.ubharajaya.sistemakademik.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.ac.ubharajaya.sistemakademik.data.AppDatabase
import id.ac.ubharajaya.sistemakademik.data.MataKuliah
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * FILE: SelectMatakuliahScreen.kt
 * LOKASI: screens/SelectMatakuliahScreen.kt
 *
 * Deskripsi:
 * Screen untuk memilih mata kuliah yang akan diabsen
 * Menampilkan jadwal hari ini dan filter MK yang sudah diabsen
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectMatakuliahScreen(
    onNavigateBack: () -> Unit,
    onMatakuliahSelected: (MataKuliah) -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()

    val primaryGreen = Color(0xFF2E7D32)
    val lightGreen = Color(0xFF66BB6A)

    // Get hari ini
    val hariIni = remember {
        SimpleDateFormat("EEEE", Locale("id", "ID")).format(Date())
    }

    val tanggalHariIni = remember {
        SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date())
    }

    // Jadwal lengkap (hardcoded - bisa diganti dengan dari database)
    val semuaJadwal = remember {
        listOf(
            MataKuliah(
                kode = "INFO-3527",
                nama = "Pemrograman Mobile",
                dosen = "Dr. Ahmad Wijaya",
                ruangan = "Lab Komputer 1",
                waktu = "08:00 - 10:00",
                hari = "Senin",
                sks = 3
            ),
            MataKuliah(
                kode = "INFO-3528",
                nama = "Interaksi Manusia dan Komputer",
                dosen = "Dian Hartanti, S.Kom., MMSI",
                ruangan = "UBJ-BKS || R. Said Soekanto || SS-409",
                waktu = "10:45 - 13:15",
                hari = "Senin",
                sks = 3
            ),
            MataKuliah(
                kode = "INFO-3530",
                nama = "Kecerdasan Buatan",
                dosen = "Hendarman Lubis, S.Kom., M.Kom.",
                ruangan = "UBJ-BKS || R. Said Soekanto || SS-419",
                waktu = "13:30 - 16:00",
                hari = "Senin",
                sks = 3
            ),
            MataKuliah(
                kode = "INFO-3531",
                nama = "Pembelajaran Mesin",
                dosen = "Mukhlis, S.Kom, MT",
                ruangan = "UBJ-BKS || R. Said Soekanto || SS-408",
                waktu = "10:45 - 13:15",
                hari = "Selasa",
                sks = 3
            ),
            MataKuliah(
                kode = "INFO-3532",
                nama = "Keamanan Siber",
                dosen = "Asep Ramdhani Mahbub, S.Kom., M.Kom.",
                ruangan = "UBJ-BKS || R. Said Soekanto || SS-412",
                waktu = "08:00 - 10:30",
                hari = "Rabu",
                sks = 3
            ),
            MataKuliah(
                kode = "MKDU-2007",
                nama = "Manajemen Sekuriti",
                dosen = "Ratna Salkiawati, S.T., M.Kom",
                ruangan = "UBJ-BKS || Grha Tanoto || W-105",
                waktu = "08:00 - 09:40",
                hari = "Kamis",
                sks = 2
            ),
            MataKuliah(
                kode = "INFO-3529",
                nama = "Pemrograman Perangkat Bergerak",
                dosen = "Arif Rifai Dwiyanto, ST., MTI",
                ruangan = "UBJ-BKS || Grha Tanoto || W-104",
                waktu = "13:30 - 16:00",
                hari = "Kamis",
                sks = 3
            ),
            MataKuliah(
                kode = "INFO-3533",
                nama = "Manajemen Proyek Perangkat Lunak",
                dosen = "M. Hadi Prayitno, S.Kom, M.Kom.",
                ruangan = "UBJ-BKS || R. Said Soekanto || SS-412",
                waktu = "08:00 - 10:30",
                hari = "Jumat",
                sks = 3
            )
        )
    }

    // Filter jadwal hari ini
    val jadwalHariIni = semuaJadwal.filter { it.hari == hariIni }

    // Get absensi hari ini dari database
    val absensiHariIni by database.absensiDao()
        .getAbsensiHariIni(tanggalHariIni)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    // Kode MK yang sudah diabsen hari ini
    val kodeMkSudahAbsen = absensiHariIni.map { it.kodeMatakuliah }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Mata Kuliah") },
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

            // Header Info
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
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = primaryGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Jadwal Kuliah Hari Ini",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "$hariIni, $tanggalHariIni",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${jadwalHariIni.size} mata kuliah tersedia",
                            fontSize = 12.sp,
                            color = primaryGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // List Mata Kuliah
            if (jadwalHariIni.isEmpty()) {
                // Tidak ada jadwal hari ini
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tidak ada jadwal kuliah hari ini",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nikmati hari liburmu! 🎉",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(jadwalHariIni) { matakuliah ->
                        val sudahAbsen = kodeMkSudahAbsen.contains(matakuliah.kode)

                        MataKuliahCard(
                            mataKuliah = matakuliah,
                            sudahAbsen = sudahAbsen,
                            onClick = {
                                if (!sudahAbsen) {
                                    onMatakuliahSelected(matakuliah)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MataKuliahCard(
    mataKuliah: MataKuliah,
    sudahAbsen: Boolean,
    onClick: () -> Unit
) {
    val primaryGreen = Color(0xFF2E7D32)
    val lightGreen = Color(0xFF66BB6A)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !sudahAbsen, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (sudahAbsen) 1.dp else 3.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (sudahAbsen)
                Color(0xFFF5F5F5)
            else
                Color.White
        )
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header - Waktu & Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (sudahAbsen)
                            Color.Gray.copy(alpha = 0.2f)
                        else
                            primaryGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = mataKuliah.waktu,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (sudahAbsen) Color.Gray else primaryGreen
                        )
                    }

                    if (sudahAbsen) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Sudah Absen",
                                tint = primaryGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sudah Absen",
                                fontSize = 11.sp,
                                color = primaryGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Nama Mata Kuliah
                Text(
                    text = mataKuliah.nama,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (sudahAbsen) Color.Gray else Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Kode MK
                Text(
                    text = mataKuliah.kode,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                // Dosen
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (sudahAbsen) Color.Gray else Color(0xFF666666)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = mataKuliah.dosen,
                        fontSize = 13.sp,
                        color = if (sudahAbsen) Color.Gray else Color(0xFF666666)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Ruangan
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (sudahAbsen) Color.Gray else Color(0xFF666666)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = mataKuliah.ruangan,
                        fontSize = 13.sp,
                        color = if (sudahAbsen) Color.Gray else Color(0xFF666666),
                        maxLines = 1
                    )
                }

                if (!sudahAbsen) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Button Absen Sekarang
                    Button(
                        onClick = onClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryGreen
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ABSEN SEKARANG")
                    }
                }
            }

            // Overlay untuk card yang sudah diabsen
            if (sudahAbsen) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.White.copy(alpha = 0.3f))
                )
            }
        }
    }
}

/**
 * CATATAN IMPLEMENTASI:
 *
 * 1. Screen ini menampilkan jadwal kuliah HARI INI saja
 * 2. Filter otomatis MK yang sudah diabsen (disable & tampil checklist)
 * 3. Click card → navigasi ke AbsensiScreen dengan data MK
 * 4. Data jadwal hardcoded (bisa diganti dengan dari API/Database)
 * 5. Validasi sudah absen berdasarkan tanggal & kode MK
 */