package com.alamin.pharma.utils

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

object ContactUtils {
    @Composable
    fun openWhatsApp(message: String) {
        val context = LocalContext.current
        val phone = "+967774973636"
        val url = "https://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    @Composable
    fun call(phone: String) {
        val context = LocalContext.current
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        context.startActivity(intent)
    }

    @Composable
    fun sendEmail(email: String) {
        val context = LocalContext.current
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
        context.startActivity(intent)
    }

    @Composable
    fun openLocation(address: String) {
        val context = LocalContext.current
        val uri = Uri.parse("geo:0,0?q=$address")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    @Composable
    fun openFacebook(url: String) {
        val context = LocalContext.current
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}
