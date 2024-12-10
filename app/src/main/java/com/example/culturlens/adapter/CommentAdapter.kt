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

class CommentAdapter(private val comments: MutableList<CommentItem>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        holder.username.text = comment.username.ifEmpty { "Loading..." }
        holder.content.text = comment.comment
        holder.createdAt.text = formatDate(comment.created_at)
    }


    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Karena input berupa UTC

            val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getDefault() // Konversi ke zona waktu lokal

            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    override fun getItemCount(): Int = comments.size

    fun addComment(comment: CommentItem) {
        comments.add(comment)
        notifyDataSetChanged() // Atau tetap gunakan notifyItemInserted jika berfungsi
        Log.d("CommentAdapter", "New comment added: ${comment.comment}")
    }


    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.tvUsername)
        val content: TextView = itemView.findViewById(R.id.tvComment)
        val createdAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
    }
}



