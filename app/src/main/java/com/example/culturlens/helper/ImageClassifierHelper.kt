package com.example.culturlens.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.culturlens.R
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier


class ImageClassifierHelper(
    var threshold: Float = 0.7f,
    var maxResults: Int = 3,
    val modelName: String = "model_CulturLens.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?
) {
    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    interface ClassifierListener{
        fun onError(error: String)
        fun onResults(results: List<Classifications>?)
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(4)
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, e.message.toString())
        }
    }

    fun classifyStaticImage(imageUri: Uri) {
        if (imageClassifier == null) {
            setupImageClassifier()
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .build()

        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)?.copy(Bitmap.Config.ARGB_8888, true)
            inputStream?.close()

            bitmap?.let {
                val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
                val results = imageClassifier?.classify(tensorImage)

                classifierListener?.onResults(results)
            } ?: run {
                classifierListener?.onError("Gagal melakukan klasifikasi pada gambar.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error classifying image: ${e.message}")
            classifierListener?.onError("Error loading image: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}