package com.alamin.pharma.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alamin.pharma.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PharmacyViewModel(
    private val repository: PharmacyRepository = PharmacyRepository()
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    private val _contactInfo = MutableStateFlow(ContactInfo())
    val contactInfo: StateFlow<ContactInfo> = _contactInfo.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getProducts().collect { _products.value = it }
                repository.getCategories().collect { _categories.value = it }
                repository.getBanners().collect { _banners.value = it }
                repository.getContactInfo().collect { _contactInfo.value = it }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart(product: Product) {
        val currentCart = _cart.value.toMutableList()
        val existing = currentCart.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity++
        } else {
            currentCart.add(CartItem(product))
        }
        _cart.value = currentCart
    }

    fun removeFromCart(productId: String) {
        _cart.value = _cart.value.filter { it.product.id != productId }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        val currentCart = _cart.value.toMutableList()
        val item = currentCart.find { it.product.id == productId }
        if (item != null) {
            if (quantity <= 0) {
                currentCart.remove(item)
            } else {
                item.quantity = quantity
            }
            _cart.value = currentCart
        }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    fun getCartTotal(): Double {
        return _cart.value.sumOf { it.product.price * it.quantity }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(categoryId: String?) {
        _selectedCategory.value = categoryId
    }

    fun getFilteredProducts(): List<Product> {
        val query = _searchQuery.value.lowercase()
        val category = _selectedCategory.value
        return _products.value.filter { product ->
            val matchesQuery = query.isEmpty() || product.name.lowercase().contains(query)
            val matchesCategory = category == null || product.category == category
            matchesQuery && matchesCategory
        }
    }

    suspend fun uploadPrescription(imageBytes: ByteArray, notes: String): Boolean {
        return try {
            val imageUrl = repository.uploadImage(imageBytes, "prescriptions/${System.currentTimeMillis()}.jpg")
            true
        } catch (e: Exception) {
            false
        }
    }
}
