package com.example.culturlens.ui.forum


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.culturlens.R
import com.example.culturlens.adapter.ForumAdapter
import com.example.culturlens.api.ApiClient
import com.example.culturlens.databinding.FragmentForumBinding
import com.example.culturlens.model.ForumItem
import com.example.culturlens.response.ForumsResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

        // Ensure binding is not null before using it
        _binding?.let { binding ->
            setupRecyclerView()
            setupSearchBar()
            fetchForums()

            // Set up TextView for "Post Forum"
            val tvPostForum: TextView = binding.tvPostForum
            tvPostForum.setOnClickListener {
                // Navigate to PostForumActivity
                val intent = Intent(requireContext(), PostForumActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupRecyclerView() {
        forumAdapter = ForumAdapter(
            forumList = filteredForumList,
            onItemClick = { forumItem ->
                Toast.makeText(requireContext(), "Forum item: ${forumItem.title}", Toast.LENGTH_SHORT).show()
            }
        )

        _binding?.recyclerViewForum?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = forumAdapter
            Log.d("ForumFragment", "RecyclerView setup complete, items count: ${forumAdapter.itemCount}")
        }
    }

    private fun setupSearchBar() {
        _binding?.etSearchForum?.addTextChangedListener { text ->
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
        _binding?.progressBar?.visibility = View.VISIBLE

        val apiService = ApiClient.instance
        apiService.getForums().enqueue(object : Callback<ForumsResponse> {
            override fun onResponse(call: Call<ForumsResponse>, response: Response<ForumsResponse>) {
                _binding?.progressBar?.visibility = View.GONE

                if (response.isSuccessful) {
                    val forumsResponse = response.body()

                    forumsResponse?.forums?.let { forums ->
                        forumList.clear()
                        forumList.addAll(forums)
                        filteredForumList.clear()
                        filteredForumList.addAll(forumList)
                        forumAdapter.notifyDataSetChanged()
                    }

                } else {
                    Toast.makeText(requireContext(),
                        getString(R.string.failed_to_load_forums), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ForumsResponse>, t: Throwable) {
                _binding?.progressBar?.visibility = View.GONE
                Toast.makeText(requireContext(),
                    getString(R.string.network_error, t.message), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}







