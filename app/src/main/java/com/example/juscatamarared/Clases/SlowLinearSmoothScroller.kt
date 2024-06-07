package com.example.juscatamarared.Clases

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller

class SlowLinearSmoothScroller (context: Context) : LinearSmoothScroller(context) {
    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        // Ajusta el valor para controlar la velocidad del desplazamiento
        return 100f / displayMetrics.densityDpi
    }
}