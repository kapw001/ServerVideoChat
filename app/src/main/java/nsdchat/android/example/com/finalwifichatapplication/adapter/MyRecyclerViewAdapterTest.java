package nsdchat.android.example.com.finalwifichatapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import nsdchat.android.example.com.finalwifichatapplication.R;
import nsdchat.android.example.com.finalwifichatapplication.databinding.RowItemBinding;
import nsdchat.android.example.com.finalwifichatapplication.model.Videos;
import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by yasar on 1/1/18.
 */

public class MyRecyclerViewAdapterTest extends BaseAdapter {

    private static final String TAG = "MyRecyclerViewAdapter";
    private List<?> list;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
//    private boolean hasActiveHolder;

    public MyRecyclerViewAdapterTest(Context mContext, List<?> list) {
        super(list);

        this.list = list;
        this.mContext = mContext;
        try {
            this.mOnItemClickListener = (OnItemClickListener) mContext;
        } catch (ClassCastException e) {
            Log.e(TAG, "MyRecyclerViewAdapter: " + e.getMessage());
        }


    }

    public MyRecyclerViewAdapterTest(Fragment mContext, List<?> list) {
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

        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_item, parent, false);
        return new MyViewHolder(binding);

    }

    boolean hasActiveHolder = false;

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Videos videos = (Videos) list.get(position);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.OnItem(v, position);
            }
        });


        final RowItemBinding binding = ((RowItemBinding) holder.binding);

//        String s = videos.getTitle().substring(0, videos.getTitle().length() - 4);

//        binding.videoname.setText(s);


//MICRO_KIND, size: 96 x 96 thumbnail
//        Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(videos.getVideoLink(), MediaStore.Images.Thumbnails.MICRO_KIND);
//        binding.videoView.setImageBitmap(bmThumbnail);


//        File file = new File(videos.getVideoLink());

        try {

            final FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
            mmr.setDataSource(videos.getVideoLink());
            final String title = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
            final String subject = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_GENRE);
            final String clas = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_PUBLISHER);
            final String comment = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_COMMENT);

            String sub = "<b>Subject</b> : " + subject;
            String classs = "<b>Class</b> : " + videos.getCl();
            binding.description.setText(comment);
            binding.videoname.setText(title == null ? "General " : title);
            binding.subject.setText(Html.fromHtml(sub));
            binding.clas.setText(Html.fromHtml(classs));
            binding.clas.setVisibility(View.VISIBLE);


            new Load(binding.videoView).execute(mmr);

            Log.e(TAG, "onBindViewHolder: " + comment);
        } catch (IllegalArgumentException e) {

            Log.e(TAG, "onBindViewHolder: " + e.getMessage());

        }


//        ((Activity) mContext).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////        Bitmap b = mmr.getFrameAtTime(2000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
//                Bitmap b = mmr.getScaledFrameAtTime(2000000, 150, 150); // frame at 2 seconds
////        byte[] artwork = mmr.getEmbeddedPicture();
//                binding.videoView.setImageBitmap(b);
//
//
//            }
//        });


//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        try {
//            retriever.setDataSource(file.getAbsolutePath());
//
////            String DURATION = retriever.extractMetadata(
////                    MediaMetadataRetriever.METADATA_KEY_GENRE);
//
////            Log.e(TAG, "onBindViewHolder:DURATION   " + DURATION);
//
//
//            for (int i = 0; i < 1000; i++) {
//                //only Metadata != null is printed!
//                if (retriever.extractMetadata(i) != null) {
//                    Log.e(TAG, "Metadata  Test :: " + retriever.extractMetadata(i));
//                }
//
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Exception : " + e.getMessage());
//        }


//        Glide
//                .with(mContext)
//                .load(Uri.fromFile(new File(videos.getVideoLink()))).thumbnail(.2f)
//                .into(binding.videoView);


//        Bitmap bMap = ThumbnailUtils.createVideoThumbnail("file:///android_asset/" + videos.getVideoname() + ".mp4", MediaStore.Video.Thumbnails.MICRO_KIND);


//        binding.videoView.setImageBitmap(bMap);

//        AssetFileDescriptor afd = null;
//        try {
//            afd = mContext.getAssets().openFd(videos.getVideoname());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        binding.videoView.setVideoPath("file:///android_asset/" + videos.getVideoname() + ".mp4");


//        binding.videoView.setVideoURI();

//        try {
//            InputStream inputStream = mContext.getAssets().open(videos.getVideoname());
//
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            binding.videoView.setImageBitmap(bitmap);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        Glide.with(mContext).asBitmap().load("file:///android_asset/" + videos.getVideoname() + ".mp4").thumbnail(.5f).into(binding.videoView);


//        Glide.with(mContext)
//                .asBitmap()
//                .load(Uri.parse("file:///android_asset/" + videos.getVideoname()))
//                .into(binding.videoView);

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
//                mOnItemClickListener.OnItem(binding.videoView, position);
////                mOnItemClickListener.OnItem(null, position);
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
////////
////////
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
////                    binding.videoView.setDisplayMode(VrVideoView.DisplayMode.EMBEDDED);
//                    binding.videoView.pauseVideo();
//                    binding.videoView.setFullscreenButtonEnabled(true);
//                    binding.videoView.setInfoButtonEnabled(false);
//                    binding.videoView.setTouchTrackingEnabled(false);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });


    }


    class Load extends AsyncTask<FFmpegMediaMetadataRetriever, Void, Bitmap> {

        private ImageView imageView;

        public Load(ImageView imageView) {
            this.imageView = imageView;
        }


        @Override
        protected Bitmap doInBackground(FFmpegMediaMetadataRetriever... voids) {

            FFmpegMediaMetadataRetriever mmr = voids[0];
            Bitmap b = mmr.getScaledFrameAtTime(2000000, 150, 150);
            mmr.release();
            return b;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);

        }
    }

    public interface OnItemClickListener {
        void OnItem(View v, int position);
    }

}
