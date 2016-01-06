package com.bignerdranch.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Daniel on 12/28/2015.
 * Background Thread that is also message loop
 */
public class ThumbnailDownloader <T> extends HandlerThread{
//
//    private static final String TAG = "ThumbnailDownloader";
//    //identify messages as download request
//    private static final int MESSAGE_DOWNLOAD = 0;
//
//    //stores reference to Handler responsible for queueing download request
//    private Handler mRequestHandler;
//    //store and retrieve URL associated with a request
//    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
//    private Handler mResponseHandler;
//    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;
//
//
//    //interface used to communicate responses with the requester
//    public interface ThumbnailDownloadListener<T> {
//        //called when image fully downloaded and ready to placed in UI
//        void onThumbnailDownloaded(T target, Bitmap bitmap);
//    }
//
//    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
//        mThumbnailDownloadListener = listener;
//    }
//
//    //Constructor Method
//    public ThumbnailDownloader(Handler responseHandler) {
//        super(TAG);
//
//        mResponseHandler = responseHandler;
//    }
//
//    //called before looper checks for queue create handler implementation here
//    @Override
//    protected void onLooperPrepared() {
//        mRequestHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                //check message type, retrieve object value, then pass it to handleRequest
//                if (msg.what == MESSAGE_DOWNLOAD) {
//                    T target = (T) msg.obj;
//                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
//                    handleRequest(target);
//                }
//            }
//        };
//    }
//
//    //Expects object type <T> to use as an identifier of the url to download
//    public void queueThumbnail (T target, String url) {
//        Log.i(TAG, "Get a URL: " + url);
//
//        if (url == null)
//            mRequestMap.remove(target);
//        else {
//            //the thumbnail is updated with new mapping
//            mRequestMap.put(target, url);
//            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
//        }
//    }
//
//    public void clearQueue() {
//        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
//    }
//
//    //Helper method where downloading happens
//    private void handleRequest(final T target) {
//        try {
//            final String url = mRequestMap.get(target);
//
//            //check existence of URL
//            if (url == null)
//                return;
//
//            //get bitmapFactory to construct bitmap with array of bytes
//            byte[] bitmapByte = new FlickrFetchr().getUrlBytes(url);
//            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
//            Log.i(TAG, "Bitmap created");
//
//            mResponseHandler.post(new Runnable() {
//                public void run() {
//                    if (mRequestMap.get(target) != url) {
//                        return;
//                    }
//
//                    mRequestMap.remove(target);
//                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
//                }
//            });
//
//
//        } catch (IOException e) {
//            Log.e(TAG, "Error downloading image", e);
//        }
//    }
//}
//
//
//package com.bignerdranch.android.photogallery;
//
//        import android.graphics.Bitmap;
//        import android.graphics.BitmapFactory;
//        import android.os.Handler;
//        import android.os.HandlerThread;
//        import android.os.Message;
//        import android.util.Log;
//
//        import java.io.IOException;
//        import java.util.concurrent.ConcurrentHashMap;
//        import java.util.concurrent.ConcurrentMap;
//
//public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    private Handler mRequestHandler;
    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap bitmap);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);

        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        }
    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);

            if (url == null) {
                return;
            }

            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");

            mResponseHandler.post(new Runnable() {
                public void run() {
                    if (mRequestMap.get(target) != url) {
                        return;
                    }

                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            });
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }
}

