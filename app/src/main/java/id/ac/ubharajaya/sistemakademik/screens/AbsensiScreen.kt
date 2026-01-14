package id.ac.ubharajaya.sistemakademik.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import id.ac.ubharajaya.sistemakademik.data.AbsensiEntity
import id.ac.ubharajaya.sistemakademik.data.AppDatabase
import id.ac.ubharajaya.sistemakademik.data.UserPreferences
import id.ac.ubharajaya.sistemakademik.utils.Constants
import id.ac.ubharajaya.sistemakademik.utils.LocationValidator
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

/**
 * FILE: AbsensiScreen.kt
 * LOKASI: screens/AbsensiScreen.kt
 *
 * Deskripsi:
 * Screen untuk melakukan absensi dengan:
 * - Deteksi lokasi GPS
 * - Validasi radius (harus di dalam area kampus)
 * - Pengambilan foto selfie
 * - Preview foto sebelum kirim
 * - Kirim data ke webhook
 * - Simpan ke database lokal
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsensiScreen(
    mataKuliah: id.ac.ubharajaya.sistemakademik.data.MataKuliah,  // Parameter BARU!
    onNavigateToSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferences = remember { UserPreferences(context) }
    val database = remember { AppDatabase.getDatabase(context) }

    // Get user data
    val userData by userPreferences.userData.collectAsStateWithLifecycle(
        initialValue = UserPreferences.UserData("", "", false)
    )

    // State
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var isLocationValid by remember { mutableStateOf(false) }
    var distance by remember { mutableStateOf(0.0) }
    var foto by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf(Constants.MSG_MENUNGGU_LOKASI) }

    // Warna
    val primaryGreen = Color(0xFF2E7D32)
    val lightGreen = Color(0xFF66BB6A)
    val errorRed = Color(0xFFD32F2F)

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Permission Launchers
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            getLocation(
                fusedLocationClient = fusedLocationClient,
                context = context,
                onSuccess = { lat, lon ->
                    latitude = lat
                    longitude = lon

                    // Validasi lokasi
                    val (valid, dist) = LocationValidator.isLocationValid(lat, lon)
                    isLocationValid = valid
                    distance = dist
                    statusMessage = LocationValidator.getStatusMessage(valid, dist)
                },
                onFailure = {
                    statusMessage = "Gagal mengambil lokasi"
                    Toast.makeText(context, "Gagal mengambil lokasi", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(context, "Izin lokasi ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.getParcelable("data", Bitmap::class.java)
            if (bitmap != null) {
                foto = bitmap
                Toast.makeText(context, "Foto berhasil diambil", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        } else {
            Toast.makeText(context, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    // Auto request location on start
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Absen Kehadiran",
                            fontSize = 16.sp
                        )
                        Text(
                            text = mataKuliah.nama,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // Info Mata Kuliah Card (BARU!)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = primaryGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mataKuliah.nama,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${mataKuliah.kode} • ${mataKuliah.waktu}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = mataKuliah.dosen,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = mataKuliah.ruangan,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Lokasi Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isLocationValid)
                        lightGreen.copy(alpha = 0.2f)
                    else
                        errorRed.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isLocationValid)
                                Icons.Default.CheckCircle
                            else
                                Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isLocationValid) primaryGreen else errorRed,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (latitude != null)
                                    "Cek Lokasi: ${if (isLocationValid) "Dalam Area Absensi ✓" else "Di Luar Area Absensi ✗"}"
                                else
                                    "Mendeteksi Lokasi...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isLocationValid) primaryGreen else errorRed
                            )

                            if (latitude != null && longitude != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Jarak: ${LocationValidator.formatDistance(distance)}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    if (latitude != null && longitude != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Koordinat Anda:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Lat: ${"%.6f".format(latitude)}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Lon: ${"%.6f".format(longitude)}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ambil Foto Section
            Text(
                text = "Ambil Foto Selfie",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Preview Foto
            if (foto != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            bitmap = foto!!.asImageBitmap(),
                            contentDescription = "Preview Foto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Icon check di corner
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = primaryGreen,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            } else {
                // Placeholder jika belum ada foto
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Belum ada foto",
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Button Ambil Foto
            Button(
                onClick = {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (foto != null) lightGreen else primaryGreen
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (foto != null) "Ambil Ulang Foto" else "AMBIL FOTO")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Button Kirim Absensi
            Button(
                onClick = {
                    if (latitude == null || longitude == null) {
                        Toast.makeText(context, "Menunggu data lokasi...", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!isLocationValid) {
                        Toast.makeText(
                            context,
                            "Anda di luar area kampus! Jarak: ${LocationValidator.formatDistance(distance)}",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }

                    if (foto == null) {
                        Toast.makeText(context, Constants.MSG_FOTO_BELUM_DIAMBIL, Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Semua validasi OK, kirim absensi
                    isLoading = true

                    scope.launch {
                        try {
                            // Siapkan data absensi
                            val calendar = Calendar.getInstance()
                            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                            val timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
                            val dayFormat = SimpleDateFormat("EEEE", Locale("id", "ID"))

                            val absensiEntity = AbsensiEntity(
                                npm = userData.npm.ifEmpty { Constants.NPM },
                                nama = userData.nama.ifEmpty { Constants.NAMA },
                                // Data Mata Kuliah (BARU!)
                                kodeMatakuliah = mataKuliah.kode,
                                namaMatakuliah = mataKuliah.nama,
                                dosenMatakuliah = mataKuliah.dosen,
                                ruanganMatakuliah = mataKuliah.ruangan,
                                waktuMatakuliah = mataKuliah.waktu,
                                // Data Lokasi
                                latitude = latitude!!,
                                longitude = longitude!!,
                                jarak = distance,
                                lokasiValid = isLocationValid,
                                timestamp = System.currentTimeMillis(),
                                tanggal = dateFormat.format(calendar.time),
                                waktu = "${timeFormat.format(calendar.time)} WIB",
                                hari = dayFormat.format(calendar.time),
                                fotoBase64 = bitmapToBase64(foto!!),
                                statusKirim = false
                            )

                            // Simpan ke database lokal
                            val id = database.absensiDao().insert(absensiEntity)

                            // Kirim ke webhook
                            kirimKeWebhook(
                                context = context,
                                absensi = absensiEntity.copy(id = id.toInt()),
                                onSuccess = {
                                    scope.launch {
                                        // Update status kirim di database
                                        database.absensiDao().updateStatusKirim(id.toInt(), true)

                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "Absensi berhasil dicatat!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onNavigateToSuccess()
                                    }
                                },
                                onFailure = { error ->
                                    scope.launch {
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "Data tersimpan lokal. Error: $error",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        onNavigateToSuccess()
                                    }
                                }
                            )

                        } catch (e: Exception) {
                            isLoading = false
                            Toast.makeText(
                                context,
                                "Error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryGreen,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading && isLocationValid && foto != null
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KIRIM ABSENSI",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info validasi
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFFF6F00),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pastikan Anda berada dalam radius ${Constants.RADIUS_METER.toInt()}m dari kampus",
                        fontSize = 12.sp,
                        color = Color(0xFFE65100)
                    )
                }
            }
        }
    }
}

// Helper Functions

private fun getLocation(
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    context: android.content.Context,
    onSuccess: (Double, Double) -> Unit,
    onFailure: () -> Unit
) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onSuccess(location.latitude, location.longitude)
                } else {
                    onFailure()
                }
            }
            .addOnFailureListener {
                onFailure()
            }
    }
}

private fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
}

private fun kirimKeWebhook(
    context: android.content.Context,
    absensi: AbsensiEntity,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    thread {
        try {
            val url = URL(Constants.WEBHOOK_URL_ACTIVE)
            val conn = url.openConnection() as HttpURLConnection

            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 10000
            conn.readTimeout = 10000

            val json = JSONObject().apply {
                put("npm", absensi.npm)
                put("nama", absensi.nama)
                put("latitude", absensi.latitude)
                put("longitude", absensi.longitude)
                put("jarak", absensi.jarak)
                put("timestamp", absensi.timestamp)
                put("tanggal", absensi.tanggal)
                put("waktu", absensi.waktu)
                put("foto_base64", absensi.fotoBase64)
            }

            conn.outputStream.use {
                it.write(json.toString().toByteArray())
            }

            val responseCode = conn.responseCode
            conn.disconnect()

            if (responseCode == 200 || responseCode == 201) {
                onSuccess()
            } else {
                onFailure("HTTP $responseCode")
            }

        } catch (e: Exception) {
            onFailure(e.message ?: "Unknown error")
        }
    }
}