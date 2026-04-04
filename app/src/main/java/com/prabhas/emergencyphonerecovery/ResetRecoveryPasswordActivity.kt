package com.prabhas.emergencyphonerecovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class ResetRecoveryPasswordActivity : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ FIX: use correct userId
        userId = SecurePrefs.getUserId(this)

        setContentView(R.layout.activity_reset_recovery_password)

        val etNew = findViewById<EditText>(R.id.etNewPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirmNewPassword)
        val btnReset = findViewById<Button>(R.id.btnResetPassword)

        val recoveryRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("recovery")

        btnReset.setOnClickListener {

            val newPw = etNew.text.toString().trim()
            val confirmPw = etConfirm.text.toString().trim()

            if (newPw.isEmpty() || confirmPw.isEmpty()) {
                toast("All fields are required")
                return@setOnClickListener
            }

            if (newPw.length < 6) {
                toast("Password must be at least 6 characters")
                return@setOnClickListener
            }

            if (newPw != confirmPw) {
                toast("Passwords do not match")
                return@setOnClickListener
            }

            val hash = HashUtils.sha256(newPw)

            println("🔥 UPDATE USER ID: $userId")
            println("🔥 NEW HASH: $hash")

            // ✅ FIX: use updateChildren (safe update)
            recoveryRef.updateChildren(
                mapOf(
                    "password_hash" to hash,
                    "password_set" to true
                )
            )
                .addOnSuccessListener {

                    toast("Recovery password updated successfully")

                    startActivity(
                        Intent(this, MainActivity::class.java)
                            .addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                            )
                    )
                    finish()
                }
                .addOnFailureListener { e ->
                    toast("Failed: ${e.message}")
                    e.printStackTrace()
                }
        }

        // 🔙 Back → Main dashboard
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    startActivity(
                        Intent(this@ResetRecoveryPasswordActivity, MainActivity::class.java)
                    )
                    finish()
                }
            }
        )
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}