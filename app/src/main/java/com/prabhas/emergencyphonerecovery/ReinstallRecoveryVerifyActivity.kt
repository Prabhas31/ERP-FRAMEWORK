package com.prabhas.emergencyphonerecovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class ReinstallRecoveryVerifyActivity : AppCompatActivity() {

    private lateinit var recoveryInput: EditText
    private lateinit var verifyButton: Button
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = SecurePrefs.getUserId(this)

        setContentView(R.layout.activity_reinstall_recovery_verify)

        recoveryInput = findViewById(R.id.etRecoveryPassword)
        verifyButton = findViewById(R.id.btnVerifyRecovery)

        verifyButton.setOnClickListener {

            val enteredPassword = recoveryInput.text.toString().trim()

            if (enteredPassword.isEmpty()) {
                recoveryInput.error = "Enter recovery password"
                return@setOnClickListener
            }

            verifyRecoveryFromFirebase(enteredPassword)
        }
    }

    private fun verifyRecoveryFromFirebase(input: String) {

        val hash = HashUtils.sha256(input.trim())

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("recovery")
            .get()
            .addOnSuccessListener { snapshot ->

                val storedHash = snapshot.child("password_hash")
                    .getValue(String::class.java)

                if (storedHash == null) {
                    Toast.makeText(this, "No password found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                if (storedHash == hash) {

                    getSharedPreferences("install_state", MODE_PRIVATE)
                        .edit()
                        .putBoolean("trusted_install", true)
                        .apply()

                    SecurePrefs.setRecoveryPasswordSet(this, true)

                    Toast.makeText(this, "Recovery SUCCESS", Toast.LENGTH_SHORT).show()

                    startActivity(
                        Intent(this, AppLockSetupActivity::class.java)
                    )
                    finish()

                } else {
                    recoveryInput.error = "Incorrect recovery password"
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Firebase error", Toast.LENGTH_SHORT).show()
            }
    }
}
// git test