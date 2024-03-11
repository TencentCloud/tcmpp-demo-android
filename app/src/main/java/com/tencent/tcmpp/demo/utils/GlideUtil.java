package com.tencent.tcmpp.demo.utils;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.tencent.tcmpp.demo.ui.GlideCircleTransform;

import java.io.File;

public final class GlideUtil {

    private static final String TAG = "GlideUtil";
    // GlideLoader
    private static GlideLoader sGlideLoader;
    private static Context sContext;
    private static int sImageLoadingRes = 0;
    private static int sImageUriErrorRes = 0;
    private static int sImageFailRes = 0;
    private static RequestOptions DF_OPTIONS = defaultOptions();

    private GlideUtil() {
    }

    // ================================
    // =  GlideLoader(RequestManager) =
    // ================================

    @NonNull
    public static GlideLoader with(@NonNull Context context) {
        return new GlideLoader(Glide.with(context));
    }

    @NonNull
    public static GlideLoader with(@NonNull Activity activity) {
        return new GlideLoader(Glide.with(activity));
    }

    @NonNull
    public static GlideLoader with(@NonNull FragmentActivity activity) {
        return new GlideLoader(Glide.with(activity));
    }

    @NonNull
    public static GlideLoader with(@NonNull android.app.Fragment fragment) {
        return new GlideLoader(Glide.with(fragment));
    }

    @NonNull
    public static GlideLoader with(@NonNull Fragment fragment) {
        return new GlideLoader(Glide.with(fragment));
    }

    @NonNull
    public static GlideLoader with(@NonNull View view) {
        return new GlideLoader(Glide.with(view));
    }

