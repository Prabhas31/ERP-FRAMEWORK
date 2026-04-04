package com.prabhas.emergencyphonerecovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class RecoverySetupActivity : AppCompatActivity() {

    private val userId = "owner_001"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery_setup)

        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirmPassword)
        val btnSave = findViewById<Button>(R.id.btnSavePassword)

        btnSave.setOnClickListener {
            val p1 = etPassword.text.toString()
            val p2 = etConfirm.text.toString()

            if (p1.length < 6) {
                toast("Password must be at least 6 characters")
                return@setOnClickListener
            }

            if (p1 != p2) {
                toast("Passwords do not match")
                return@setOnClickListener
            }

            savePassword(p1)
        }
    }

    private fun savePassword(password: String) {

        val hash = sha256(password)

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("recovery")
            .setValue(
                mapOf(
                    "password_hash" to hash,
                    "password_set" to true
                )
            )
            .addOnSuccessListener {

                // ✅ mark recovery locally
                SecurePrefs.setRecoveryPasswordSet(this, true)

                // ✅ go directly to Trusted Email setup
                startActivity(
                    Intent(
                        this,
                        TrustedEmailSetupActivity::class.java
                    )
                )

                finish()
            }
            .addOnFailureListener {
                toast("Failed to save password")
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
