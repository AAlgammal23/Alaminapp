package com.alamin.pharma

import android.app.Application
import com.google.firebase.FirebaseApp

class AlAminApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
