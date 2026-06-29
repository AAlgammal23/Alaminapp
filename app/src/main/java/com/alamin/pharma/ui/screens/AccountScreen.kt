package com.alamin.pharma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alamin.pharma.ui.PharmacyViewModel
import com.alamin.pharma.utils.ContactUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: PharmacyViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val contact = viewModel.contactInfo.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الحساب") },
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
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "صورة المستخدم",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("صيدلية الأمين الحديثة", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            InfoItem(icon = Icons.Default.Phone, text = contact.phone) {
                ContactUtils.call(context, contact.phone)
            }
            InfoItem(icon = Icons.Default.Message, text = "واتساب") {
                ContactUtils.openWhatsApp(context, "")
            }
            InfoItem(icon = Icons.Default.Email, text = contact.email) {
                ContactUtils.sendEmail(context, contact.email)
            }
            InfoItem(icon = Icons.Default.LocationOn, text = contact.address) {
                ContactUtils.openLocation(context, contact.address)
            }
            InfoItem(icon = Icons.Default.ThumbUp, text = "فيسبوك") {
                ContactUtils.openFacebook(context, contact.facebook)
            }
            InfoItem(icon = Icons.Default.Schedule, text = contact.workingHours) {}
        }
    }
}

@Composable
fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
