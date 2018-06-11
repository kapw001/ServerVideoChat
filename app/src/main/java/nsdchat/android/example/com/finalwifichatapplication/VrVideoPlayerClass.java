package nsdchat.android.example.com.finalwifichatapplication;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import java.io.File;
import java.io.IOException;

/**
 * Created by yasar on 6/2/18.
 */

public class VrVideoPlayerClass {

    private static final String TAG = "VrVideoPlayerClass";

    private VrVideoView mVrVideoView;
    private Context mContext;
    private boolean isPaused = false;
    private ScoketClass scoketClass;


    public VrVideoPlayerClass(VrVideoView mVrVideoView, Context mContext, ScoketClass scoketClass) {
        this.mVrVideoView = mVrVideoView;
        this.mContext = mContext;
        this.scoketClass = scoketClass;
    }

    private void init() {

        mVrVideoView.setInfoButtonEnabled(false);
        mVrVideoView.setEventListener(new ActivityEventListener());

    }


    public void onPause() {

        mVrVideoView.pauseRendering();
        isPaused = true;

    }

    public void onResume() {

        mVrVideoView.resumeRendering();

    }


    public void play(final String videoName) {


        new Handler().post(new Runnable() {
            @Override
            public void run() {

                VrVideoView.Options options = new VrVideoView.Options();
                options.inputType = VrVideoView.Options.TYPE_STEREO_OVER_UNDER;
//                    videoWidgetView.loadVideoFromAsset("congo.mp4", options);
//                    videoWidgetView.setDisplayMode(VrVideoView.DisplayMode.FULLSCREEN_MONO);
                try {

                    File file = new File(videoName);
                    mVrVideoView.loadVideo(Uri.fromFile(file), options);
                    mVrVideoView.playVideo();
                    isPaused = false;

                } catch (IOException e) {

                    mVrVideoView.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast
                                    .makeText(mContext, "Error opening file. ", Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                    Log.e(TAG, "Could not open video: " + e);
                }

            }
        });
//
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVrVideoView.setDisplayMode(VrVideoView.DisplayMode.FULLSCREEN_MONO);
            }
        });

    }


    private class ActivityEventListener extends VrVideoEventListener {
        /**
         * Called by video widget on the UI thread when it's done loading the video.
         */
        @Override
        public void onLoadSuccess() {
            Log.i(TAG, "Successfully loaded video " + mVrVideoView.getDuration());

        }

        /**
         * Called by video widget on the UI thread on any asynchronous error.
         */
        @Override
        public void onLoadError(String errorMessage) {
            // An error here is normally due to being unable to decode the video format.

            Toast.makeText(mContext, "Error loading video: " + errorMessage, Toast.LENGTH_LONG)
                    .show();
            Log.e(TAG, "Error loading video: " + errorMessage);
        }

        @Override
        public void onClick() {

            if (isPaused) {
                mVrVideoView.playVideo();
            } else {
                mVrVideoView.pauseVideo();
            }
            isPaused = !isPaused;

            scoketClass.sendMessages("playpause");
        }

        /**
         * Update the UI every frame.
         */
        @Override
        public void onNewFrame() {

        }

        /**
         * Make the video play in a loop. This method could also be used to move to the next video in
         * a playlist.
         */
        @Override
        public void onCompletion() {
//            vrVideoView.seekTo(0);
        }
    }
}
