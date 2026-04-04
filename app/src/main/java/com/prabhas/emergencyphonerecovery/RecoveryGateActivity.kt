package com.prabhas.emergencyphonerecovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class RecoveryGateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery_gate)

        val pinInput = findViewById<EditText>(R.id.recoveryInput)
        val recoverBtn = findViewById<Button>(R.id.recoverBtn)

        recoverBtn.setOnClickListener {
            val enteredPin = pinInput.text.toString()
            val enteredHash = TrustManager.hash(enteredPin)

            val userRef = FirebaseDatabase.getInstance()
                .reference.child("users").child("owner_001")

            userRef.child("recovery_hash").get()
                .addOnSuccessListener { snapshot ->
                    val savedHash = snapshot.getValue(String::class.java)

                    if (savedHash == enteredHash) {
                        // ✅ OWNER VERIFIED
                        TrustManager.clearLocalTrust(this)
                        val newTrust = TrustManager.createTrust(this)
                        userRef.child("trust_id").setValue(newTrust)

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Invalid recovery PIN",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
// git test