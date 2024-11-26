package com.example.culturlens.ui.camera

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.culturlens.R
import com.example.culturlens.databinding.FragmentImageBinding

class ImageFragment : Fragment(R.layout.fragment_image) {

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentImageBinding.bind(view)

        val imageUri = arguments?.getString("imageUri")?.let { Uri.parse(it) }
        imageUri?.let {
            Glide.with(this)
                .load(it)
                .into(binding.previewImageView)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
