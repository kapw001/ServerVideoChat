package nsdchat.android.example.com.finalwifichatapplication.model;

/**
 * Created by yasar on 3/1/18.
 */

public class Videos {

    public String videoname;
    public boolean isPlaying = false;
    public String videoLink;

    public String getCl() {
        return Cl;
    }

    public void setCl(String cl) {
        Cl = cl;
    }

    public String Cl;

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String title;
    public boolean isPaused = false;

    public Videos(String videoname) {
        this.videoname = videoname;
    }

    public Videos(String title, String videoLink, String cl) {

        this.title = title;
        this.videoLink = videoLink;
        this.Cl = cl;

    }


    public boolean isPaused() {
        return isPaused;

    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public String getVideoname() {
        return videoname;
    }

    public void setVideoname(String videoname) {
        this.videoname = videoname;
    }
}
