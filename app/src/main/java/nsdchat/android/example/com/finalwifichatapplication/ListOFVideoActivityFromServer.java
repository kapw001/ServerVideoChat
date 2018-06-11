package nsdchat.android.example.com.finalwifichatapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import nsdchat.android.example.com.finalwifichatapplication.adapter.MyRecyclerViewAdapterServer;

public class ListOFVideoActivityFromServer extends AppCompatActivity implements MyRecyclerViewAdapterServer.OnItemClickListener {

    private static final String TAG = "ListOFVideoActivity";
    final int portNum = 3238;
    InetAddress ip = null;
    NetworkInterface networkInterface = null;
    ServerSocket serverSocket;

    private MulticastSocket socket;
    private InetAddress group;


    private VrVideoView vrVideoView;
    private List<Video> mList;
    private RecyclerView recyclerView;
    private MyRecyclerViewAdapterServer mRecyclerAdapter;
    private boolean isPaused = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_videos);

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

        mList = new ArrayList<>();
//
//        mList.add(new Videos("congo"));
//        mList.add(new Videos("digestivesystem"));
//        mList.add(new Videos("skeletalsystem"));


        vrVideoView = (VrVideoView) findViewById(R.id.video_view);
        vrVideoView.setInfoButtonEnabled(false);
        ;
        mRecyclerAdapter = new MyRecyclerViewAdapterServer(this, mList);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mRecyclerAdapter);
        recyclerView.setHasFixedSize(true);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));


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

                group = InetAddress.getByName("224.0.0.1");
                socket.joinGroup(new InetSocketAddress(group, portNum), networkInterface);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        vrVideoView.setEventListener(new ActivityEventListener());

        loadVideos();
    }


    private void loadVideos() {

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = "https://devjk.pappaya.education/mobile/rest/video";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();

                        Gson gson = new Gson();
                        VrVideoModel mlist = gson.fromJson(response.toString(), VrVideoModel.class);

                        mList.clear();
                        mList.addAll(mlist.getVideos());

                        mRecyclerAdapter.updateData(mList);


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
            }
        });

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Prevent the view from rendering continuously when in the background.
        vrVideoView.pauseRendering();
        // If the video is playing when onPause() is called, the default behavior will be to pause
        // the video and keep it paused when onResume() is called.
        isPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume the 3D rendering.
        vrVideoView.resumeRendering();

        try {
            socket = new MulticastSocket(portNum);
            socket.setInterface(ip);
            socket.setBroadcast(true);

            group = InetAddress.getByName("224.0.0.1");
            socket.joinGroup(new InetSocketAddress(group, portNum), networkInterface);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onRetry(View view) {


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

            try {
                if (socket != null) {
                    socket.send(packet);
                    return true;
                }
                return false;

            } catch (IOException e) {
                return false;
            }

        }
    }


    @Override
    public void OnItem(View v, int position) {

//        Toast.makeText(this, "called ", Toast.LENGTH_SHORT).show();

        vrVideoView.setVisibility(View.VISIBLE);
        final String videoName = mList.get(position).getLink();

        new sendMessage(videoName).execute();


        new Handler().post(new Runnable() {
            @Override
            public void run() {

                VrVideoView.Options options = new VrVideoView.Options();
                options.inputType = VrVideoView.Options.TYPE_STEREO_OVER_UNDER;
//                    videoWidgetView.loadVideoFromAsset("congo.mp4", options);
//                    videoWidgetView.setDisplayMode(VrVideoView.DisplayMode.FULLSCREEN_MONO);
                try {

                    vrVideoView.loadVideo(Uri.parse(videoName), options);
//                    vrVideoView.loadVideoFromAsset(videoName + ".mp4", options);
                    isPaused = false;

                } catch (IOException e) {

                    vrVideoView.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast
                                    .makeText(ListOFVideoActivityFromServer.this, "Error opening file. ", Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                    Log.e(TAG, "Could not open video: " + e);
                }

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
                    ListOFVideoActivityFromServer.this, "Error loading video: " + errorMessage, Toast.LENGTH_LONG)
                    .show();
            Log.e(TAG, "Error loading video: " + errorMessage);
        }

        @Override
        public void onClick() {
            if (isPaused) {
                vrVideoView.playVideo();
                new sendMessage("play").execute();
            } else {
                vrVideoView.pauseVideo();
                new sendMessage("pause").execute();
            }
            isPaused = !isPaused;
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
