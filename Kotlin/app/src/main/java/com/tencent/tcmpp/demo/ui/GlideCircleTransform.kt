package com.tencent.tcmpp.demo.ui

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest
import kotlin.math.min

class GlideCircleTransform : BitmapTransformation() {

    companion object {
        private fun circleCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
            if (source == null) {
                return null
            }

            val size = min(source.width, source.height)
            val x = (source.width - size) / 2
            val y = (source.height - size) / 2

            // TODO this could be acquired from the pool too
            val squared = Bitmap.createBitmap(source, x, y, size, size)

            var result = pool.get(size, size, Bitmap.Config.ARGB_8888)
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            }

            val canvas = Canvas(result)
            val paint = Paint().apply {
                shader = BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                isAntiAlias = true
            }
            val r = size / 2f
            canvas.drawCircle(r, r, r, paint)
            return result
        }
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
        return circleCrop(pool, toTransform)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        // Empty implementation
    }
}