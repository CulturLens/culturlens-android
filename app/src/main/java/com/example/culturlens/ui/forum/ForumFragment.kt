package com.example.culturlens.ui.forum


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.culturlens.adapter.ForumAdapter
import com.example.culturlens.api.ApiClient
import com.example.culturlens.databinding.FragmentForumBinding
import com.example.culturlens.model.ForumItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForumFragment : Fragment() {

    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!

    private lateinit var forumAdapter: ForumAdapter
    private val forumList = mutableListOf<ForumItem>()
    private val filteredForumList = mutableListOf<ForumItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchBar()
        fetchForums()
    }

    private fun setupRecyclerView() {
        forumAdapter = ForumAdapter(
            forumList = filteredForumList,
            onItemClick = { forumItem ->
                val intent = Intent(requireContext(), DetailForumActivity::class.java)
                intent.putExtra("forum_id", forumItem.id)
                startActivity(intent)
            },
            onLikeClick = { forumItem ->
            }
        )

        binding.recyclerViewForum.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = forumAdapter
        }
    }

    private fun setupSearchBar() {
        binding.etSearchForum.addTextChangedListener { text ->
            val query = text.toString().trim()
            filterForums(query)
        }
    }

    private fun filterForums(query: String) {
        filteredForumList.clear()
        if (query.isEmpty()) {
            filteredForumList.addAll(forumList)
        } else {
            filteredForumList.addAll(
                forumList.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
            )
        }
        forumAdapter.notifyDataSetChanged()
    }

    private fun fetchForums() {
        binding.progressBar.visibility = View.VISIBLE

        val apiService = ApiClient.instance
        apiService.getForums().enqueue(object : Callback<List<ForumItem>> {
            override fun onResponse(call: Call<List<ForumItem>>, response: Response<List<ForumItem>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val forums = response.body()
                    if (forums != null) {
                        forumList.clear()
                        forumList.addAll(forums)

                        filteredForumList.clear()
                        filteredForumList.addAll(forumList)

                        forumAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load forums", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ForumItem>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

