package nsdchat.android.example.com.finalwifichatapplication.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nsdchat.android.example.com.finalwifichatapplication.R;
import nsdchat.android.example.com.finalwifichatapplication.databinding.RowItemBinding;
import nsdchat.android.example.com.finalwifichatapplication.databinding.RowItemserverBinding;
import nsdchat.android.example.com.finalwifichatapplication.model.Videos;

/**
 * Created by yasar on 1/1/18.
 */

public class MyRecyclerViewAdapter extends BaseAdapter {

    private static final String TAG = "MyRecyclerViewAdapter";
    private List<?> list;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public MyRecyclerViewAdapter(Context mContext, List<?> list) {
        super(list);

        this.list = list;
        this.mContext = mContext;
        try {
            this.mOnItemClickListener = (OnItemClickListener) mContext;
        } catch (ClassCastException e) {
            Log.e(TAG, "MyRecyclerViewAdapter: " + e.getMessage());
        }


    }

    public MyRecyclerViewAdapter(Fragment mContext, List<?> list) {
        super(list);

        this.list = list;
        this.mContext = mContext.getActivity();
        try {
            this.mOnItemClickListener = (OnItemClickListener) mContext;
        } catch (ClassCastException e) {
            Log.e(TAG, "MyRecyclerViewAdapter: " + e.getMessage());
        }

    }

    public void updateData(List<?> mList1) {

        this.list = new ArrayList<>();
        this.list = mList1;
        notifyDataSetChanged();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_itemserver, parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Videos videos = (Videos) list.get(position);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.OnItem(v, position);
            }
        });


        final RowItemserverBinding binding = ((RowItemserverBinding) holder.binding);

        binding.videoname.setText(videos.getTitle());


//        binding.videoView.pauseVideo();
//
//        binding.videoView.setEventListener(new VrVideoEventListener() {
//            @Override
//            public void onLoadSuccess() {
//
//            }
//
//            /**
//             * Called by video widget on the UI thread on any asynchronous error.
//             */
//            @Override
//            public void onLoadError(String errorMessage) {
//
//            }
//
//            @Override
//            public void onClick() {
//                mOnItemClickListener.OnItem(null, position);
////                if (videos.isPaused) {
////                    binding.videoView.playVideo();
////                } else {
////                    binding.videoView.pauseVideo();
////                }
////                videos.isPaused = !videos.isPaused;
//            }
//
//            /**
//             * Update the UI every frame.
//             */
//            @Override
//            public void onNewFrame() {
//
//            }
//
//            /**
//             * Make the video play in a loop. This method could also be used to move to the next video in
//             * a playlist.
//             */
//            @Override
//            public void onCompletion() {
//
//            }
//        });
////
////
//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//
//                VrVideoView.Options options = new VrVideoView.Options();
//                options.inputType = VrVideoView.Options.TYPE_STEREO_OVER_UNDER;
////                    videoWidgetView.loadVideoFromAsset("congo.mp4", options);
////                    videoWidgetView.setDisplayMode(VrVideoView.DisplayMode.FULLSCREEN_MONO);
//                try {
//                    binding.videoView.loadVideoFromAsset(videos.getVideoname() + ".mp4", options);
//                    binding.videoView.setDisplayMode(VrVideoView.DisplayMode.EMBEDDED);
//                    binding.videoView.pauseVideo();
//                    binding.videoView.setFullscreenButtonEnabled(false);
//                    binding.videoView.setInfoButtonEnabled(false);
//                    binding.videoView.setTouchTrackingEnabled(false);
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });


    }


    public interface OnItemClickListener {
        void OnItem(View v, int position);
    }

}
