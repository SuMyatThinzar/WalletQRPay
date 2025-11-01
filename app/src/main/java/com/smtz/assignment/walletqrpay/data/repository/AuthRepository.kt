package com.smtz.assignment.walletqrpay.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.smtz.assignment.walletqrpay.data.model.UserData

class AuthRepository(
    firebaseFirestoreDB: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firebaseFirestoreDB.collection("users")

    fun login(
        phone: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        usersCollection.whereEqualTo("phone", phone).get()
            .addOnSuccessListener { query ->
                if (!query.isEmpty) {
                    val doc = query.documents.first()   // query.documents is a list of DocumentSnapshot
                    if (doc.getString("password") == password) {
                        val userId = doc.id
                        onSuccess(userId)    // get userId and save it to UserPreferences
                    } else {
                        onFailure("Invalid Phone Number or Password")
                    }
                } else {
                    onFailure("User not found")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Login failed")
            }
    }

    fun signup(
        userName: String,
        phone: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        usersCollection.whereEqualTo("phone", phone).get()
            .addOnSuccessListener { query ->
                if (!query.isEmpty) {
                    onFailure("User already exists, Please Login")
                    return@addOnSuccessListener
                }

                val newDocRef = usersCollection.document() // generates an ID, but doesn't write yet


                val newUserData = UserData(
                    userId = newDocRef.id,
                    userName = userName,
                    phone = phone,
                    password = password,
                    points = 1000,
                    transactions = emptyList()
                )

                newDocRef.set(newUserData)    // writes data to an existing or specified document (overwrites if exists)
                    .addOnSuccessListener {
                        onSuccess(newDocRef.id)
                    }
                    .addOnFailureListener { e ->
                        onFailure(e.message ?: "Signup failed")
                    }

//                usersCollection.add(newUser)    //  creates a new document with auto ID
//                    .addOnSuccessListener { documentReference ->
//                        onSuccess(documentReference.id)
//                    }
//                    .addOnFailureListener { e ->
//                        onFailure(e.message ?: "Signup failed")
//                    }

            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Signup failed")
            }
    }


    // if FCM token is used
//    fun signup(
//        userName: String,
//        phone: String,
//        password: String,
//        onSuccess: (String) -> Unit,
//        onFailure: (String) -> Unit
//    ) {
//        usersCollection.whereEqualTo("phone", phone).get()
//            .addOnSuccessListener { query ->
//                if (!query.isEmpty) {
//                    onFailure("User already exists, Please Login")
//                    return@addOnSuccessListener
//                }
//
//                val newDocRef = usersCollection.document() // generates an ID, but doesn't write yet
//
//                // get the FCM token first
//                FirebaseMessaging.getInstance().token
//                    .addOnCompleteListener { task ->
//                        if (!task.isSuccessful) {
////                            onFailure("Failed to get FCM token")
//                            onFailure("Please check the internet connection and try again")
//                            return@addOnCompleteListener
//                        }
//
//                        val token = task.result ?: ""
//
//                        val newUserData = UserData(
//                            userId = newDocRef.id,
//                            userName = userName,
//                            phone = phone,
//                            password = password,
//                            points = 1000,
//                            fcmToken = token,
//                            transactions = emptyList()
//                        )
//
//                        newDocRef.set(newUserData)    // writes data to an existing or specified document (overwrites if exists)
//                            .addOnSuccessListener {
//                                onSuccess(newDocRef.id)
//                            }
//                            .addOnFailureListener { e ->
//                                onFailure(e.message ?: "Signup failed")
//                            }
//                    }
//
//            }
//            .addOnFailureListener { e ->
//                onFailure(e.message ?: "Signup failed")
//            }
//    }

}

