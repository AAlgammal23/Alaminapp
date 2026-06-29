package com.alamin.pharma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alamin.pharma.ui.PharmacyViewModel
import com.alamin.pharma.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PharmacyViewModel = viewModel(),
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    val products = viewModel.products.value
    val categories = viewModel.categories.value
    val banners = viewModel.banners.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("صيدلية الأمين الحديثة") },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "بحث")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { GridItemSpan(2) }) {
                BannerCarousel(banners, modifier = Modifier.padding(8.dp))
            }
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "الأقسام",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(categories) { category ->
                        CategoryCard(
                            category = category,
                            onClick = { onCategoryClick(category.id) },
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "منتجات مميزة",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
            items(products.take(10)) { product ->
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
