package com.example.task3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ProfilePage : AppCompatActivity() {

    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var chosenImageUri: Uri
    private lateinit var btnUploadImage: Button
    private lateinit var uploadImageBtn: Button
    private lateinit var profilePicture: ImageView
    private lateinit var studentName: EditText
    private lateinit var qualification: EditText
    private lateinit var studentNumber: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_page)

        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        studentName = findViewById(R.id.StudentName)
        qualification = findViewById(R.id.Qualification)
        studentNumber = findViewById(R.id.StudentNumber)
        profilePicture = findViewById(R.id.ProfilePicture)
        uploadImageBtn = findViewById(R.id.UploadImageBtn)
        btnUploadImage = findViewById(R.id.SubmitBtn)

        uploadImageBtn.setOnClickListener {
            pickImageFromGallery()
        }

        btnUploadImage.setOnClickListener {
            val name = studentName.text.toString()
            val qualification = qualification.text.toString()
            val number = studentNumber.text.toString()

            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val username = FirebaseAuth.getInstance().currentUser?.displayName

            userProfile(userID, name, qualification, number, username)
        }
    }

    private fun userProfile(userID: String?, name: String, qualification: String, studentNumber: String, username: String?) {
        userID?.let { uid ->
            // Retrieve username from Firebase Authentication
            val currentUser = FirebaseAuth.getInstance().currentUser
            val username = currentUser?.displayName


            // Include username in the userData
            val userData = hashMapOf(
                "userID" to uid,
                "username" to (username ?: ""), // Ensure username is not null
                "name" to name,
                "qualification" to qualification,
                "studentNumber" to studentNumber
            )

            // Save userData to Firestore
            firestore.collection("users").document(uid)
                .set(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }


    private fun pickImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                chosenImageUri = data.data!!
                profilePicture.setImageURI(chosenImageUri)
                uploadImageToFirebase(chosenImageUri)
            } catch (e: Exception) {
                Log.e("ProfilePage", "Error getting selected files: ${e.message}")
                Toast.makeText(this, "Error getting selected files", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = storage.reference.child("images/$fileName")

        refStorage.putFile(fileUri)
            .addOnSuccessListener {
                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("ProfilePage", "Image upload failed: ${e.message}")
                Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
