package com.smtz.assignment.walletqrpay.data.repository

import com.google.firebase.firestore.FirebaseFirestore

class ProfileEditRepository(
    db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = db.collection("users")

    fun updateProfile(userId: String, newUserName: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        usersCollection.document(userId)
            .update("userName", newUserName)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Failed to update profile")
            }
    }
}
