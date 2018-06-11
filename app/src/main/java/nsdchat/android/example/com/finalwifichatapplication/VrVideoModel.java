package nsdchat.android.example.com.finalwifichatapplication;

import android.os.Parcel;
import android.os.Parcelable;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yasar on 23/1/18.
 */

public class VrVideoModel implements Serializable, Parcelable {

    @SerializedName("videos")
    @Expose
    private List<Video> videos = null;
    public final static Creator<VrVideoModel> CREATOR = new Creator<VrVideoModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public VrVideoModel createFromParcel(Parcel in) {
            return new VrVideoModel(in);
        }

        public VrVideoModel[] newArray(int size) {
            return (new VrVideoModel[size]);
        }

    };
    private final static long serialVersionUID = 1891300238975505452L;

    protected VrVideoModel(Parcel in) {
        in.readList(this.videos, (Video.class.getClassLoader()));
    }

    public VrVideoModel() {
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(videos);
    }

    public int describeContents() {
        return 0;
    }

}