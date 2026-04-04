package com.prabhas.emergencyphonerecovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class VerifyRecoveryPasswordActivity : AppCompatActivity() {

    private val userId = "owner_001"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_recovery_password)

        val etPassword = findViewById<EditText>(R.id.etRecoveryPassword)
        val btnVerify = findViewById<Button>(R.id.btnVerifyPassword)
        val tvForgot = findViewById<TextView>(R.id.tvForgotRecovery)

        btnVerify.setOnClickListener {

            val input = etPassword.text.toString().trim()

            if (input.isEmpty()) {
                toast("Enter recovery password")
                return@setOnClickListener
            }

            verifyPassword(input)
        }

        tvForgot.setOnClickListener {
            startActivity(
                Intent(this, PhysicalKeyVerifyActivity::class.java)
                    .putExtra("TARGET", intent.getStringExtra("TARGET"))
            )
            finish()
        }
    }

    private fun verifyPassword(input: String) {

        val target =
            intent.getStringExtra("TARGET") ?: "FULL_RECOVERY"

        val userRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)

        userRef.child("recovery")
            .child("password_hash")
            .get()
            .addOnSuccessListener { snap ->

                val savedHash = snap.getValue(String::class.java)

                if (savedHash == null || sha256(input) != savedHash) {
                    toast("Wrong recovery password")
                    return@addOnSuccessListener
                }

                // ✅ PASSWORD VERIFIED — ROUTE CORRECTLY
                when (target) {

                    "MANAGE_TRUSTED_EMAILS" -> {
                        startActivity(
                            Intent(this, ManageTrustedEmailsActivity::class.java)
                        )
                        finish()
                    }

                    "VIEW_PHYSICAL_KEY" -> {
                        startActivity(
                            Intent(this, PhysicalRecoveryKeyActivity::class.java)
                        )
                        finish()
                    }

                    "RESET_RECOVERY_PASSWORD" -> {
                        startActivity(
                            Intent(this, ResetRecoveryPasswordActivity::class.java)
                        )
                        finish()
                    }

                    else -> {
                        // 🔓 FULL RECOVERY FLOW

                        // 1️⃣ EXIT STOLEN MODE (prefs + services)
                        StolenModeManager.deactivate(this)

                        // 2️⃣ RESET FAILED ATTEMPTS
                        SecurePrefs.resetFailedAttempts(this)

                        // 3️⃣ STOP SCREEN PINNING (CRITICAL FIX)
                        try {
                            stopLockTask()
                        } catch (e: Exception) {
                            // ignore
                        }

                        // 4️⃣ CLEAN CLOUD DATA
                        userRef.child("live_location").removeValue()
                        userRef.child("share_sessions").removeValue()

                        Toast.makeText(
                            this,
                            "Phone recovered successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        // 5️⃣ RETURN TO DASHBOARD CLEANLY
                        startActivity(
                            Intent(this, MainActivity::class.java)
                                .addFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                                )
                        )
                        finish()
                    }
                }
            }
    }

    private fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
