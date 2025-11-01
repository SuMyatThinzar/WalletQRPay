package com.smtz.assignment.walletqrpay.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.smtz.assignment.walletqrpay.data.model.TransactionData
import com.smtz.assignment.walletqrpay.data.model.UserData

class TransferRepository(
    private val firebaseFirestoreDB: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun getUserInfo(
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

    fun fetchUserInfoUsingPhoneNumber(
        phoneNumber: String,
        onSuccess: (UserData) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firebaseFirestoreDB.collection("users")
            .whereEqualTo("phone", phoneNumber)
            .limit(1) // since phone numbers are unique
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val user = querySnapshot.documents[0].toObject(UserData::class.java)
                    user?.let { onSuccess(it) } ?: onFailure("Failed to parse user data")
                } else {
                    onFailure("User not found")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error fetching user")
            }
    }

    fun performTransferTransaction(
        senderData: UserData,
        receiverData: UserData,
        amount: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val senderRef = db.collection("users").document(senderData.userId)
        val receiverRef = db.collection("users").document(receiverData.userId)

        db.runTransaction { transaction ->
            val senderSnap = transaction.get(senderRef)
            val receiverSnap = transaction.get(receiverRef)

            val senderPoints = senderSnap.getLong("points") ?: 0
            val receiverPoints = receiverSnap.getLong("points") ?: 0

            if (senderPoints < amount) {
                throw Exception("Insufficient balance")
            }

            val transactionId = System.currentTimeMillis().toString()
            val timestamp = System.currentTimeMillis()


            val newTransaction = TransactionData(
                transactionId = transactionId,
                senderId = senderData.userId,
                senderPhone = senderData.phone,
                receiverId = receiverData.userId,
                receiverPhone = receiverData.phone,
                amount = amount,
                timestamp = timestamp,
                transactionType = "transfer"
            )

            //  update points for both users
            transaction.update(senderRef, "points", senderPoints - amount)
            transaction.update(receiverRef, "points", receiverPoints + amount)

            // add transaction info to both users’ transactions list
            transaction.update(senderRef, "transactions", FieldValue.arrayUnion(newTransaction))
            transaction.update(receiverRef, "transactions", FieldValue.arrayUnion(newTransaction))

            //  save in "transactions" collection
            val transactionRef = db.collection("transactions").document(transactionId)
            transaction.set(transactionRef, newTransaction)
        }
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to transfer") }
    }

}
