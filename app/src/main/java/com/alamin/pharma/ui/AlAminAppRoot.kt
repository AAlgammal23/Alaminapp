package com.alamin.pharma.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alamin.pharma.ui.screens.*

@Composable
fun AlAminAppRoot() {
    val navController = rememberNavController()
    val viewModel: PharmacyViewModel = viewModel()  // ✅ إنشاء viewModel

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,  // ✅ تمرير viewModel
                onProductClick = { productId ->
                    navController.navigate("product/$productId")
                },
                onCategoryClick = { categoryId ->
                    navController.navigate("categories/$categoryId")
                },
                onSearchClick = {
                    navController.navigate("search")
                }
            )
        }

        composable("categories/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            CategoriesScreen(
                viewModel = viewModel,  // ✅ تمرير viewModel
                onProductClick = { productId ->
                    navController.navigate("product/$productId")
                },
                onCategoryClick = { id ->
                    navController.navigate("categories/$id")
                }
            )
        }

        composable("search") {
            SearchScreen(
                viewModel = viewModel,  // ✅ تمرير viewModel
                onProductClick = { productId ->
                    navController.navigate("product/$productId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("cart") {
            CartScreen(
                viewModel = viewModel,  // ✅ تمرير viewModel
                onBack = { navController.popBackStack() }
            )
        }

        composable("prescription") {
            PrescriptionScreen(
                viewModel = viewModel,  // ✅ تمرير viewModel
                onBack = { navController.popBackStack() }
            )
        }

        composable("consultation") {
            ConsultationScreen(onBack = { navController.popBackStack() })
        }

        composable("reminders") {
            MedicineReminderScreen(onBack = { navController.popBackStack() })
        }

        composable("account") {
            AccountScreen(
                viewModel = viewModel,  // ✅ تمرير viewModel
                onBack = { navController.popBackStack() }
            )
        }

        composable("product/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                viewModel = viewModel,  // ✅ تمرير viewModel
                onBack = { navController.popBackStack() }
            )
        }
    }
}
