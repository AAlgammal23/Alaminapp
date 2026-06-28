package com.alamin.pharma.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PharmacyRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    fun getProducts(): Flow<List<Product>> = flow {
        val snapshot = db.collection("pharmacy_products")
            .orderBy("createdAt")
            .get()
            .await()
        val products = snapshot.documents.mapNotNull { it.toObject<Product>() }
        emit(products)
    }

    suspend fun addProduct(product: Product) {
        db.collection("pharmacy_products").document(product.id).set(product).await()
    }

    suspend fun updateProduct(product: Product) {
        db.collection("pharmacy_products").document(product.id).set(product).await()
    }

    suspend fun deleteProduct(productId: String) {
        db.collection("pharmacy_products").document(productId).delete().await()
    }

    fun getCategories(): Flow<List<Category>> = flow {
        val snapshot = db.collection("pharmacy_categories")
            .orderBy("order")
            .get()
            .await()
        val categories = snapshot.documents.mapNotNull { it.toObject<Category>() }
        emit(categories)
    }

    fun getBanners(): Flow<List<Banner>> = flow {
        val snapshot = db.collection("pharmacy_banners")
            .whereEqualTo("active", true)
            .orderBy("order")
            .get()
            .await()
        val banners = snapshot.documents.mapNotNull { it.toObject<Banner>() }
        emit(banners)
    }

    fun getContactInfo(): Flow<ContactInfo> = flow {
        val snapshot = db.collection("pharmacy_contacts").document("info").get().await()
        val contact = snapshot.toObject<ContactInfo>() ?: ContactInfo()
        emit(contact)
    }

    suspend fun updateContactInfo(contact: ContactInfo) {
        db.collection("pharmacy_contacts").document("info").set(contact).await()
    }

    suspend fun uploadImage(imageBytes: ByteArray, path: String): String {
        val ref = storage.child(path)
        ref.putBytes(imageBytes).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun loginAdmin(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isAdminLoggedIn(): Boolean = auth.currentUser != null
}
