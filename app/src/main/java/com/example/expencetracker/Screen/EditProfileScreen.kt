package com.example.expencetracker.Screen

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.expencetracker.ui.theme.Zinc
import com.example.expencetracker.viewmodel.AuthState
import com.example.expencetracker.viewmodel.AuthViewModel
import com.example.expencetracker.widget.ExpenceTextView

@Composable
fun EditProfileScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {
    val currentUser by viewModel.currentUser.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    var showImagePickerDialog by remember { mutableStateOf(false) }
    var showEmailDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    // For email update
    var newEmail by remember { mutableStateOf("") }
    var emailPassword by remember { mutableStateOf("") }

    // For password update
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileImageUri = it
            Toast.makeText(context, "Profile photo selected!", Toast.LENGTH_SHORT).show()
        }
    }

    // Camera launcher  
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            Toast.makeText(context, "Photo captured!", Toast.LENGTH_SHORT).show()
        }
    }

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle auth state
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.ProfileUpdated -> {
                Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                viewModel.resetAuthState()
            }

            is AuthState.EmailUpdated -> {
                Toast.makeText(context, "Email updated!", Toast.LENGTH_SHORT).show()
                email = newEmail
                showEmailDialog = false
                newEmail = ""
                emailPassword = ""
                viewModel.resetAuthState()
            }

            is AuthState.PasswordUpdated -> {
                Toast.makeText(context, "Password updated!", Toast.LENGTH_SHORT).show()
                showPasswordDialog = false
                currentPassword = ""
                newPassword = ""
                confirmNewPassword = ""
                viewModel.resetAuthState()
            }

            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG)
                    .show()
                viewModel.resetAuthState()
            }

            else -> {}
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF5F5F5))) {
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            // Header
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Zinc)) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    ExpenceTextView(
                        text = "Edit Profile",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture
                Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
                    if (profileImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(profileImageUri),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(Zinc),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                modifier = Modifier.size(80.dp),
                                tint = Color.White
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Zinc)
                            .align(Alignment.BottomEnd)
                            .clickable { showImagePickerDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Photo",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap to change photo",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.clickable { showImagePickerDialog = true })

                Spacer(modifier = Modifier.height(32.dp))

                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Zinc,
                        focusedLabelColor = Zinc
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email Field (Clickable)
                OutlinedTextField(
                    value = email,
                    onValueChange = {},
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                    trailingIcon = {
                        IconButton(onClick = { showEmailDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Email", tint = Zinc)
                        }
                    },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEmailDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Zinc,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Click to change email",
                    fontSize = 12.sp,
                    color = Zinc,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { showEmailDialog = true })

                Spacer(modifier = Modifier.height(16.dp))

                // Change Password Button
                OutlinedButton(
                    onClick = { showPasswordDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Zinc)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Change Password")
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Save Button
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            viewModel.updateProfile(name)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Zinc),
                    enabled = authState !is AuthState.Loading
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Save Changes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Image Picker Dialog
    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Choose Photo") },
            text = { Text("Select from gallery or take a photo") },
            confirmButton = {
                Column {
                    Button(
                        onClick = {
                            showImagePickerDialog = false; galleryLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Zinc)
                    ) {
                        Text("Gallery")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showImagePickerDialog = false
                            val permission = Manifest.permission.CAMERA
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    permission
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                cameraLauncher.launch(null)
                            } else {
                                cameraPermissionLauncher.launch(permission)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color.Gray)
                    ) {
                        Text("Camera")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImagePickerDialog = false
                }) { Text("Cancel") }
            }
        )
    }

    // Email Update Dialog
    if (showEmailDialog) {
        AlertDialog(
            onDismissRequest = { showEmailDialog = false; newEmail = ""; emailPassword = "" },
            title = { Text("Change Email") },
            text = {
                Column {
                    Text("Enter new email and current password")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("New Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = emailPassword,
                        onValueChange = { emailPassword = it },
                        label = { Text("Current Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    when {
                        newEmail.isBlank() -> Toast.makeText(
                            context,
                            "Enter email",
                            Toast.LENGTH_SHORT
                        ).show()

                        !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail)
                            .matches() -> Toast.makeText(
                            context,
                            "Invalid email",
                            Toast.LENGTH_SHORT
                        ).show()

                        emailPassword.isBlank() -> Toast.makeText(
                            context,
                            "Enter password",
                            Toast.LENGTH_SHORT
                        ).show()

                        else -> viewModel.updateEmail(newEmail, emailPassword)
                    }
                }, colors = ButtonDefaults.buttonColors(Zinc)) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEmailDialog = false; newEmail = ""; emailPassword = ""
                }) { Text("Cancel") }
            }
        )
    }

    // Password Update Dialog
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showPasswordDialog = false; currentPassword = ""; newPassword =
                ""; confirmNewPassword = ""
            },
            title = { Text("Change Password") },
            text = {
                Column {
                    Text("Enter current and new password")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    when {
                        currentPassword.isBlank() -> Toast.makeText(
                            context,
                            "Enter current password",
                            Toast.LENGTH_SHORT
                        ).show()

                        newPassword.isBlank() -> Toast.makeText(
                            context,
                            "Enter new password",
                            Toast.LENGTH_SHORT
                        ).show()

                        newPassword.length < 6 -> Toast.makeText(
                            context,
                            "Min 6 characters",
                            Toast.LENGTH_SHORT
                        ).show()

                        newPassword != confirmNewPassword -> Toast.makeText(
                            context,
                            "Passwords don't match",
                            Toast.LENGTH_SHORT
                        ).show()

                        else -> viewModel.updatePassword(currentPassword, newPassword)
                    }
                }, colors = ButtonDefaults.buttonColors(Zinc)) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPasswordDialog = false; currentPassword = ""; newPassword =
                    ""; confirmNewPassword = ""
                }) { Text("Cancel") }
            }
        )
    }
}
