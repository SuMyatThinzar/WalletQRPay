package com.smtz.assignment.walletqrpay.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.smtz.assignment.walletqrpay.data.model.UserData

class HomeRepository(
    private val firebaseFirestoreDB: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun getUserInfoOneTime(
        userId: String,
        onSuccess: (UserData) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firebaseFirestoreDB.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(UserData::class.java)
                    user?.let { onSuccess(it) }
                } else {
                    onFailure("User not found")
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Unknown error")
            }
    }

    fun observeUserInfoRealtime(
        userId: String,
        onSuccess: (UserData) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firebaseFirestoreDB.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onFailure(error.message ?: "Error observing user data")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(UserData::class.java)
                    user?.let { onSuccess(it) } ?: onFailure("Failed to parse user data")
                } else {
                    onFailure("User not found")
                }
            }
    }


}