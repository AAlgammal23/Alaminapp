package com.alamin.pharma.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val description: String = "",
    val isOffer: Boolean = false,
    val offerPrice: Double = 0.0,
    val createdAt: Date = Date()
) : Parcelable

@Parcelize
data class Category(
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    val order: Int = 0
) : Parcelable

@Parcelize
data class Banner(
    val id: String = "",
    val imageUrl: String = "",
    val title: String = "",
    val link: String = "",
    val active: Boolean = true,
    val order: Int = 0
) : Parcelable

@Parcelize
data class ContactInfo(
    val whatsapp: String = "+967774973636",
    val phone: String = "+967774973636",
    val email: String = "alaminmodern.ph@gmail.com",
    val address: String = "اليمن - إب - مدينة القاعدة",
    val facebook: String = "https://www.facebook.com/share/18BNE6VzVK/",
    val workingHours: String = "يومياً 8 ص - 11 م"
) : Parcelable

data class CartItem(
    val product: Product,
    var quantity: Int = 1
)

@Parcelize
data class MedicineReminder(
    val id: String = "",
    val name: String = "",
    val time: String = "",
    val days: List<Int> = listOf(1,2,3,4,5,6,7),
    val isActive: Boolean = true
) : Parcelable
