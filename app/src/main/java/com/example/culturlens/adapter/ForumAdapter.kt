package com.example.culturlens.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.culturlens.R
import com.example.culturlens.model.ForumItem

class ForumAdapter(
    private val forumList: MutableList<ForumItem>,
    private val onItemClick: (ForumItem) -> Unit,
    private val onLikeClick: (ForumItem) -> Unit
) : RecyclerView.Adapter<ForumAdapter.ForumViewHolder>() {

    class ForumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.tvUsername)
        val postContent: TextView = itemView.findViewById(R.id.tvPostContent)
        val postImage: ImageView = itemView.findViewById(R.id.ivPostImage)
        val likeIcon: ImageView = itemView.findViewById(R.id.ivLike)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forum, parent, false)
        return ForumViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForumViewHolder, position: Int) {
        val item = forumList[position]

        holder.username.text = item.username
        holder.postContent.text = item.content

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_error)
            .into(holder.postImage)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }


        holder.likeIcon.setImageResource(
            if (item.isLiked) R.drawable.ic_like_fill else R.drawable.ic_like
        )
        holder.likeIcon.setOnClickListener {
            item.isLiked = !item.isLiked
            onLikeClick(item)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = forumList.size
}


