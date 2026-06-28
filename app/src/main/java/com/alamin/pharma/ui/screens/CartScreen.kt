package com.alamin.pharma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alamin.pharma.data.CartItem
import com.alamin.pharma.ui.PharmacyViewModel
import com.alamin.pharma.utils.ContactUtils

@Composable
fun CartScreen(
    viewModel: PharmacyViewModel = viewModel(),
    onBack: () -> Unit
) {
    val cartItems = viewModel.cart.value
    val total = viewModel.getCartTotal()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("السلة") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الإجمالي: $total ريال",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(
                            onClick = {
                                val message = buildOrderMessage(cartItems)
                                ContactUtils.openWhatsApp(message)
                                viewModel.clearCart()
                            }
                        ) {
                            Text("إرسال الطلب")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("السلة فارغة", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onQuantityChange = { newQuantity ->
                            viewModel.updateQuantity(item.product.id, newQuantity)
                        },
                        onRemove = {
                            viewModel.removeFromCart(item.product.id)
                        }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(item.product.name, style = MaterialTheme.typography.bodyMedium)
            Text("${item.product.price} ريال", style = MaterialTheme.typography.bodySmall)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onQuantityChange(item.quantity - 1) }) {
                Icon(Icons.Default.Remove, contentDescription = "نقص")
            }
            Text(item.quantity.toString())
            IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
                Icon(Icons.Default.Add, contentDescription = "زيادة")
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "حذف")
            }
        }
    }
}

fun buildOrderMessage(cartItems: List<CartItem>): String {
    val itemsText = cartItems.joinToString("\n") {
        "${it.product.name} × ${it.quantity} = ${it.product.price * it.quantity} ريال"
    }
    val total = cartItems.sumOf { it.product.price * it.quantity }
    return "طلب جديد:\n$itemsText\nالإجمالي: $total ريال"
}
