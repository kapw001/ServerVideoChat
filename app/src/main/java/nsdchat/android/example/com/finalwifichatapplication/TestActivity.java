package nsdchat.android.example.com.finalwifichatapplication;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import java.io.IOException;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";
    private VrVideoView videoWidgetView;
    private VideoLoaderTask backgroundVideoLoaderTask;
    private Uri fileUri;
    private VrVideoView.Options videoOptions = new VrVideoView.Options();
    private boolean isMuted;
    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        videoOptions.inputFormat = getIntent().getIntExtra("inputFormat", VrVideoView.Options.FORMAT_DEFAULT);
        videoOptions.inputType = getIntent().getIntExtra("inputType", VrVideoView.Options.TYPE_MONO);
        fileUri = getIntent().getData();
        if (fileUri == null) {
            Log.w(TAG, "No data uri specified. Use \"-d /path/filename\".");
        } else {
            Log.i(TAG, "Using file " + fileUri.toString());
        }

//        videoWidgetView = new VrVideoView(this);
        videoWidgetView = (VrVideoView) findViewById(R.id.video_view);
        videoWidgetView.setInfoButtonEnabled(false);
        videoWidgetView.setEventListener(new ActivityEventListener());

        if (backgroundVideoLoaderTask != null) {
            // Cancel any task from a previous intent sent to this activity.
            backgroundVideoLoaderTask.cancel(true);
        }
        backgroundVideoLoaderTask = new VideoLoaderTask();
        backgroundVideoLoaderTask.execute(Pair.create(fileUri, videoOptions));
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Prevent the view from rendering continuously when in the background.
        videoWidgetView.pauseRendering();
        // If the video is playing when onPause() is called, the default behavior will be to pause
        // the video and keep it paused when onResume() is called.
        isPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume the 3D rendering.
        videoWidgetView.resumeRendering();
        // Update the text to account for the paused video in onPause().

    }


    private class VideoLoaderTask extends AsyncTask<Pair<Uri, VrVideoView.Options>, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Pair<Uri, VrVideoView.Options>... fileInformation) {
            try {
                if (fileInformation == null || fileInformation.length < 1
                        || fileInformation[0] == null || fileInformation[0].first == null) {
                    // No intent was specified, so we default to playing the local stereo-over-under video.
                    VrVideoView.Options options = new VrVideoView.Options();
                    options.inputType = VrVideoView.Options.TYPE_STEREO_OVER_UNDER;
//                    videoWidgetView.loadVideoFromAsset("congo.mp4", options);
//                    videoWidgetView.setDisplayMode(VrVideoView.DisplayMode.FULLSCREEN_MONO);
                    videoWidgetView.loadVideoFromAsset("congo.mp4", options);
                } else {
//                    videoWidgetView.setDisplayMode(VrVideoView.DisplayMode.FULLSCREEN_MONO);
                    videoWidgetView.loadVideo(fileInformation[0].first, fileInformation[0].second);
                }
            } catch (IOException e) {
                // Since this is a background thread, we need to switch to the main thread to show a toast.
                videoWidgetView.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast
                                .makeText(TestActivity.this, "Error opening file. ", Toast.LENGTH_LONG)
                                .show();
                    }
                });
                Log.e(TAG, "Could not open video: " + e);
            }

            return true;
        }
    }

    private class ActivityEventListener extends VrVideoEventListener {
        /**
         * Called by video widget on the UI thread when it's done loading the video.
         */
        @Override
        public void onLoadSuccess() {
            Log.i(TAG, "Successfully loaded video " + videoWidgetView.getDuration());

        }

        /**
         * Called by video widget on the UI thread on any asynchronous error.
         */
        @Override
        public void onLoadError(String errorMessage) {

        }

        @Override
        public void onClick() {
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
    }
}