    public static GlideLoader with() {
        if (sGlideLoader == null) {
            try {
                sGlideLoader = new GlideLoader(Glide.with(sContext));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sGlideLoader;
    }

    public static void init(final Context context) {
        if (sContext == null && context != null) {
            // 设置全局 Context
            sContext = context.getApplicationContext();
            // 默认进行初始化
            with();
        }
    }

    public static RequestOptions cloneImageOptions(final RequestOptions options) {
        return (options != null) ? options.clone() : null;
    }

    public static RequestOptions defaultOptions() {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(sImageLoadingRes)
                .fallback(sImageUriErrorRes)
                .error(sImageFailRes)
                .priority(Priority.HIGH);
        return requestOptions;
    }

    public static RequestOptions emptyOptions() {
        return new RequestOptions();
    }

    public static RequestOptions skipCacheOptions() {
        return skipCacheOptions(cloneImageOptions(DF_OPTIONS));
    }

    public static RequestOptions skipCacheOptions(final RequestOptions options) {
        if (options != null) {
            return options.diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
        }
        return options;
    }

    public static RequestOptions getLoadResOptions(@DrawableRes final int loadingRes) {
        return getLoadResOptions(cloneImageOptions(DF_OPTIONS), loadingRes);
    }

    public static RequestOptions getLoadResOptions(final RequestOptions options, @DrawableRes final int loadingRes) {
        if (options != null && loadingRes != 0) {
            options.placeholder(loadingRes) // 设置图片在下载期间显示的图片
                    .fallback(loadingRes) // 设置图片 Uri 为空或是错误的时候显示的图片
                    .error(loadingRes); // 设置图片 ( 加载 / 解码 ) 过程中错误时候显示的图片
        }
        return options;
    }

    public static RequestOptions transformationOptions(final Transformation transformation) {
        return transformationOptions(cloneImageOptions(DF_OPTIONS), transformation);
    }

    public static RequestOptions transformationOptions(final RequestOptions options,
                                                       final Transformation transformation) {
        if (options != null) {
            try {
                options.transform(transformation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return options;
    }

    public static void clearDiskCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // This method must be called on a background thread.
                    Glide.get(sContext).clearDiskCache();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void clearMemoryCache() {
        try {
            // This method must be called on the main thread.
            Glide.get(sContext).clearMemory(); // 必须在主线程上调用该方法
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onLowMemory() {
        try {
            Glide.get(sContext).onLowMemory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File getDiskCache() {
        try {
            return Glide.getPhotoCacheDir(sContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public final static class GlideLoader {

        // RequestManager
        private RequestManager mRequestManager;

        public GlideLoader(RequestManager requestManager) {
            this.mRequestManager = requestManager;
            if (requestManager != null) {
                requestManager.setDefaultRequestOptions(DF_OPTIONS);
            }
        }

        public void preload(final String uri) {
            preload(uri, null);
        }

        public void preload(final String uri, final RequestOptions options) {
            if (mRequestManager != null) {
                if (options != null) {
                    mRequestManager.asBitmap().load(uri).apply(options).preload();
                } else {
                    mRequestManager.asBitmap().load(uri).preload();
                }
            }
        }

        public void displayImage(final String uri, final ImageView imageView) {
            displayImage(uri, imageView, null);
        }

        public void displayCircleImage(final String uri, final ImageView imageView) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.transform(new GlideCircleTransform());
            displayImage(uri, imageView, requestOptions);
        }

        public void displayCircleImage(final int resId, final ImageView imageView) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.transform(new GlideCircleTransform());
            displayImage(resId, imageView, requestOptions);
        }

        public void displayImage(final String uri, final ImageView imageView, final RequestOptions options) {
            if (mRequestManager != null && imageView != null) {
                if (options != null) {
                    mRequestManager.asBitmap().load(uri).apply(options).into(imageView);
                } else {
                    mRequestManager.asBitmap().load(uri).into(imageView);
                }
            }
        }

        public void displayImage(final int redId, final ImageView imageView, final RequestOptions options) {
            if (mRequestManager != null && imageView != null) {
                if (options != null) {
                    mRequestManager.asDrawable().load(redId).apply(options).into(imageView);
                } else {
                    mRequestManager.asDrawable().load(redId).into(imageView);
                }
            }
        }

        public void displayImageToGif(final String uri, final ImageView imageView) {
            displayImageToGif(uri, imageView, null);
        }

        public void displayImageToGif(final String uri, final ImageView imageView, final RequestOptions options) {
            if (mRequestManager != null && imageView != null) {
                if (options != null) {
                    mRequestManager.asGif().load(uri).apply(options).into(imageView);
                } else {
                    mRequestManager.asGif().load(uri).into(imageView);
                }
            }
        }

        public void loadImageBitmap(final String uri, final Target<Bitmap> target) {
            loadImageBitmap(uri, target, null);
        }

        public void loadImageBitmap(final String uri, final Target<Bitmap> target, final RequestOptions options) {
            if (mRequestManager != null) {
                if (options != null) {
                    mRequestManager.asBitmap().load(uri).apply(options).into(target);
                } else {
                    mRequestManager.asBitmap().load(uri).into(target);
                }
            }
        }

        public void loadImageDrawable(final String uri, final Target<Drawable> target) {
            loadImageDrawable(uri, target, null);
        }

        public void loadImageDrawable(final String uri, final Target<Drawable> target, final RequestOptions options) {
            if (mRequestManager != null) {
                if (options != null) {
                    mRequestManager.asDrawable().load(uri).apply(options).into(target);
                } else {
                    mRequestManager.asDrawable().load(uri).into(target);
                }
            }
        }

        public void loadImageFile(final String uri, final Target<File> target) {
            loadImageFile(uri, target, null);
        }

        public void loadImageFile(final String uri, final Target<File> target, final RequestOptions options) {
            if (mRequestManager != null) {
                if (options != null) {
                    mRequestManager.asFile().load(uri).apply(options).into(target);
                } else {
                    mRequestManager.asFile().load(uri).into(target);
                }
            }
        }

        public void loadImageGif(final String uri, final Target<GifDrawable> target) {
            loadImageGif(uri, target, null);
        }

        public void loadImageGif(final String uri, final Target<GifDrawable> target, final RequestOptions options) {
            if (mRequestManager != null) {
                if (options != null) {
                    mRequestManager.asGif().load(uri).apply(options).into(target);
                } else {
                    mRequestManager.asGif().load(uri).into(target);
                }
            }
        }

        public void cancelDisplayTask(final View view) {
            if (mRequestManager != null && view != null) {
                mRequestManager.clear(view);
            }
        }

        public void cancelDisplayTask(final Target target) {
            if (mRequestManager != null && target != null) {
                mRequestManager.clear(target);
            }
        }

        public void destroy() {
            if (mRequestManager != null) {
                mRequestManager.onDestroy();
            }
        }

        public void pause() {
            if (mRequestManager != null) {
                mRequestManager.pauseAllRequests();
            }
        }

        public void resume() {
            if (mRequestManager != null) {
                mRequestManager.resumeRequests();
            }
        }

        public void stop() {
            if (mRequestManager != null) {
                mRequestManager.onStop();
            }
        }

        public void start() {
            if (mRequestManager != null) {
                mRequestManager.onStart();
            }
        }
    }
}

