package com.example.culturlens.ui.camera

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.culturlens.R
import com.example.culturlens.databinding.ActivityResultBinding
import com.example.culturlens.helper.ClassificationInfo

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra("IMAGE_URI")?.let { Uri.parse(it) }
        val resultText = intent.getStringExtra("RESULT_TEXT") ?: getString(R.string.result_not_found)

        binding.resultImage.setImageURI(imageUri)
        binding.resultText.text = resultText

        if (resultText == getString(R.string.result_not_found)) {
            binding.descriptionText.visibility = View.GONE
            binding.prohibitionsText.visibility = View.GONE
            binding.dilarang.visibility = View.GONE
            binding.about.visibility = View.GONE
        } else {
            val classification = resultText.split(":").firstOrNull()?.lowercase()
            val descriptions = ClassificationInfo.descriptions(this)
            val prohibitionsMap = ClassificationInfo.prohibitions(this)

            val description = descriptions[classification]
            val prohibitions = prohibitionsMap[classification]?.joinToString(
                separator = "\n• ",
                prefix = "• "
            )

            binding.descriptionText.text = description
            binding.prohibitionsText.text = prohibitions
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
