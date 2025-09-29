package com.tencent.tcmpp.demo.proxy

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.tencent.tmfmini.sdk.annotation.ProxyService
import com.tencent.tmfmini.sdk.media.MediaImageLoaderProxy
import com.tencent.tmfmini.sdk.media.albumpicker.engine.ImageEngine

@ProxyService(proxy = MediaImageLoaderProxy::class)
class CustomMediaImageLoaderProxy : MediaImageLoaderProxy {
    private val glideImageEngine = GlideImageEngine()

    override fun getCustomImageEngine(): ImageEngine {
        return glideImageEngine
    }

    internal class GlideImageEngine : ImageEngine {

        override fun loadPhoto(context: Context, uri: Uri, imageView: ImageView) {
            Glide.with(context).load(uri).transition(withCrossFade()).into(imageView)
        }

        override fun loadGifAsBitmap(context: Context, gifUri: Uri, imageView: ImageView) {
            Glide.with(context).asBitmap().load(gifUri).into(imageView)
        }

        override fun loadGif(context: Context, gifUri: Uri, imageView: ImageView) {
            Glide.with(context).asGif().load(gifUri).transition(withCrossFade()).into(imageView)
        }

        @Throws(Exception::class)
        override fun getCacheBitmap(context: Context, uri: Uri, width: Int, height: Int): Bitmap {
            return Glide.with(context).asBitmap().load(uri).submit(width, height).get()
        }
    }
}