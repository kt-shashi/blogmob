package com.shashi.blogmob.models

data class Post(
    val title: String = "",
    val desc: String = "",
    val createdBy: User = User(),
    val createdAt: Long = 0L,
    val likedBy: ArrayList<String> = ArrayList()
) {

}