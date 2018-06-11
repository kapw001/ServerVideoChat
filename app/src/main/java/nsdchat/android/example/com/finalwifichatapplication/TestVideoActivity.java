package nsdchat.android.example.com.finalwifichatapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.vr.sdk.widgets.video.VrVideoView;

public class TestVideoActivity extends AppCompatActivity implements WifiConnectToPassMessages.ReceiveSocketMessage {

    private static final String TAG = "TestVideoActivity";

    private VideoPlayer videoPlayer;

    private WifiConnectToPassMessages wifiConnectToPassMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_video);

        VrVideoView videoWidgetView = (VrVideoView) findViewById(R.id.video_view);
        videoPlayer = new VideoPlayer(this, videoWidgetView);
        wifiConnectToPassMessages = new WifiConnectToPassMessages(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        wifiConnectToPassMessages.onResume();
        videoPlayer.onResume();

    }

    /**
     *
     */
    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiConnectToPassMessages.onDestroy();
    }


    /**
     * @param msg this is receive message from teacher
     */
    @Override
    public void onMessages(String msg) {

//        wifiConnectToPassMessages.sendMessages(msg);

        Log.e(TAG, "onMessages: " + msg);

    }
}
