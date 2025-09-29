package com.tencent.tcmpp.demo.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.tencent.tcmpp.demo.ui.GlideCircleTransform
import java.io.File

object GlideUtil {
    private const val TAG = "GlideUtil"

    // GlideLoader
    private var sGlideLoader: GlideLoader? = null
    private var sContext: Context? = null
    private var sImageLoadingRes = 0
    private var sImageUriErrorRes = 0
    private var sImageFailRes = 0
    private val DF_OPTIONS = defaultOptions()

    // ================================
    // =  GlideLoader(RequestManager) =
    // ================================

    fun with(context: Context): GlideLoader {
        return GlideLoader(Glide.with(context))
    }

    fun with(activity: Activity): GlideLoader {
        return GlideLoader(Glide.with(activity))
    }

    fun with(activity: FragmentActivity): GlideLoader {
        return GlideLoader(Glide.with(activity))
    }

    fun with(fragment: android.app.Fragment): GlideLoader {
        return GlideLoader(Glide.with(fragment))
    }

    fun with(fragment: Fragment): GlideLoader {
        return GlideLoader(Glide.with(fragment))
    }

    fun with(view: View): GlideLoader {
        return GlideLoader(Glide.with(view))
    }

    fun with(): GlideLoader? {
        if (sGlideLoader == null) {
            try {
                sContext?.let {
                    sGlideLoader = GlideLoader(Glide.with(it))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return sGlideLoader
    }

    fun init(context: Context) {
        if (sContext == null) {
            sContext = context.applicationContext
            with()
        }
    }

    fun cloneImageOptions(options: RequestOptions?): RequestOptions? {
        return options?.clone()
    }

    fun defaultOptions(): RequestOptions {
        return RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(sImageLoadingRes)
            .fallback(sImageUriErrorRes)
            .error(sImageFailRes)
            .priority(Priority.HIGH)
    }

    fun emptyOptions(): RequestOptions {
        return RequestOptions()
    }

    fun skipCacheOptions(): RequestOptions {
        return skipCacheOptions(cloneImageOptions(DF_OPTIONS)) ?: RequestOptions()
    }

    fun skipCacheOptions(options: RequestOptions?): RequestOptions? {
        return options?.diskCacheStrategy(DiskCacheStrategy.NONE)?.skipMemoryCache(true)
    }

    fun getLoadResOptions(@DrawableRes loadingRes: Int): RequestOptions? {
        return getLoadResOptions(cloneImageOptions(DF_OPTIONS), loadingRes)
    }

    fun getLoadResOptions(options: RequestOptions?, @DrawableRes loadingRes: Int): RequestOptions? {
        if (options != null && loadingRes != 0) {
            options.placeholder(loadingRes)
                .fallback(loadingRes)
                .error(loadingRes)
        }
        return options
    }

    fun transformationOptions(transformation: Transformation<Bitmap>): RequestOptions? {
        return transformationOptions(cloneImageOptions(DF_OPTIONS), transformation)
    }

    fun transformationOptions(options: RequestOptions?, transformation: Transformation<Bitmap>): RequestOptions? {
        if (options != null) {
            try {
                options.transform(transformation)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return options
    }

    fun clearDiskCache() {
        Thread {
            try {
                // This method must be called on a background thread.
                sContext?.let { Glide.get(it).clearDiskCache() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun clearMemoryCache() {
        try {
            // This method must be called on the main thread.
            sContext?.let { Glide.get(it).clearMemory() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onLowMemory() {
        try {
            sContext?.let { Glide.get(it).onLowMemory() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getDiskCache(): File? {
        return try {
            sContext?.let { Glide.getPhotoCacheDir(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    class GlideLoader(private val mRequestManager: RequestManager?) {

        init {
            mRequestManager?.setDefaultRequestOptions(DF_OPTIONS)
        }

        fun preload(uri: String) {
            preload(uri, null)
        }

        fun preload(uri: String, options: RequestOptions?) {
            mRequestManager?.let {
                if (options != null) {
                    it.asBitmap().load(uri).apply(options).preload()
                } else {
                    it.asBitmap().load(uri).preload()
                }
            }
        }

        fun displayImage(uri: String, imageView: ImageView) {
            displayImage(uri, imageView, null)
        }

        fun displayCircleImage(uri: String, imageView: ImageView) {
            val requestOptions = RequestOptions()
            requestOptions.transform(GlideCircleTransform())
            displayImage(uri, imageView, requestOptions)
        }

        fun displayCircleImage(resId: Int, imageView: ImageView) {
            val requestOptions = RequestOptions()
            requestOptions.transform(GlideCircleTransform())
            displayImage(resId, imageView, requestOptions)
        }

        fun displayImage(uri: String, imageView: ImageView?, options: RequestOptions?) {
            if (mRequestManager != null && imageView != null) {
                if (options != null) {
                    mRequestManager.asBitmap().load(uri).apply(options).into(imageView)
                } else {
                    mRequestManager.asBitmap().load(uri).into(imageView)
                }
            }
        }

        fun displayImage(resId: Int, imageView: ImageView?, options: RequestOptions?) {
            if (mRequestManager != null && imageView != null) {
                if (options != null) {
                    mRequestManager.asDrawable().load(resId).apply(options).into(imageView)
                } else {
                    mRequestManager.asDrawable().load(resId).into(imageView)
                }
            }
        }

        fun displayImageToGif(uri: String, imageView: ImageView) {
            displayImageToGif(uri, imageView, null)
        }

        fun displayImageToGif(uri: String, imageView: ImageView?, options: RequestOptions?) {
            if (mRequestManager != null && imageView != null) {
                if (options != null) {
                    mRequestManager.asGif().load(uri).apply(options).into(imageView)
                } else {
                    mRequestManager.asGif().load(uri).into(imageView)
                }
            }
        }

        fun loadImageBitmap(uri: String, target: Target<Bitmap>) {
            loadImageBitmap(uri, target, null)
        }

        fun loadImageBitmap(uri: String, target: Target<Bitmap>, options: RequestOptions?) {
            mRequestManager?.let {
                if (options != null) {
                    it.asBitmap().load(uri).apply(options).into(target)
                } else {
                    it.asBitmap().load(uri).into(target)
                }
            }
        }

        fun loadImageDrawable(uri: String, target: Target<Drawable>) {
            loadImageDrawable(uri, target, null)
        }

        fun loadImageDrawable(uri: String, target: Target<Drawable>, options: RequestOptions?) {
            mRequestManager?.let {
                if (options != null) {
                    it.asDrawable().load(uri).apply(options).into(target)
                } else {
                    it.asDrawable().load(uri).into(target)
                }
            }
        }

        fun loadImageFile(uri: String, target: Target<File>) {
            loadImageFile(uri, target, null)
        }

        fun loadImageFile(uri: String, target: Target<File>, options: RequestOptions?) {
            mRequestManager?.let {
                if (options != null) {
                    it.asFile().load(uri).apply(options).into(target)
                } else {
                    it.asFile().load(uri).into(target)
                }
            }
        }

        fun loadImageGif(uri: String, target: Target<GifDrawable>) {
            loadImageGif(uri, target, null)
        }

        fun loadImageGif(uri: String, target: Target<GifDrawable>, options: RequestOptions?) {
            mRequestManager?.let {
                if (options != null) {
                    it.asGif().load(uri).apply(options).into(target)
                } else {
                    it.asGif().load(uri).into(target)
                }
            }
        }

        fun cancelDisplayTask(view: View?) {
            if (mRequestManager != null && view != null) {
                mRequestManager.clear(view)
            }
        }

        fun cancelDisplayTask(target: Target<*>?) {
            if (mRequestManager != null && target != null) {
                mRequestManager.clear(target)
            }
        }

        fun destroy() {
            mRequestManager?.onDestroy()
        }

        fun pause() {
            mRequestManager?.pauseAllRequests()
        }

        fun resume() {
            mRequestManager?.resumeRequests()
        }

        fun stop() {
            mRequestManager?.onStop()
        }

        fun start() {
            mRequestManager?.onStart()
        }
    }
}