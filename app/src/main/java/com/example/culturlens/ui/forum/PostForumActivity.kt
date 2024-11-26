package com.example.culturlens.ui.forum

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.culturlens.R


class PostForumActivity : AppCompatActivity() {

    private lateinit var btnAddImage: TextView
    private lateinit var ivPreview: ImageView
    private lateinit var tvPlaceholder: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_forum)

        btnAddImage = findViewById(R.id.btnAddImage)
        ivPreview = findViewById(R.id.ivPreview)
        tvPlaceholder = findViewById(R.id.etInputText)

        btnAddImage.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                displayImage(imageUri)
            }
        }
    }

    private fun displayImage(imageUri: Uri) {
        ivPreview.setImageURI(imageUri)
        ivPreview.visibility = ImageView.VISIBLE
        tvPlaceholder.visibility = TextView.GONE
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}

