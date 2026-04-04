package com.prabhas.emergencyphonerecovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class RecoveryDashboardActivity : AppCompatActivity() {

    private lateinit var etOldPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnChangePassword: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🚨 STEP 5 ENFORCEMENT — BLOCK DASHBOARD IF STOLEN
        if (StolenModeManager.isStolen(this)) {
            startActivity(Intent(this, StolenActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_recovery_dashboard)

        // Existing button (UNCHANGED behavior)
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // STEP 3 views
        etOldPassword = findViewById(R.id.etOldPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnChangePassword = findViewById(R.id.btnChangePassword)

        btnChangePassword.setOnClickListener {
            changeRecoveryPassword()
        }
    }

    // STEP 3 core logic
    private fun changeRecoveryPassword() {

        val oldPass = etOldPassword.text.toString().trim()
        val newPass = etNewPassword.text.toString().trim()
        val confirmPass = etConfirmPassword.text.toString().trim()

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            toast("All fields are required")
            return
        }

        if (newPass != confirmPass) {
            toast("New passwords do not match")
            return
        }

        val recoveryRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child("owner_001")
            .child("recovery")

        recoveryRef.child("password_hash").get()
            .addOnSuccessListener { snapshot ->

                val storedHash = snapshot.getValue(String::class.java)
                val oldHash = hashPassword(oldPass)

                if (storedHash == null || storedHash != oldHash) {
                    toast("Old recovery password is incorrect")
                    return@addOnSuccessListener
                }

                val newHash = hashPassword(newPass)

                recoveryRef.child("password_hash").setValue(newHash)
                    .addOnSuccessListener {
                        toast("Recovery password updated successfully")

                        etOldPassword.text.clear()
                        etNewPassword.text.clear()
                        etConfirmPassword.text.clear()
                    }
                    .addOnFailureListener {
                        toast("Failed to update password")
                    }
            }
            .addOnFailureListener {
                toast("Error verifying old password")
            }
    }

    // SAME hashing logic
    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
// git test