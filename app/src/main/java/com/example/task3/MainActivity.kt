package com.example.task3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var regEmail: EditText
    private lateinit var passwordReg: EditText
    private lateinit var RegButton: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        regEmail = findViewById(R.id.regEmail)
        passwordReg = findViewById(R.id.passwordReg)
        RegButton = findViewById(R.id.RegButton)
        firebaseAuth = FirebaseAuth.getInstance()

        RegButton.setOnClickListener {
            val email = regEmail.text.toString().trim()
            val password = passwordReg.text.toString().trim()

            // Create user with email and password
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // User registered successfully
                        // You can handle success actions here
                        // For example, navigating to login page
                        // Commenting out navigation for now
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Registration failed
                        // You can handle failure actions here
                    }
                }
        }
    }

    // Method to navigate to the login screen
    fun navigateToLogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
