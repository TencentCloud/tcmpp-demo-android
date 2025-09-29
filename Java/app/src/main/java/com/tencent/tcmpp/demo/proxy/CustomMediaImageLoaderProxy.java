package com.tencent.tcmpp.demo.proxy;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.tencent.tmfmini.sdk.annotation.ProxyService;
import com.tencent.tmfmini.sdk.media.MediaImageLoaderProxy;
import com.tencent.tmfmini.sdk.media.albumpicker.engine.ImageEngine;

@ProxyService(proxy = MediaImageLoaderProxy.class)

public class CustomMediaImageLoaderProxy implements MediaImageLoaderProxy {
    private GlideImageEngine glideImageEngine = new GlideImageEngine();

    @Override
    public ImageEngine getCustomImageEngine() {
        return glideImageEngine;
    }

    static class GlideImageEngine implements ImageEngine {

        @Override
        public void loadPhoto(@NonNull Context context, @NonNull Uri uri, @NonNull ImageView imageView) {
            Glide.with(context).load(uri).transition(withCrossFade()).into(imageView);

        }

        @Override
        public void loadGifAsBitmap(@NonNull Context context, @NonNull Uri gifUri, @NonNull ImageView imageView) {
            Glide.with(context).asBitmap().load(gifUri).into(imageView);

        }

        @Override
        public void loadGif(@NonNull Context context, @NonNull Uri gifUri, @NonNull ImageView imageView) {
            Glide.with(context).asGif().load(gifUri).transition(withCrossFade()).into(imageView);

        }

        @Override
        public Bitmap getCacheBitmap(@NonNull Context context, @NonNull Uri uri, int width, int height)
                throws Exception {
            return Glide.with(context).asBitmap().load(uri).submit(width, height).get();
        }
    }
}
