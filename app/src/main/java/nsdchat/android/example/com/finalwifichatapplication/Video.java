package nsdchat.android.example.com.finalwifichatapplication;

import android.os.Parcel;
import android.os.Parcelable;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by yasar on 23/1/18.
 */

public class Video implements Serializable, Parcelable
{

    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("title")
    @Expose
    private String title;
    public final static Creator<Video> CREATOR = new Creator<Video>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        public Video[] newArray(int size) {
            return (new Video[size]);
        }

    }
            ;
    private final static long serialVersionUID = -580964261597248953L;

    protected Video(Parcel in) {
        this.link = ((String) in.readValue((String.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Video() {
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(link);
        dest.writeValue(title);
    }

    public int describeContents() {
        return 0;
    }

}