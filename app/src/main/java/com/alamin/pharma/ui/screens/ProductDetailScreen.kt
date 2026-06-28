package com.alamin.pharma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alamin.pharma.ui.PharmacyViewModel

@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: PharmacyViewModel = viewModel(),
    onBack: () -> Unit
) {
    val product = viewModel.products.value.find { it.id == productId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تفاصيل المنتج") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("المنتج غير موجود")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // يمكن إضافة صورة كبيرة وأسعار ووصف
                Text(product.name, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text("السعر: ${product.price} ريال", style = MaterialTheme.typography.bodyLarge)
                if (product.isOffer) {
                    Text("عرض: ${product.offerPrice} ريال", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(product.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.addToCart(product) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("أضف للسلة")
                }
            }
        }
    }
}
