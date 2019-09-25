package com.han_wm.photoviewmarker

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.github.chrisbanes.photoview.PhotoView
import kotlin.random.Random

class MarkerPhotoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : PhotoView(context, attrs, defStyleAttr) {

    private val markerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFE300.toInt()
        style = Paint.Style.FILL
    }
    private val markers = arrayListOf<Marker>()

    init {
        setOnPhotoTapListener { view, x, y ->
            val markerBounds = markers.map { it.getBound(drawable, imageMatrix) }
            val find = markerBounds.find { it.contains(x, y) }
            if (find != null) {
                log("click point: $find")
            } else {
                addMarker(Marker(TYPE.random(), x, y))
            }
        }
    }

    fun addMarker(marker: Marker) {
        if (marker.type == TYPE.TYPE_TESTING) {
            markers.removeAll { it.type == TYPE.TYPE_TESTING }
        }
        markers.add(marker)
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawMakers(canvas)
    }

    /**
     * fixme: 速度太慢了，即使是画普通的矩形都很慢
     */
    private fun drawMakers(canvas: Canvas) {
        markers.forEach { drawMarker(canvas, it) }
    }

    private fun drawMarker(canvas: Canvas, it: Marker) {
        val pointShift = it.getDrawPoint(drawable, imageMatrix)
        canvas.drawBitmap(it.type.bitmap, pointShift.x, pointShift.y, markerPaint)
    }

}

private fun genBitmap(@ColorRes color: Int): Bitmap {
    return MyApp.resources.getDrawable(R.drawable.ic_pin, null).apply {
        setTint(ContextCompat.getColor(MyApp.context, color))
        setTintMode(PorterDuff.Mode.DST_OVER)
    }.toBitmap()
}

@Suppress("unused")
enum class TYPE(val bitmap: Bitmap) {
    TYPE_OK(genBitmap(R.color.success)),
    TYPE_FAIL(genBitmap(R.color.error)),
    TYPE_TESTING(genBitmap(R.color.warning));

    companion object {
        fun random(): TYPE {
            return values()[Random.nextInt(3)]
        }
    }
}

/**
 * @param x 百分比
 * @param y 百分比
 */
data class Marker(val type: TYPE, val x: Float, val y: Float) {
    val point = PointF(x, y)

    /**
     * 2次转换：
     *    1. 百分比 -> 图像坐标
     *    2. 图像坐标 -> 视图坐标（缩放，平移等变换）
     */
    private fun getMappedPoint(drawable: Drawable, matrix: Matrix): PointF {
        val pointRaw = floatArrayOf(drawable.intrinsicWidth * x, drawable.intrinsicHeight * y)
        val pointMapped = FloatArray(2)
        matrix.mapPoints(pointMapped, pointRaw)
        return PointF(pointMapped[0], pointMapped[1])
    }

    /**
     * 偏移转换后的坐标，使图标的下边中间位于坐标上
     */
    fun getDrawPoint(drawable: Drawable, matrix: Matrix): PointF {
        val point = getMappedPoint(drawable, matrix)
        return PointF(point.x - type.bitmap.width / 2, point.y - type.bitmap.height)
    }

    /**
     * 获取绘制的图标的边界
     */
    fun getBound(drawable: Drawable, matrix: Matrix): RectF {
        val point = getMappedPoint(drawable, matrix) // bottom-center
        val markerWidthHalf = type.bitmap.width / 2
        val markerHeight = type.bitmap.height
        return RectF(point.x - markerWidthHalf, point.y - markerHeight, point.x + markerWidthHalf, point.y)
    }
}