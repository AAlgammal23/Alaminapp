package com.alamin.pharma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.alamin.pharma.ui.components.SearchBar

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
            SearchBar(
                query = query,
                onQueryChange = { viewModel.setSearchQuery(it) },
                onSearch = { /* تلقائي */ },
                modifier = Modifier.padding(8.dp)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(categories.size) { index ->
                    FilterChip(
                        selected = viewModel.selectedCategory.value == categories[index].id,
                        onClick = {
                            if (viewModel.selectedCategory.value == categories[index].id) {
                                viewModel.setSelectedCategory(null)
                            } else {
                                viewModel.setSelectedCategory(categories[index].id)
                            }
                        },
                        label = { Text(categories[index].name) },
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
                    items(filteredProducts.size) { index ->
                        ProductCard(
                            product = filteredProducts[index],
                            onClick = { onProductClick(filteredProducts[index].id) },
                            onAddToCart = { viewModel.addToCart(filteredProducts[index]) },
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}
