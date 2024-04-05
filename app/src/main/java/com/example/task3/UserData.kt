import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

fun UserProfile(
    context: Context,
    db: FirebaseFirestore,
    userID: String?,
    name: String,
    qualification: String,
    studentNumber: String,
    profilePictureUri: Uri?,
    username: String?
) {
    userID?.let { uid ->
        profilePictureUri?.let { uri ->
            val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/${UUID.randomUUID()}")
            storageRef.putFile(uri)
                .addOnSuccessListener { snapshot ->
                    snapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                        val profilePictureUrl = downloadUri.toString()
                        // Create a HashMap to store user data including the profile picture URL
                        val userData = hashMapOf(
                            "userID" to uid,
                            "username" to (username ?: ""),
                            "name" to name,
                            "qualification" to qualification,
                            "studentNumber" to studentNumber,
                            "profilePictureUrl" to profilePictureUrl
                            // Add other fields as needed
                        )

                        // Store user data in Firestore
                        db.collection("users").document(uid)
                            .set(userData)
                            .addOnSuccessListener {
                                // Data saved successfully
                                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                // Error occurred while saving data
                                Toast.makeText(
                                    context,
                                    "Failed to update profile: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        context,
                        "Failed to upload profile picture: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } ?: run {
            Toast.makeText(context, "Profile picture URI is null", Toast.LENGTH_SHORT).show()
        }
    } ?: run {
        Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show()
    }
}
