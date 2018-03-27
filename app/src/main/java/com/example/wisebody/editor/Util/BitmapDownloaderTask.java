package com.example.wisebody.editor.Util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

public class BitmapDownloaderTask extends AsyncTask<String, Void, String> {
    private final WeakReference<View> imageViewReference;
    private final ProgressBar progressBar;
    String url;
    Context context;
    Boolean crop;

    public BitmapDownloaderTask(View imageView, @Nullable ProgressBar progressBar, Context context, @Nullable Boolean crop) {
        imageViewReference = new WeakReference<View>(imageView);
        this.progressBar = progressBar;
        this.context = context;
        this.crop = crop;
    }

    @Override
    // Actual download method, run in the task thread
    protected String doInBackground(String... params) {
        url = (String) params[0];
        return url;
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(String url) {
        if (isCancelled()) {
            url = null;
        }
        if (imageViewReference != null) {
            View imageView = imageViewReference.get();
            BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask((ImageView)imageView);

            if (this == bitmapDownloaderTask) {
                Util util = new Util();
                int radius = (int)util.pxFromDp(context,2);
                if(crop!=null) {
                    if (crop) {
                        int width = (int) util.pxFromDp(context, 135);
                        int height = (int) util.pxFromDp(context, 240);
                        Picasso.with(context).load(url).resize(width, height).centerCrop().into((ImageView)imageView);
                    } else
                        Picasso.with(context).load(url).fit().into((ImageView)imageView);
                }
                else
                    Picasso.with(context).load(url).fit().into((ImageView)imageView);
//                if(progressBar!=null)
//                progressBar.setVisibility(View.GONE);
            }
            bitmapDownloaderTask = null;
        }
    }
    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
            super(Color.BLACK);
            bitmapDownloaderTaskReference =
                    new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }
    public void download(String url, ImageView imageView) {
        if (cancelPotentialDownload(url, imageView)) {
           /* BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, progressBar);*/
            DownloadedDrawable downloadedDrawable = new DownloadedDrawable(this);
            imageView.setImageDrawable(downloadedDrawable);
            this.execute(url);
        }
    }
    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }
    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }
}