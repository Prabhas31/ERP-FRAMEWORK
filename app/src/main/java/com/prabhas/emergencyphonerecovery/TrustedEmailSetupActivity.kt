package com.prabhas.emergencyphonerecovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class TrustedEmailSetupActivity : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ FIX
        userId = SecurePrefs.getUserId(this)

        setContentView(R.layout.activity_trusted_email_setup)

        val etEmail1 = findViewById<EditText>(R.id.etEmail1)
        val etEmail2 = findViewById<EditText>(R.id.etEmail2)
        val etEmail3 = findViewById<EditText>(R.id.etEmail3)
        val btnSave = findViewById<Button>(R.id.btnSaveEmails)

        btnSave.setOnClickListener {

            val email1 = etEmail1.text.toString().trim()
            val email2 = etEmail2.text.toString().trim()
            val email3 = etEmail3.text.toString().trim()

            if (email1.isEmpty()) {
                toast("Email 1 is required")
                return@setOnClickListener
            }

            saveTrustedEmails(email1, email2, email3)
        }
    }

    private fun saveTrustedEmails(
        email1: String,
        email2: String,
        email3: String
    ) {

        val trustedRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("trusted_emails")

        val updates = mutableMapOf<String, Any>(
            "email_1" to email1,
            "trusted_emails_set" to true
        )

        if (email2.isNotEmpty()) updates["email_2"] = email2
        if (email3.isNotEmpty()) updates["email_3"] = email3

        trustedRef.updateChildren(updates)
            .addOnSuccessListener {

                toast("Trusted emails saved")

                startActivity(
                    Intent(this, MainActivity::class.java)
                        .addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                        )
                )

                finish()
            }
            .addOnFailureListener {
                toast("Failed to save trusted emails")
            }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
// git test