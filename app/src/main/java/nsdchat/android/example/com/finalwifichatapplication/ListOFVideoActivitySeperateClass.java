package nsdchat.android.example.com.finalwifichatapplication;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.vr.sdk.widgets.video.VrVideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nsdchat.android.example.com.finalwifichatapplication.adapter.MyRecyclerViewAdapterTest;
import nsdchat.android.example.com.finalwifichatapplication.model.Videos;

public class ListOFVideoActivitySeperateClass extends AppCompatActivity implements MyRecyclerViewAdapterTest.OnItemClickListener {

    private static final String TAG = "ListOFVideoActivity";
    private static final String[] READ_AND_WRITE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int RC_READ_AND_WRITE = 123;
    private VrVideoView vrVideoView;
    private List<Videos> mList;
    private RecyclerView recyclerView;
    private MyRecyclerViewAdapterTest mRecyclerAdapter;
    private View currentFocusedLayout, oldFocusedLayout;
    private RelativeLayout main;
    private TextView error;
    private LinearLayout second;
    private ImageView idelImg;
    private ScoketClass scoketClass;
    private VrVideoPlayerClass vrVideoPlayerClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_videos);


        main = (RelativeLayout) findViewById(R.id.main);
        second = (LinearLayout) findViewById(R.id.second);
        error = (TextView) findViewById(R.id.error);
        vrVideoView = (VrVideoView) findViewById(R.id.video_view);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        scoketClass = new ScoketClass(this);
        vrVideoPlayerClass = new VrVideoPlayerClass(vrVideoView, this, scoketClass);

        Retry();


    }


    private void Retry() {

        main.setVisibility(View.VISIBLE);
        second.setVisibility(View.GONE);
        enableVideoList();

    }


    private void enableVideoList() {


        mList = new ArrayList<>();
        mRecyclerAdapter = new MyRecyclerViewAdapterTest(this, mList);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mRecyclerAdapter);
        recyclerView.setHasFixedSize(true);

        methodRequiresTwoPermission();


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

        scoketClass.sendMessages("stop");
        vrVideoPlayerClass.onPause();
//        if (getWifiManager().isWifiEnabled()) {
        // Prevent the view from rendering continuously when in the background.

//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        scoketClass.init();
        vrVideoPlayerClass.onResume();


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

//        String path = Environment.getExternalStorageDirectory().toString();
        String path = Environment.getExternalStorageDirectory().toString() + "/vrvideos";

//        ArrayList<Videos> videosArrayList = new ArrayList<Videos>();

        mList.clear();
        File directory = new File(path);
        if (directory.exists()) {
            File[] file = directory.listFiles();
            for (int i = 0; i < file.length; i++) {

//                mList.add(new Videos(file[i].getName(), file[i].getAbsolutePath()));

//            Log.e(TAG, "getAllFiles: " + file[i].getName() + "   " + file[i].getAbsolutePath());

            }

            mRecyclerAdapter.updateData(mList);
            recyclerView.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
        } else {

            Toast.makeText(this, "There is no vr videos ", Toast.LENGTH_SHORT).show();

        }

    }


    @Override
    public void OnItem(View v, int position) {

//        Toast.makeText(this, "called ", Toast.LENGTH_SHORT).show();

        vrVideoView.setVisibility(View.VISIBLE);
        idelImg.setVisibility(View.GONE);
        final String videoName = mList.get(position).getVideoLink();


        scoketClass.sendMessages(videoName);
        vrVideoPlayerClass.play(videoName);


    }


}
