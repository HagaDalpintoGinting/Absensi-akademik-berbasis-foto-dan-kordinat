package id.ac.ubharajaya.sistemakademik

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import id.ac.ubharajaya.sistemakademik.ui.theme.SistemAkademikTheme
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/* ================= UTIL ================= */

fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
}

fun kirimKeN8n(
    context: ComponentActivity,
    latitude: Double,
    longitude: Double,
    foto: Bitmap
) {
    thread {
        try {
            val url = URL("https://n8n.lab.ubharajaya.ac.id/webhook/23c6993d-1792-48fb-ad1c-ffc78a3e6254")
// test URL            val url = URL("https://n8n.lab.ubharajaya.ac.id/webhook-test/23c6993d-1792-48fb-ad1c-ffc78a3e6254")
            val conn = url.openConnection() as HttpURLConnection

            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val json = JSONObject().apply {
                put("npm", "12345")
                put("nama","Arif R D")
                put("latitude", latitude)
                put("longitude", longitude)
                put("timestamp", System.currentTimeMillis())
                put("foto_base64", bitmapToBase64(foto))
            }

            conn.outputStream.use {
                it.write(json.toString().toByteArray())
            }

            val responseCode = conn.responseCode

            context.runOnUiThread {
                Toast.makeText(
                    context,
                    if (responseCode == 200)
                        "Absensi diterima server"
                    else
                        "Absensi ditolak server",
                    Toast.LENGTH_SHORT
                ).show()
            }

            conn.disconnect()

        } catch (_: Exception) {
            context.runOnUiThread {
                Toast.makeText(
                    context,
                    "Gagal kirim ke server",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

/* ================= ACTIVITY ================= */

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SistemAkademikTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AbsensiScreen(
                        modifier = Modifier.padding(innerPadding),
                        activity = this
                    )
                }
            }
        }
    }
}

/* ================= UI ================= */

@Composable
fun AbsensiScreen(
    modifier: Modifier = Modifier,
    activity: ComponentActivity
) {
    val context = LocalContext.current

    var lokasi by remember { mutableStateOf("Koordinat: -") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var foto by remember { mutableStateOf<Bitmap?>(null) }

    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    /* ===== Permission Lokasi ===== */

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {

                if (
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                latitude = location.latitude
                                longitude = location.longitude
                                lokasi =
                                    "Lat: ${location.latitude}\nLon: ${location.longitude}"
                            } else {
                                lokasi = "Lokasi tidak tersedia"
                            }
                        }
                        .addOnFailureListener {
                            lokasi = "Gagal mengambil lokasi"
                        }
                }

            } else {
                Toast.makeText(
                    context,
                    "Izin lokasi ditolak",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    /* ===== Kamera ===== */

    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap =
                    result.data?.extras?.getParcelable("data", Bitmap::class.java)
                if (bitmap != null) {
                    foto = bitmap
                    Toast.makeText(
                        context,
                        "Foto berhasil diambil",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                val intent =
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraLauncher.launch(intent)
            } else {
                Toast.makeText(
                    context,
                    "Izin kamera ditolak",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    /* ===== Request Awal ===== */

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    /* ===== UI ===== */

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Absensi Akademik",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = lokasi)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                cameraPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ambil Foto")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (latitude != null && longitude != null && foto != null) {
                    kirimKeN8n(
                        activity,
                        latitude!!,
                        longitude!!,
                        foto!!
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Lokasi atau foto belum lengkap",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kirim Absensi")
        }
    }
}
