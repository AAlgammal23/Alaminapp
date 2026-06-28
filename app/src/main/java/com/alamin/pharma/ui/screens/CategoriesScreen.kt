package com.alamin.pharma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alamin.pharma.ui.PharmacyViewModel
import com.alamin.pharma.ui.components.CategoryCard
import com.alamin.pharma.ui.components.ProductCard

@Composable
fun CategoriesScreen(
    viewModel: PharmacyViewModel = viewModel(),
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit
) {
    val categories = viewModel.categories.value
    val filteredProducts = viewModel.getFilteredProducts()

    Scaffold(
        topBar = { TopAppBar(title = { Text("الأقسام") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                items(categories.size) { index ->
                    CategoryCard(
                        category = categories[index],
                        onClick = { onCategoryClick(categories[index].id) },
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            Divider(modifier = Modifier.padding(8.dp))
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
