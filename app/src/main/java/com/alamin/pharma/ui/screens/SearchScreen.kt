package com.alamin.pharma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alamin.pharma.ui.PharmacyViewModel
import com.alamin.pharma.ui.components.ProductCard
import com.alamin.pharma.ui.components.SearchBarComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: PharmacyViewModel = viewModel(),
    onProductClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val filteredProducts = viewModel.getFilteredProducts()
    val categories = viewModel.categories.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("البحث") },
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
        ) {
            SearchBarComponent(
                query = query,
                onQueryChange = { viewModel.setSearchQuery(it) },
                onSearch = { /* البحث التلقائي */ },
                modifier = Modifier.padding(8.dp)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                // التصحيح: استخدام items(categories)
                items(categories) { category ->
                    FilterChip(
                        selected = viewModel.selectedCategory.value == category.id,
                        onClick = {
                            if (viewModel.selectedCategory.value == category.id) {
                                viewModel.setSelectedCategory(null)
                            } else {
                                viewModel.setSelectedCategory(category.id)
                            }
                        },
                        label = { Text(category.name) },
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            if (filteredProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد منتجات", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // التصحيح: استخدام items(filteredProducts)
                    items(filteredProducts) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            onAddToCart = { viewModel.addToCart(product) },
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}
