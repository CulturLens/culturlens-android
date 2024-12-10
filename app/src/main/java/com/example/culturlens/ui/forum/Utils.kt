package com.example.culturlens.ui.forum

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object FileUtils {
    fun getFileFromUri(context: Context, uri: Uri): File {
        return when (uri.scheme) {
            "content" -> {
                val contentResolver = context.contentResolver
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    it.moveToFirst()
                    val fileName = it.getString(nameIndex)
                    val inputStream = contentResolver.openInputStream(uri)
                    val tempFile = File(context.cacheDir, fileName)
                    inputStream?.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    tempFile
                } ?: throw IllegalArgumentException("Gagal membaca file dari URI")
            }
            "file" -> File(uri.path!!)
            else -> throw IllegalArgumentException("Skema URI tidak didukung: ${uri.scheme}")
        }
    }
}




