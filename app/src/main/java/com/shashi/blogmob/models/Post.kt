package com.shashi.blogmob.models

import com.google.firebase.Timestamp

data class Post(
    val title: String = "",
    val desc: String = "",
    val createdBy: User = User(),
    val createdAt: Timestamp? = null,
    val likedBy: ArrayList<String> = ArrayList()
) {

}