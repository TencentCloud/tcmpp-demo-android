package com.tencent.tcmpp.demo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import java.io.File;

/**
 * demo app 临时用的异步drawable
 */
public class UniversalDrawable extends Drawable {

    private final static String TAG = "UniversalDrawable";
    private Drawable mCurrDrawable;

    @Override
    public void draw(Canvas canvas) {
        if (this.mCurrDrawable != null) {
            this.mCurrDrawable.draw(canvas);
        }
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    public UniversalDrawable loadImage(Context context, String uri) {
        if (!ImageLoader.getInstance().isInited()) {
            initImageLoader(context);
        }

        if (!TextUtils.isEmpty(uri)) {
            ImageLoader.getInstance().loadImage(uri, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);

                    mCurrDrawable = new BitmapDrawable(loadedImage);
                    mCurrDrawable.setBounds(getBounds());
                    invalidateSelf();
                }
            });
        }

        return this;
    }

    /**
     * 初始化Universal Image Loader
     */
    static private void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "imageloader/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800)//设置缓存图片的默认尺寸,一般取设备的屏幕尺寸
                .diskCacheExtraOptions(480, 800, null)
                .threadPoolSize(3)// 线程池内加载的数量,default = 3
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))//自定义内存的缓存策略
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)// default
                .diskCache(new UnlimitedDiskCache(cacheDir))// default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)//缓存的文件数量
                .diskCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())// default
                .imageDownloader(new BaseImageDownloader(context))// default
                .imageDecoder(new BaseImageDecoder(true))// default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())// default
                .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
    }
}
