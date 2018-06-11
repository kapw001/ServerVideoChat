package nsdchat.android.example.com.finalwifichatapplication;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import java.io.IOException;

/**
 * Created by yasar on 10/1/18.
 */

public class VideoPlayer extends VrVideoEventListener {

    private static final String TAG = "VideoPlayer";
    private VrVideoView videoWidgetView;
    public static final int LOAD_VIDEO_STATUS_UNKNOWN = 0;
    public static final int LOAD_VIDEO_STATUS_SUCCESS = 1;
    public static final int LOAD_VIDEO_STATUS_ERROR = 2;
    private int loadVideoStatus = LOAD_VIDEO_STATUS_UNKNOWN;
    private VideoLoaderTask backgroundVideoLoaderTask;
    private boolean isPaused = false;
    private Context context;

    public VideoPlayer(Context context, VrVideoView vrVideoView) {

        this.videoWidgetView = vrVideoView;
        this.context = context;
        VrVideoView.Options videoOptions = new VrVideoView.Options();
        videoOptions.inputFormat = VrVideoView.Options.FORMAT_DEFAULT;
        videoOptions.inputType = VrVideoView.Options.TYPE_MONO;
        videoWidgetView.setEventListener(this);

        loadVideoStatus = LOAD_VIDEO_STATUS_UNKNOWN;

        if (backgroundVideoLoaderTask != null) {
            // Cancel any task from a previous intent sent to this activity.
            backgroundVideoLoaderTask.cancel(true);
        }
        backgroundVideoLoaderTask = new VideoLoaderTask();
        backgroundVideoLoaderTask.execute(Pair.create(Uri.EMPTY, videoOptions));

    }


    public void onPause() {

        // Prevent the view from rendering continuously when in the background.
        videoWidgetView.pauseRendering();
        // If the video is playing when onPause() is called, the default behavior will be to pause
        // the video and keep it paused when onResume() is called.
        isPaused = true;
    }


    public void onResume() {

        // Resume the 3D rendering.
        videoWidgetView.resumeRendering();
        videoWidgetView.playVideo();
        isPaused = false;

    }

    public void togglePause() {
        try {

            if (isPaused) {
                videoWidgetView.playVideo();
            } else {
                videoWidgetView.pauseVideo();
            }
            isPaused = !isPaused;
        } catch (NumberFormatException e) {

        }
    }

    @Override
    public void onLoadSuccess() {
        Log.i(TAG, "Successfully loaded video " + videoWidgetView.getDuration());
        loadVideoStatus = LOAD_VIDEO_STATUS_SUCCESS;
    }

    /**
     * Called by video widget on the UI thread on any asynchronous error.
     */
    @Override
    public void onLoadError(String errorMessage) {
        // An error here is normally due to being unable to decode the video format.
        loadVideoStatus = LOAD_VIDEO_STATUS_ERROR;
        Toast.makeText(
                context, "Error loading video: " + errorMessage, Toast.LENGTH_LONG)
                .show();
        Log.e(TAG, "Error loading video: " + errorMessage);
    }

    @Override
    public void onClick() {
        togglePause();
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
        videoWidgetView.seekTo(0);
    }

    class VideoLoaderTask extends AsyncTask<Pair<Uri, VrVideoView.Options>, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Pair<Uri, VrVideoView.Options>... fileInformation) {

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    final VrVideoView.Options options = new VrVideoView.Options();
                    options.inputType = VrVideoView.Options.TYPE_STEREO_OVER_UNDER;
                    try {
                        videoWidgetView.loadVideoFromAsset("congo.mp4", options);
                        isPaused = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                        videoWidgetView.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast
                                        .makeText(context, "Error opening file. ", Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                        Log.e(TAG, "Could not open video: " + e);
                    }


                }
            });

            return true;
        }
    }


}
