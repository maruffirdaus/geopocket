package dev.maruffirdaus.geopocket.ui.ar.activity.extension

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

suspend fun Bitmap.saveToCache(context: Context): File =
    withContext(Dispatchers.IO) {
        File(context.cacheDir, "${System.currentTimeMillis()}.jpg").also { file ->
            FileOutputStream(file).use { compress(Bitmap.CompressFormat.JPEG, 90, it) }
        }
    }