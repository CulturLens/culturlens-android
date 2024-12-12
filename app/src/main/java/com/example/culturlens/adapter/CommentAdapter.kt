package com.example.culturlens.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.culturlens.R
import com.example.culturlens.model.CommentItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CommentAdapter(private val comments: MutableList<CommentItem>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.tvUsername)
        private val commentTextView: TextView = itemView.findViewById(R.id.tvComment)
        private val createdAtTextView: TextView = itemView.findViewById(R.id.tvCreatedAt)

        fun bind(comment: CommentItem) {
            usernameTextView.text = comment.username
            commentTextView.text = comment.comment
            createdAtTextView.text = formatCreatedAt(comment.created_at)
        }

        private fun formatCreatedAt(createdAt: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                val date = inputFormat.parse(createdAt)
                date?.let { outputFormat.format(it) } ?: createdAt
            } catch (e: Exception) {
                createdAt
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int = comments.size

    fun updateComments(newComments: List<CommentItem>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }

    fun addComment(comment: CommentItem) {
        comments.add(comment)
        notifyItemInserted(comments.size - 1)
    }
}
