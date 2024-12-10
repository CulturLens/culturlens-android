package com.example.culturlens.helper

import android.content.Context
import com.example.culturlens.R

object ClassificationInfo {

    fun descriptions (context: Context): Map<String, String> {
        return mapOf(
            "canang" to context.getString(R.string.canang_desc),
            "kain poleng" to context.getString(R.string.kain_poleng_desc),
            "pelinggih" to context.getString(R.string.pelinggih_desc),
            "gebogan" to context.getString(R.string.gebogan_desc),
            "pelangkiran" to context.getString(R.string.pelangkiran_desc),
            "banten saiban" to context.getString(R.string.banten_saiban_desc),
            "penjor" to context.getString(R.string.penjor_desc)
        )
    }

    fun prohibitions (context: Context): Map<String, List<String>> {
        return mapOf(
            "canang" to context.resources.getStringArray(R.array.canang_pro).toList(),
            "kain poleng" to context.resources.getStringArray(R.array.kain_poleng_pro).toList(),
            "pelinggih" to context.resources.getStringArray(R.array.pelinggih_pro).toList(),
            "gebogan" to context.resources.getStringArray(R.array.gebogan_pro).toList(),
            "pelangkiran" to context.resources.getStringArray(R.array.pelangkiran_pro).toList(),
            "banten saiban" to context.resources.getStringArray(R.array.banten_saiban_pro).toList(),
            "penjor" to context.resources.getStringArray(R.array.penjor_pro).toList()
        )
    }
}
