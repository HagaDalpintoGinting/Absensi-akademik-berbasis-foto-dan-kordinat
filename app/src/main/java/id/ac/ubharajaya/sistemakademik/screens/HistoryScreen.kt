package id.ac.ubharajaya.sistemakademik.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.ac.ubharajaya.sistemakademik.data.AbsensiEntity
import id.ac.ubharajaya.sistemakademik.data.AppDatabase
import id.ac.ubharajaya.sistemakademik.utils.LocationValidator

/**
 * FILE: HistoryScreen.kt
 * LOKASI: screens/HistoryScreen.kt
 *
 * Deskripsi:
 * Screen untuk melihat riwayat absensi mahasiswa
 * Menampilkan list semua absensi yang pernah dilakukan
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }

    // Get data absensi dari database
    val absensiList by database.absensiDao()
        .getAllAbsensi()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    // State untuk detail dialog
    var selectedAbsensi by remember { mutableStateOf<AbsensiEntity?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }

    val primaryGreen = Color(0xFF2E7D32)
    val lightGreen = Color(0xFF66BB6A)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Absensi") },
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

            // Header Stats
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatItem(
                        icon = Icons.Default.CheckCircle,
                        label = "Total Absensi",
                        value = "${absensiList.size}",
                        color = primaryGreen
                    )

                    Divider(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp)
                    )

                    StatItem(
                        icon = Icons.Default.CloudDone,
                        label = "Terkirim",
                        value = "${absensiList.count { it.statusKirim }}",
                        color = primaryGreen
                    )
                }
            }

            // List Absensi
            if (absensiList.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.HistoryToggleOff,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Belum ada riwayat absensi",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(absensiList) { absensi ->
                        AbsensiItem(
                            absensi = absensi,
                            onClick = {
                                selectedAbsensi = absensi
                                showDetailDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Detail Dialog
    if (showDetailDialog && selectedAbsensi != null) {
        AbsensiDetailDialog(
            absensi = selectedAbsensi!!,
            onDismiss = { showDetailDialog = false }
        )
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun AbsensiItem(
    absensi: AbsensiEntity,
    onClick: () -> Unit
) {
    val primaryGreen = Color(0xFF2E7D32)
    val errorRed = Color(0xFFD32F2F)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Status Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (absensi.lokasiValid)
                            primaryGreen.copy(alpha = 0.2f)
                        else
                            errorRed.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (absensi.lokasiValid)
                        Icons.Default.CheckCircle
                    else
                        Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (absensi.lokasiValid) primaryGreen else errorRed,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nama Mata Kuliah (BARU!)
                Text(
                    text = absensi.namaMatakuliah,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Hari & Tanggal
                Text(
                    text = "${absensi.hari}, ${absensi.tanggal}",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${absensi.waktuMatakuliah} • ${absensi.waktu}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Status badge
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (absensi.lokasiValid)
                            primaryGreen.copy(alpha = 0.15f)
                        else
                            errorRed.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (absensi.lokasiValid) "Valid" else "Invalid",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (absensi.lokasiValid) primaryGreen else errorRed
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (absensi.statusKirim) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Terkirim",
                            modifier = Modifier.size(16.dp),
                            tint = primaryGreen
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Belum terkirim",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Detail",
                tint = Color.Gray
            )
        }
    }
}

@Composable
private fun AbsensiDetailDialog(
    absensi: AbsensiEntity,
    onDismiss: () -> Unit
) {
    val primaryGreen = Color(0xFF2E7D32)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = primaryGreen
            )
        },
        title = {
            Text(
                text = "Detail Absensi",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                // Info Mata Kuliah (BARU!)
                Text(
                    text = "📚 Mata Kuliah",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                DetailRow("Kode", absensi.kodeMatakuliah)
                DetailRow("Nama MK", absensi.namaMatakuliah)
                DetailRow("Dosen", absensi.dosenMatakuliah)
                DetailRow("Ruangan", absensi.ruanganMatakuliah)
                DetailRow("Jam Kuliah", absensi.waktuMatakuliah)

                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                // Info Mahasiswa
                Text(
                    text = "👤 Mahasiswa",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                DetailRow("Nama", absensi.nama)
                DetailRow("NPM", absensi.npm)

                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                // Info Waktu & Lokasi
                Text(
                    text = "📍 Waktu & Lokasi",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                DetailRow("Hari", absensi.hari)
                DetailRow("Tanggal", absensi.tanggal)
                DetailRow("Waktu Absen", absensi.waktu)
                DetailRow("Jarak", LocationValidator.formatDistance(absensi.jarak))
                DetailRow("Status Lokasi", if (absensi.lokasiValid) "✓ Valid" else "✗ Invalid")
                DetailRow("Status Kirim", if (absensi.statusKirim) "✓ Terkirim" else "⌛ Pending")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("TUTUP", color = primaryGreen)
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * CATATAN IMPLEMENTASI:
 *
 * 1. Screen ini menampilkan list semua absensi dari Room Database
 *
 * 2. Menggunakan LazyColumn untuk efisiensi jika data banyak
 *
 * 3. Ada statistik singkat di atas (total & terkirim)
 *
 * 4. Click item untuk melihat detail lengkap
 *
 * 5. Data otomatis update karena menggunakan Flow dari Room
 */