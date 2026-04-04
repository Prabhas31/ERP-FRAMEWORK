package com.prabhas.emergencyphonerecovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class VerifyRecoveryPasswordActivity : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ FIX: use correct dynamic userId
        userId = SecurePrefs.getUserId(this)

        println("🔥 VERIFY USER ID (MAIN VERIFY): $userId")

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

        val hash = HashUtils.sha256(input.trim())

        userRef.child("recovery")
            .child("password_hash")
            .get()
            .addOnSuccessListener { snap ->

                val savedHash = snap.getValue(String::class.java)

                println("🔥 ENTERED HASH: $hash")
                println("🔥 STORED HASH: $savedHash")

                if (savedHash == null || hash != savedHash) {
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

                        StolenModeManager.deactivate(this)
                        SecurePrefs.resetFailedAttempts(this)

                        try {
                            stopLockTask()
                        } catch (e: Exception) {}

                        userRef.child("live_location").removeValue()
                        userRef.child("share_sessions").removeValue()

                        Toast.makeText(
                            this,
                            "Phone recovered successfully",
                            Toast.LENGTH_LONG
                        ).show()

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

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
// git test