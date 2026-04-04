package com.prabhas.emergencyphonerecovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class VerifyAppLockActivity : AppCompatActivity() {

    private lateinit var passwordInput: EditText
    private lateinit var verifyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_applock)

        passwordInput = findViewById(R.id.passwordInput)
        verifyButton = findViewById(R.id.verifyButton)

        verifyButton.setOnClickListener {

            val enteredPassword = passwordInput.text.toString()

            if (enteredPassword.isEmpty()) {
                Toast.makeText(this,
                    "Enter password",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val storedHash =
                SecurePrefs.getAppLockHash(this)

            val enteredHash =
                HashUtils.sha256(enteredPassword)

            if (enteredHash == storedHash) {

                // Correct password → open change screen
                startActivity(
                    Intent(this,
                        ChangeAppLockPasswordActivity::class.java)
                )

                finish()

            } else {

                Toast.makeText(this,
                    "Incorrect password",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}
