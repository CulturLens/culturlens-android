package com.example.culturlens.adapter

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
}
