package com.shashi.blogmob.daos

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.shashi.blogmob.Constants
import com.shashi.blogmob.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {

    val db = FirebaseFirestore.getInstance()
    val userCollection = db.collection(Constants.DB_USER)

    fun addUser(user: User) {
        user?.let {

            GlobalScope.launch(Dispatchers.IO) {
                userCollection.document(user.uid).set(it)
            }

        }
    }

    // get user info
    fun getUserById(uid: String): Task<DocumentSnapshot> {
        return userCollection.document(uid).get()
    }

}