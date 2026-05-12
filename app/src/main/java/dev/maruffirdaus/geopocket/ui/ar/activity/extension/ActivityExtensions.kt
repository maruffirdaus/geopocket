package dev.maruffirdaus.geopocket.ui.ar.activity.extension

import android.app.Activity
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import androidx.core.graphics.createBitmap

fun Activity.capture(onSuccess: (Bitmap) -> Unit, onError: (Int) -> Unit) {
    val bounds = windowManager.currentWindowMetrics.bounds
    val bitmap = createBitmap(bounds.width(), bounds.height())
    PixelCopy.request(window, null, bitmap, { result ->
        if (result == PixelCopy.SUCCESS) onSuccess(bitmap)
        else onError(result)
    }, Handler(Looper.getMainLooper()))
}