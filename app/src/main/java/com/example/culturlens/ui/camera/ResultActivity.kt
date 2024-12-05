package com.example.culturlens.ui.camera

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.culturlens.R
import com.example.culturlens.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra("IMAGE_URI")?.let { Uri.parse(it) }
        val resultText = intent.getStringExtra("RESULT_TEXT")

        binding.resultImage.setImageURI(imageUri)
        binding.resultText.text = resultText ?: getString(R.string.result_not_found)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
