package com.example.culturlens.adapter

import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.culturlens.R
import com.example.culturlens.model.NotificationItem
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class NotificationAdapter(
    private var notifications: List<NotificationItem>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePicture: ImageView = itemView.findViewById(R.id.ivProfilePicture)
        val notificationText: TextView = itemView.findViewById(R.id.tvNotifications)
        val timeText: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notifications, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.notificationText.text = notification.message
        holder.timeText.text = notification.createdAt

        holder.timeText.text = getTimeAgo(notification.createdAt)

        notification.profilePhoto?.let {
            Glide.with(holder.profilePicture.context)
                .load(it)
                .into(holder.profilePicture)
        }
    }

    override fun getItemCount(): Int = notifications.size

    fun updateData(newNotifications: List<NotificationItem>) {
        notifications = newNotifications
        Log.d("NotificationAdapter", "Updated Notifications: $notifications")
        notifyDataSetChanged()
    }

    private fun getTimeAgo(createdAt: String?): String {
        if (createdAt.isNullOrEmpty()) return "Unknown time"

        try {
            // Memperbarui format untuk menangani milidetik (SSS) setelah detik
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            // Parsing tanggal dengan format baru
            val date = sdf.parse(createdAt)
            if (date == null) {
                Log.e("NotificationAdapter", "Failed to parse date: $createdAt")
                return "Unknown time"
            }

            val currentTime = System.currentTimeMillis()
            val diffInMillis = currentTime - date.time

            // Menghitung perbedaan waktu
            return when {
                diffInMillis < DateUtils.MINUTE_IN_MILLIS -> "Just now"
                diffInMillis < DateUtils.HOUR_IN_MILLIS -> {
                    val minutes = (diffInMillis / DateUtils.MINUTE_IN_MILLIS).toInt()
                    "$minutes minute${if (minutes > 1) "s" else ""} ago"
                }
                diffInMillis < DateUtils.DAY_IN_MILLIS -> {
                    val hours = (diffInMillis / DateUtils.HOUR_IN_MILLIS).toInt()
                    "$hours hour${if (hours > 1) "s" else ""} ago"
                }
                diffInMillis < DateUtils.WEEK_IN_MILLIS -> {
                    val days = (diffInMillis / DateUtils.DAY_IN_MILLIS).toInt()
                    "$days day${if (days > 1) "s" else ""} ago"
                }
                diffInMillis < DateUtils.YEAR_IN_MILLIS -> {
                    val months = (diffInMillis / (30L * DateUtils.DAY_IN_MILLIS)).toInt() // Menghitung bulan secara kasar
                    "$months month${if (months > 1) "s" else ""} ago"
                }
                else -> {
                    // Menghitung tahun secara manual (365 hari = 1 tahun)
                    val years = (diffInMillis / (365L * 24 * 60 * 60 * 1000)).toInt()
                    "$years year${if (years > 1) "s" else ""} ago"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("NotificationAdapter", "Error parsing date: ${e.message}")
            return "Unknown time"
        }
    }
}
