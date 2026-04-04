package com.prabhas.emergencyphonerecovery

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

object ShareSessionManager {

    fun onStolenModeActivated(context: Context) {

        val userId = SecurePrefs.getUserId(context)

        val sessionId = UUID.randomUUID()
            .toString()
            .replace("-", "")

        val sessionData = mapOf(
            "active" to true,
            "emailSent" to false,
            "createdAt" to System.currentTimeMillis()
        )

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("share_sessions")
            .child(sessionId)
            .setValue(sessionData)
    }

    fun onRecoveryCompleted(context: Context) {

        val userId = SecurePrefs.getUserId(context)

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("share_sessions")
            .removeValue()
    }
}
// git test