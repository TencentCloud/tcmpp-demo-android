package com.tencent.tcmpp.demo.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder
import com.nostra13.universalimageloader.core.download.BaseImageDownloader
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import com.nostra13.universalimageloader.utils.StorageUtils
import java.io.File

class UniversalDrawable : Drawable() {
    
    companion object {
        private const val TAG = "UniversalDrawable"
        
        private fun initImageLoader(context: Context) {
            val cacheDir = StorageUtils.getOwnCacheDirectory(context, "imageloader/Cache")
            val config = ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800)
                .diskCacheExtraOptions(480, 800, null)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                .diskCache(UnlimitedDiskCache(cacheDir))
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCache(UnlimitedDiskCache(cacheDir))
                .diskCacheFileNameGenerator(HashCodeFileNameGenerator())
                .imageDownloader(BaseImageDownloader(context))
                .imageDecoder(BaseImageDecoder(true))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .writeDebugLogs()
                .build()

            ImageLoader.getInstance().init(config)
        }
    }
    
    private var mCurrDrawable: Drawable? = null

    override fun draw(canvas: Canvas) {
        mCurrDrawable?.draw(canvas)
    }

    override fun setAlpha(alpha: Int) {
        // Empty implementation
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // Empty implementation
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    fun loadImage(context: Context, uri: String): UniversalDrawable {
        if (!ImageLoader.getInstance().isInited) {
            initImageLoader(context)
        }

        if (!TextUtils.isEmpty(uri)) {
            ImageLoader.getInstance().loadImage(uri, object : SimpleImageLoadingListener() {
                override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                    super.onLoadingComplete(imageUri, view, loadedImage)

                    loadedImage?.let { bitmap ->
                        mCurrDrawable = BitmapDrawable(bitmap).apply {
                            setBounds(bounds)
                        }
                        invalidateSelf()
                    }
                }
            })
        }

        return this
    }
}