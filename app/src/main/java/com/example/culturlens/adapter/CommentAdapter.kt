package com.example.culturlens.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.culturlens.R
import com.example.culturlens.model.CommentItem

class CommentAdapter(private val comments: MutableList<CommentItem>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.username.text = comment.username
        holder.content.text = comment.content
        holder.createdAt.text = comment.createdAt
    }

    override fun getItemCount(): Int = comments.size

    fun addComment(comment: CommentItem) {
        comments.add(comment)
        notifyItemInserted(comments.size - 1)
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.tvUsername)
        val content: TextView = itemView.findViewById(R.id.tvComment)
        val createdAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
    }
}



