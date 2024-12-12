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

class CommentAdapter(private var comments: MutableList<CommentItem>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    // ViewHolder class
    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.tvUsername)
        val commentTextView: TextView = itemView.findViewById(R.id.tvComment)
        val createdAtTextView: TextView = itemView.findViewById(R.id.tvCreatedAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.usernameTextView.text = comment.username
        holder.commentTextView.text = comment.comment
        holder.createdAtTextView.text = comment.created_at
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    // Fungsi untuk memperbarui data komentar
    fun updateComments(newComments: List<CommentItem>) {
        comments.clear() // Hapus data lama
        comments.addAll(newComments) // Tambahkan data baru
        notifyDataSetChanged() // Beri tahu RecyclerView bahwa datanya telah berubah
    }

    // Fungsi untuk menambahkan komentar baru
    fun addComment(comment: CommentItem) {
        comments.add(comment) // Tambahkan komentar ke daftar
        notifyItemInserted(comments.size - 1) // Beri tahu RecyclerView tentang item baru
    }
}




