package com.prabhas.emergencyphonerecovery

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class EmergencyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Log.d("FIREBASE_INIT", "Firebase initialized")
    }
}
