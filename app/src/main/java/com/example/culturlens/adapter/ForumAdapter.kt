package com.example.culturlens.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.culturlens.R
import com.example.culturlens.databinding.ItemForumBinding
import com.example.culturlens.model.ForumItem

class ForumAdapter(
    private var forumList: List<ForumItem>,
    private val onItemClick: (ForumItem) -> Unit
) : RecyclerView.Adapter<ForumAdapter.ForumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumViewHolder {
        val binding = ItemForumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForumViewHolder, position: Int) {
        val forumItem = forumList[position]
        Log.d("ForumAdapter", "Binding item: ${forumItem.title}")
        holder.bind(forumItem)
    }

    override fun getItemCount(): Int = forumList.size

    fun submitList(newList: List<ForumItem>) {
        forumList = newList
        notifyDataSetChanged()
    }

    inner class ForumViewHolder(private val binding: ItemForumBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(forumItem: ForumItem) {
            binding.tvUsername.text = forumItem.username
            binding.tvDescription.text = forumItem.description

            val fullImageUrl = forumItem.imageUrl
            Glide.with(binding.root.context)
                .load(fullImageUrl)
                .placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.ic_error)
                .into(binding.ivPostImage)

            binding.root.setOnClickListener {
                onItemClick(forumItem)
            }
        }
    }
}