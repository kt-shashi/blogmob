package com.shashi.blogmob

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.shashi.blogmob.models.Post

class PostAdapter(options: FirestoreRecyclerOptions<Post>, val listener: IPostAdapter) :
    FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(
        options
    ) {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postTitle: TextView = itemView.findViewById(R.id.tv_postTitlePost)
        val postDesc: TextView = itemView.findViewById(R.id.tv_postDescPost)
        val userText: TextView = itemView.findViewById(R.id.tv_userNamePost)
        val createdAt: TextView = itemView.findViewById(R.id.tv_createdAtPost)
        val likeCount: TextView = itemView.findViewById(R.id.tv_likeCountPost)
        val userImage: ImageView = itemView.findViewById(R.id.iv_userImagePost)
        val likeButton: ImageView = itemView.findViewById(R.id.iv_likeButtonPost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        var viewHolder = PostViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post, parent, false)
        )

        viewHolder.likeButton.setOnClickListener {
            listener.onLikeClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {

        holder.postTitle.text = model.title
        holder.postDesc.text = model.desc
        holder.userText.text = model.createdBy.name
        Glide
            .with(holder.userImage.context)
            .load(model.createdBy.imageUrl)
            .circleCrop()
            .into(holder.userImage)

        holder.likeCount.text = model.likedBy.size.toString()
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)

        val auth = Firebase.auth
        val currentUserId = auth.currentUser!!.uid

        val isLiked = model.likedBy.contains(currentUserId)

        if (isLiked)
            holder.likeButton.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.likeButton.context,
                    R.drawable.icon_liked
                )
            )
        else
            holder.likeButton.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.likeButton.context,
                    R.drawable.icon_not_liked
                )
            )
    }

}

interface IPostAdapter {
    fun onLikeClicked(postId: String)
}