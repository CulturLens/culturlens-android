package com.example.culturlens.ui.camera

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.culturlens.R
import com.example.culturlens.databinding.FragmentImageBinding
import com.example.culturlens.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class ImageFragment : Fragment(R.layout.fragment_image), ImageClassifierHelper.ClassifierListener {

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var currentImageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentImageBinding.bind(view)

        imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = this
        )

        val imageUri = arguments?.getString("imageUri")?.let { Uri.parse(it) }
        imageUri?.let {
            currentImageUri = it
            Glide.with(this)
                .load(it)
                .into(binding.previewImageView)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let { uri ->
                analyzeImage(uri)
            } ?: showToast(getString(R.string.empty_image_warning))
        }
    }

    private fun analyzeImage(uri: Uri) {
        binding.progressBar.visibility = View.VISIBLE
        imageClassifierHelper.classifyStaticImage(uri)
    }

    override fun onResults(results: List<Classifications>?) {
        binding.progressBar.visibility = View.GONE

        val resultText = results?.firstOrNull()?.categories
            ?.maxByOrNull { it.score }
            ?.let { category ->
                if (category.score >= imageClassifierHelper.threshold) {
                    "${category.label}: ${"%.2f".format(category.score * 100)}%"
                } else {
                    getString(R.string.result_not_found)
                }
            } ?: getString(R.string.result_not_found)

        // Kirim data ke SuccessResultActivity
        currentImageUri?.let { uri ->
            val intent = Intent(requireContext(), SuccessResultActivity::class.java).apply {
                putExtra("IMAGE_URI", uri.toString())  // Kirim URI gambar
                putExtra("RESULT_TEXT", resultText)    // Kirim hasil klasifikasi
            }
            startActivity(intent)
        }
    }

    override fun onError(error: String) {
        binding.progressBar.visibility = View.GONE
        showToast(error)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
