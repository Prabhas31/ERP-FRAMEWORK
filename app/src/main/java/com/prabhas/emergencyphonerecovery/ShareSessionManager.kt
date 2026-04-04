package com.prabhas.emergencyphonerecovery

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

object ShareSessionManager {

    private const val USER_ID = "owner_001"

    /**
     * Called when Stolen Mode is activated
     * Creates ONE new share session
     * Cloud Function will send email ONLY once for this session
     */
    fun onStolenModeActivated(context: Context) {

        val sessionId = UUID.randomUUID()
            .toString()
            .replace("-", "")

        val sessionData = mapOf(
            "active" to true,
            "emailSent" to false,   // 🔑 REQUIRED for backend once-only email logic
            "createdAt" to System.currentTimeMillis()
        )

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(USER_ID)
            .child("share_sessions")
            .child(sessionId)
            .setValue(sessionData)
    }

    /**
     * Called when recovery is completed
     * Instantly expires ALL live links
     */
    fun onRecoveryCompleted() {

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(USER_ID)
            .child("share_sessions")
            .removeValue()
    }
}
