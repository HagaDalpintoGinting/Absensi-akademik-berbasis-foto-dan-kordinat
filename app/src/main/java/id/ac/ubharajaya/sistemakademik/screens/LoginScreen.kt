package id.ac.ubharajaya.sistemakademik.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.ac.ubharajaya.sistemakademik.data.UserPreferences
import id.ac.ubharajaya.sistemakademik.utils.Constants
import kotlinx.coroutines.launch

/**
 * FILE: LoginScreen.kt
 * LOKASI: screens/LoginScreen.kt
 *
 * Deskripsi:
 * Screen untuk login mahasiswa
 * Input: NPM dan Password
 * Validasi sederhana: NPM harus sesuai dengan Constants.NPM
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferences = remember { UserPreferences(context) }

    // State untuk form input
    var npm by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Warna tema hijau akademik
    val primaryGreen = Color(0xFF2E7D32)
    val lightGreen = Color(0xFF66BB6A)

    // Check jika sudah login sebelumnya
    val userData by userPreferences.userData.collectAsStateWithLifecycle(
        initialValue = UserPreferences.UserData("", "", false)
    )

    LaunchedEffect(userData.isLoggedIn) {
        if (userData.isLoggedIn) {
            onLoginSuccess()
        }
    }

    // UI
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

            // Logo/Icon Graduation Cap
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp),
                tint = primaryGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Absensi Akademik",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = primaryGreen
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = Constants.KAMPUS_NAMA,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Input NPM
            OutlinedTextField(
                value = npm,
                onValueChange = {
                    npm = it
                    errorMessage = ""
                },
                label = { Text("NPM") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryGreen,
                    focusedLabelColor = primaryGreen
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input Password
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible)
                                "Hide password"
                            else
                                "Show password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryGreen,
                    focusedLabelColor = primaryGreen
                )
            )

            // Error Message
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Button Login
            Button(
                onClick = {
                    // Validasi input
                    when {
                        npm.isEmpty() || password.isEmpty() -> {
                            errorMessage = "NPM dan Password harus diisi"
                        }
                        npm != Constants.NPM -> {
                            errorMessage = "NPM tidak valid"
                        }
                        password.length < 4 -> {
                            errorMessage = "Password minimal 4 karakter"
                        }
                        else -> {
                            // Login berhasil
                            isLoading = true
                            scope.launch {
                                userPreferences.saveUserData(
                                    npm = Constants.NPM,
                                    nama = Constants.NAMA
                                )
                                isLoading = false
                                onLoginSuccess()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryGreen
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "LOGIN",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Forgot Password (Optional - bisa dihapus)
            TextButton(onClick = {
                // TODO: Implement forgot password
            }) {
                Text(
                    text = "Forgot Password?",
                    color = primaryGreen
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Info untuk testing
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = lightGreen.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "ℹ️ Info Login",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "NPM: ${Constants.NPM}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Password: Bebas (minimal 4 karakter)",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * CATATAN IMPLEMENTASI:
 *
 * 1. Screen ini melakukan validasi sederhana:
 *    - NPM harus sesuai dengan Constants.NPM
 *    - Password minimal 4 karakter (tidak ada validasi server)
 *
 * 2. Data login disimpan di DataStore Preferences
 *
 * 3. Jika sudah pernah login, langsung masuk ke Dashboard
 *
 * 4. Untuk production, sebaiknya:
 *    - Tambahkan enkripsi password
 *    - Validasi ke server/database
 *    - Implementasi forgot password yang proper
 */