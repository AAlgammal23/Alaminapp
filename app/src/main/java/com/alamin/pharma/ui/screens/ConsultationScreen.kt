package com.alamin.pharma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alamin.pharma.utils.ContactUtils

@Composable
fun ConsultationScreen(
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var question by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("استشارة طبية") },
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("الاسم") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("رقم الهاتف (اختياري)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                label = { Text("استفسارك الطبي") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                minLines = 5
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val message = buildString {
                        append("استشارة طبية جديدة:\n")
                        append("الاسم: $name\n")
                        if (phone.isNotEmpty()) append("الهاتف: $phone\n")
                        append("السؤال:\n$question")
                    }
                    ContactUtils.openWhatsApp(message)
                    name = ""
                    phone = ""
                    question = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("إرسال الاستشارة")
            }
        }
    }
}
