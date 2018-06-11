package nsdchat.android.example.com.finalwifichatapplication.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nsdchat.android.example.com.finalwifichatapplication.R;
import nsdchat.android.example.com.finalwifichatapplication.Video;
import nsdchat.android.example.com.finalwifichatapplication.databinding.RowItemBinding;
import nsdchat.android.example.com.finalwifichatapplication.databinding.RowItemserverBinding;
import nsdchat.android.example.com.finalwifichatapplication.model.Videos;

/**
 * Created by yasar on 1/1/18.
 */

public class MyRecyclerViewAdapterServer extends BaseAdapter {

    private static final String TAG = "MyRecyclerViewAdapter";
    private List<?> list;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public MyRecyclerViewAdapterServer(Context mContext, List<?> list) {
        super(list);

        this.list = list;
        this.mContext = mContext;
        try {
            this.mOnItemClickListener = (OnItemClickListener) mContext;
        } catch (ClassCastException e) {
            Log.e(TAG, "MyRecyclerViewAdapter: " + e.getMessage());
        }


    }

    public MyRecyclerViewAdapterServer(Fragment mContext, List<?> list) {
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

        final Video videos = (Video) list.get(position);


        RowItemserverBinding binding = (RowItemserverBinding) holder.binding;
        binding.videoname.setText(videos.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.OnItem(v, position);
            }
        });

    }


    public interface OnItemClickListener {
        void OnItem(View v, int position);
    }

}
