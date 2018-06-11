package nsdchat.android.example.com.finalwifichatapplication;

import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Enumeration;

public class StudentActivity extends AppCompatActivity {

    private static final String TAG = "StudentActivity";
    private VrVideoView videoWidgetView;
    private VideoLoaderTask backgroundVideoLoaderTask;
    private Uri fileUri;
    private VrVideoView.Options videoOptions = new VrVideoView.Options();
    private boolean isMuted;
    private boolean isPaused = false;
    private String videoname;

    final int portNum = 3238;
    InetAddress ip = null;
    NetworkInterface networkInterface = null;
    ServerSocket serverSocket;

    private MulticastSocket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String networkSSID = "TeacherStudent";
        if (wifi != null) {

            WifiManager.MulticastLock lock =
                    wifi.createMulticastLock(networkSSID);
            lock.setReferenceCounted(true);
            lock.acquire();
        } else {
            Log.e(networkSSID, "Unable to acquire multicast lock");
            Toast.makeText(getApplicationContext(), "Unable to acquire multicast lock", Toast.LENGTH_SHORT).show();

//            finish();
        }

        videoname = "congo";//getIntent().getStringExtra("videoname");

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
        videoWidgetView.setTouchTrackingEnabled(false);
        videoWidgetView.setFullscreenButtonEnabled(false);
        videoWidgetView.setEventListener(new ActivityEventListener());
        videoWidgetView.getHeadRotation(new float[]{-180, 180});
//        videoWidgetView.setDisplayMode(VrVideoView.DisplayMode.FULLSCREEN_MONO);


        try {
            if (socket == null) {


                Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (enumNetworkInterfaces.hasMoreElements()) {

                    networkInterface = enumNetworkInterfaces.nextElement();
                    Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();

                    while (enumInetAddress.hasMoreElements()) {
                        InetAddress inetAddress = enumInetAddress.nextElement();

                        if (inetAddress.isSiteLocalAddress()) {
                            ip = inetAddress;
                            break;
                        }
                    }
                    if (ip != null) {
                        break;
                    }


                }
                socket = new MulticastSocket(portNum);
                Log.e(TAG, "onCreate: port " + socket.getPort() + "   local post " + socket.getLocalPort());
                socket.setInterface(ip);
                socket.setBroadcast(true);

                InetAddress group = InetAddress.getByName("224.0.0.1");
                socket.joinGroup(new InetSocketAddress(group, portNum), networkInterface);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.getMessage();
        }


        ReceiverMessage recvMsgThread = new ReceiverMessage();
        recvMsgThread.execute((Void) null);
    }


//    https://www.youtube.com/watch?v=VUPM387qyrw
//    http://angrytools.com/android/button/

    private class ReceiverMessage extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {

            Thread newThread = new Thread() {

                public void run() {
                    while (true) {
                        byte[] recvPkt = new byte[1024];
                        DatagramPacket recv = new DatagramPacket(recvPkt, recvPkt.length);
                        try {
                            socket.receive(recv);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        final String medd = new String(recvPkt, 0, recv.getLength());

                        if (medd.equalsIgnoreCase("play")) {
                            play(medd);
                        } else if (medd.equalsIgnoreCase("pause")) {
                            pause();
                        } else {
                            playbySongs(medd);
                        }

                    }
                }
            };
            newThread.start();
            return null;
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

    private void play(String videoname) {

//        this.videoname = videoname;
        if (isPaused) {
            videoWidgetView.playVideo();
            isPaused = false;
        } else {
            if (backgroundVideoLoaderTask != null) {
                // Cancel any task from a previous intent sent to this activity.
                backgroundVideoLoaderTask.cancel(true);
            }
            backgroundVideoLoaderTask = new VideoLoaderTask();
            backgroundVideoLoaderTask.execute(Pair.create(fileUri, videoOptions));
            isPaused = true;
        }
    }

    private void playbySongs(String videoname) {

        this.videoname = videoname;

        if (backgroundVideoLoaderTask != null) {
            // Cancel any task from a previous intent sent to this activity.
            backgroundVideoLoaderTask.cancel(true);
        }
        backgroundVideoLoaderTask = new VideoLoaderTask();
        backgroundVideoLoaderTask.execute(Pair.create(fileUri, videoOptions));
//            isPaused = true;
        isPaused = true;

    }

    private void pause() {
        if (videoWidgetView != null) {
            videoWidgetView.pauseVideo();
            isPaused = true;
        }
    }

    private void stopVideo() {
        if (videoWidgetView != null) {
            videoWidgetView.pauseRendering();
            videoWidgetView.shutdown();
            finish();
        }
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

    @Override
    protected void onDestroy() {
        videoWidgetView.shutdown();
        super.onDestroy();


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
                    videoWidgetView.loadVideoFromAsset(videoname + ".mp4", options);

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
                                .makeText(StudentActivity.this, "Error opening file. ", Toast.LENGTH_LONG)
                                .show();
                    }
                });
                Log.e(TAG, "Could not open video: " + e);
            }

            return true;
        }
    }


}
