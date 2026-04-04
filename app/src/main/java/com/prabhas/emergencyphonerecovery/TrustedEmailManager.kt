package com.prabhas.emergencyphonerecovery

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.firebase.database.FirebaseDatabase

object TrustedEmailManager {

    private const val OWNER_ID = "owner_001"

    // =================================================
    // 🔹 EXISTING FUNCTION (DO NOT CHANGE)
    // =================================================
    fun sendStolenAlert(
        context: Context,
        liveLink: String
    ) {

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(OWNER_ID)
            .child("trusted_emails")
            .get()
            .addOnSuccessListener { snapshot ->

                if (!snapshot.exists()) return@addOnSuccessListener

                val emails = mutableListOf<String>()

                snapshot.children.forEach {
                    val email = it.getValue(String::class.java)
                    if (!email.isNullOrEmpty()) {
                        emails.add(email)
                    }
                }

                if (emails.isEmpty()) return@addOnSuccessListener

                val subject = "🚨 Phone Stolen Alert"
                val body = """
Your phone has been marked as STOLEN.

📍 Live Location:
$liveLink

This link will automatically stop working once the phone is recovered.

— Emergency Phone Recovery
""".trimIndent()

                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, emails.toTypedArray())
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, body)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                context.startActivity(intent)
            }
    }

    // =================================================
    // 🔹 NEW ADDITION (SAFE, READ-ONLY)
    // Used ONLY for StartupGateActivity
    // =================================================
    fun hasAnyTrustedEmail(
        context: Context,
        onResult: (Boolean) -> Unit
    ) {
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(OWNER_ID)
            .child("trusted_emails")
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(snapshot.exists() && snapshot.childrenCount > 0)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }
}
// git test