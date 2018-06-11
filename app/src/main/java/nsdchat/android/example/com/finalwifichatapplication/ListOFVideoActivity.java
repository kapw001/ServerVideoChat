package nsdchat.android.example.com.finalwifichatapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import nsdchat.android.example.com.finalwifichatapplication.adapter.MyRecyclerViewAdapterTest;
import nsdchat.android.example.com.finalwifichatapplication.model.Videos;

public class ListOFVideoActivity extends AppCompatActivity implements MyRecyclerViewAdapterTest.OnItemClickListener {

    private static final String[] READ_AND_WRITE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int RC_READ_AND_WRITE = 123;
    private static final String TAG = "ListOFVideoActivity";
    private final int portNum = 45238;
    private InetAddress ip = null;
    private NetworkInterface networkInterface = null;
    private MulticastSocket socket;
    private InetAddress group;
    private VrVideoView vrVideoView;
    private List<Videos> mList;
    private RecyclerView recyclerView;
    private MyRecyclerViewAdapterTest mRecyclerAdapter;
    private boolean isPaused = false;

    private View currentFocusedLayout, oldFocusedLayout;
    private RelativeLayout main;

    private RelativeLayout playpause;

    private TextView error;

    private LinearLayout second;
    private ImageView idelImg;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_of_videos);


        main = (RelativeLayout) findViewById(R.id.main);
        second = (LinearLayout) findViewById(R.id.second);
        error = (TextView) findViewById(R.id.error);
        playpause = (RelativeLayout) findViewById(R.id.playpause);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");

        Retry();


    }


    private void Retry() {
//        if (getWifiManager().isWifiEnabled()) {

        //wifi is enabled
        main.setVisibility(View.VISIBLE);
        second.setVisibility(View.GONE);
        enableVideoList();

//        } else {
//
//            main.setVisibility(View.GONE);
//            second.setVisibility(View.VISIBLE);
//            Toast.makeText(this, "There is no wifi please enabled ", Toast.LENGTH_SHORT).show();
//
//        }
    }


    private void enableVideoList() {

        String networkSSID = "TeacherStudent";
        if (getWifiManager() != null) {
            WifiManager.MulticastLock lock =
                    getWifiManager().createMulticastLock(networkSSID);
            lock.setReferenceCounted(true);
            lock.acquire();

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

//                    Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

                    group = InetAddress.getByName("224.0.0.1");
                    socket.joinGroup(new InetSocketAddress(group, portNum), networkInterface);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log.e(networkSSID, "Unable to acquire multicast lock");
            Toast.makeText(getApplicationContext(), "Unable to acquire multicast lock", Toast.LENGTH_SHORT).show();

//                finish();
        }

        mList = new ArrayList<>();

//        mList.add(new Videos("ajourneytovenus", "A Journey to Venus"));
//        mList.add(new Videos("atourtothecell", "A Tour to the Cell"));
//        mList.add(new Videos("thepyramids", "The Pyramids"));
//        mList.add(new Videos("tourtojapan", "Tour to Japan"));
//        mList.add(new Videos("digestivesystem", "Digestive System"));
//        mList.add(new Videos("skeletalsystem", "Skeletal System"));


        vrVideoView = (VrVideoView) findViewById(R.id.video_view);
        vrVideoView.setInfoButtonEnabled(false);
        mRecyclerAdapter = new MyRecyclerViewAdapterTest(this, mList);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mRecyclerAdapter);
        recyclerView.setHasFixedSize(true);

        methodRequiresTwoPermission();

        vrVideoView.setEventListener(new ActivityEventListener());

    }

    private void methodRequiresTwoPermission() {


        if (RuntimePermissionRequest.checkMultiplePermission(this, READ_AND_WRITE)) {

            RuntimePermissionRequest.requestPermissionMultiple(this, READ_AND_WRITE, RC_READ_AND_WRITE, "Permission", "Please accept read and write permission to read internal storage ");

        } else {

            getAllFiles();

        }


    }

    @Override
    protected void onPause() {
        super.onPause();

        new sendMessage("stop").execute();
//        if (getWifiManager().isWifiEnabled()) {
        // Prevent the view from rendering continuously when in the background.
        vrVideoView.pauseRendering();
        playpause.setVisibility(View.VISIBLE);

        // If the video is playing when onPause() is called, the default behavior will be to pause
        // the video and keep it paused when onResume() is called.
        isPaused = true;
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume the 3D rendering.


//        if (getWifiManager().isWifiEnabled()) {

        try {
            socket = new MulticastSocket(portNum);
            socket.setInterface(ip);
            socket.setBroadcast(true);

            group = InetAddress.getByName("224.0.0.1");
            socket.joinGroup(new InetSocketAddress(group, portNum), networkInterface);
        } catch (IOException e) {
            e.printStackTrace();
        }

        vrVideoView.resumeRendering();
//            vrVideoView.playVideo();
//            new sendMessage("play").execute();
//        } else {
//
//            Toast.makeText(this, "There is no wifi please enabled ", Toast.LENGTH_SHORT).show();
//
//        }

    }

    public WifiManager getWifiManager() {
        return (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    public void onRetry(View view) {

        Retry();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RC_READ_AND_WRITE) {

            if (RuntimePermissionRequest.verifyPermissions(grantResults)) {

//                    Toast.makeText(this, "All multiple Permission granted", Toast.LENGTH_SHORT).show();
                getAllFiles();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {

                Toast.makeText(this, "Please accept read and write permission to read internal storage", Toast.LENGTH_SHORT).show();

                recyclerView.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);
                error.setText("Please accept read and write permission to read all vr videos from internal storage");
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }


    private void getAllFiles() {


        new LoadData().execute();


    }

    private String[] cl = {"XI", "VI", "VII", "X", "V"};
    Random random = new Random();

    class LoadData extends AsyncTask<Void, Void, List<Videos>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            if (progressDialog != null) progressDialog.show();
        }

        @Override
        protected List<Videos> doInBackground(Void... voids) {
            String path = Environment.getExternalStorageDirectory().toString() + "/vrvideos";

//        ArrayList<Videos> videosArrayList = new ArrayList<Videos>();


            mList.clear();
            File directory = new File(path);
            File[] file = directory.listFiles();
            for (int i = 0; i < file.length; i++) {
                int n = random.nextInt(cl.length - 1);

                String c = cl[n];
                mList.add(new Videos(file[i].getName(), file[i].getAbsolutePath(), c));

//            Log.e(TAG, "getAllFiles: " + file[i].getName() + "   " + file[i].getAbsolutePath());

            }

            return mList;
        }

        @Override
        protected void onPostExecute(List<Videos> videos) {
            super.onPostExecute(videos);
//            if (progressDialog != null) progressDialog.cancel();
            mRecyclerAdapter.updateData(mList);
            recyclerView.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);

        }
    }

    private class sendMessage extends AsyncTask<Void, Void, Boolean> {

        String textMsg;

        sendMessage(String message) {

            textMsg = message;
        }

        @Override
        protected Boolean doInBackground(Void... params) {


            byte[] data = textMsg.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, group, portNum);

            Log.e(TAG, "doInBackground: " + data);

            try {
                if (socket != null) {

                    socket.send(packet);
                    Log.e(TAG, "doInBackground: Test " + packet.getData().toString() + packet.getData().length);

                    return true;
                }
                return false;

            } catch (IOException e) {
                return false;
            } catch (RuntimeException e) {
                return false;
            }

        }
    }


    @Override
    public void OnItem(View v, int position) {

//        Toast.makeText(this, "called ", Toast.LENGTH_SHORT).show();

        vrVideoView.setVisibility(View.VISIBLE);

        final String videoName = mList.get(position).getVideoLink();
        final String video = mList.get(position).getTitle();

        new sendMessage(video).execute();


//        Toast.makeText(this, "" + video, Toast.LENGTH_SHORT).show();

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                VrVideoView.Options options = new VrVideoView.Options();
                options.inputType = VrVideoView.Options.TYPE_STEREO_OVER_UNDER;
//                    videoWidgetView.loadVideoFromAsset("congo.mp4", options);
//                    videoWidgetView.setDisplayMode(VrVideoView.DisplayMode.FULLSCREEN_MONO);
                try {

                    File file = new File(videoName);
                    vrVideoView.loadVideo(Uri.fromFile(file), options);
                    vrVideoView.playVideo();
                    isPaused = false;

                } catch (IOException e) {

                    vrVideoView.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast
                                    .makeText(ListOFVideoActivity.this, "Error opening file. ", Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                    Log.e(TAG, "Could not open video: " + e);
                }

            }
        });
//
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                vrVideoView.setDisplayMode(VrVideoView.DisplayMode.FULLSCREEN_MONO);
            }
        });

    }

    private class ActivityEventListener extends VrVideoEventListener {
        /**
         * Called by video widget on the UI thread when it's done loading the video.
         */
        @Override
        public void onLoadSuccess() {
            Log.i(TAG, "Successfully loaded video " + vrVideoView.getDuration());

        }

        /**
         * Called by video widget on the UI thread on any asynchronous error.
         */
        @Override
        public void onLoadError(String errorMessage) {
            // An error here is normally due to being unable to decode the video format.

            Toast.makeText(
                    ListOFVideoActivity.this, "Error loading video: " + errorMessage, Toast.LENGTH_LONG)
                    .show();
            Log.e(TAG, "Error loading video: " + errorMessage);
        }

        @Override
        public void onClick() {

            if (isPaused) {
                vrVideoView.playVideo();
                playpause.setVisibility(View.GONE);
            } else {

                vrVideoView.pauseVideo();

                playpause.setVisibility(View.VISIBLE);
            }
            isPaused = !isPaused;

            new sendMessage("playpause").execute();
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
