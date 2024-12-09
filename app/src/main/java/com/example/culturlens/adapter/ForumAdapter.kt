package com.example.culturlens.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.culturlens.R
import com.example.culturlens.databinding.ItemForumBinding
import com.example.culturlens.model.ForumItem

class ForumAdapter(
    private var forumList: List<ForumItem>,
    private val onItemClick: (ForumItem) -> Unit,
    private val onLikeClick: (ForumItem) -> Unit
) : RecyclerView.Adapter<ForumAdapter.ForumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumViewHolder {
        val binding = ItemForumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForumViewHolder, position: Int) {
        val forumItem = forumList[position]
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
            binding.ivLike.setImageResource(
                if (forumItem.isLiked) R.drawable.ic_like_fill else R.drawable.ic_like
            )

            binding.root.setOnClickListener { onItemClick(forumItem) }
            binding.ivLike.setOnClickListener { onLikeClick(forumItem) }
        }
    }
}

