package com.example.task3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    //Declaring variables
    private lateinit var loginUser: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginBtn: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()


        loginUser = findViewById(R.id.LoginUser)
        loginPassword = findViewById(R.id.LoginPassword)
        loginBtn = findViewById(R.id.LoginBtn)

        loginBtn.setOnClickListener {
            val email = loginUser.text.toString().trim()
            val password = loginPassword.text.toString().trim()

            // Sign in user with email and password
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with signed-in user's information
                        val user = firebaseAuth.currentUser
                        // You can proceed to your app's main activity or perform other actions
                        // For example:
                        val intent = Intent(this, ProfilePage::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        // You can handle different failure scenarios here.
                    }
                }
        }
    }
}
