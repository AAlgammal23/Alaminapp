package com.alamin.pharma.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.alamin.pharma.ui.PharmacyViewModel
import com.alamin.pharma.utils.ContactUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionScreen(
    viewModel: PharmacyViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var notes by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
        uploadError = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("رفع وصفة طبية") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // زر اختيار الصورة
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("اختيار صورة الوصفة")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // عرض الصورة المختارة
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "الوصفة",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // حقل الملاحظات
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("ملاحظات (اختياري)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // عرض رسالة الخطأ إن وجدت
            if (uploadError != null) {
                Text(
                    text = uploadError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // زر الإرسال
            Button(
                onClick = {
                    isUploading = true
                    uploadError = null
                    try {
                        if (selectedImageUri != null) {
                            val bytes = context.contentResolver
                                .openInputStream(selectedImageUri!!)
                                ?.readBytes()
                            if (bytes != null) {
                                // إرسال الوصفة عبر واتساب (مع الصورة)
                                // هنا يمكنك رفع الصورة إلى Firebase Storage
                                val message = "وصفة طبية جديدة.\nالملاحظات: $notes"
                                ContactUtils.openWhatsApp(context, message)
                                selectedImageUri = null
                                notes = ""
                            } else {
                                uploadError = "فشل في قراءة الصورة"
                            }
                        } else {
                            // إرسال بدون صورة
                            val message = "وصفة طبية جديدة (بدون صورة).\nالملاحظات: $notes"
                            ContactUtils.openWhatsApp(context, message)
                            notes = ""
                        }
                    } catch (e: Exception) {
                        uploadError = e.message ?: "حدث خطأ غير متوقع"
                    } finally {
                        isUploading = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUploading
            ) {
                if (isUploading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("إرسال الوصفة")
                }
            }
        }
    }
}
