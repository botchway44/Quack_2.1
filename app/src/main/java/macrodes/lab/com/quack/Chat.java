package macrodes.lab.com.quack;

import android.text.format.Time;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Botchway on 3/10/2018.
 */

public class Chat {

    private String userId = "";
    private String author = "Anonymous";
    private String Url;
    private String message;
    private boolean isMedia;
    private Date time;

    public Chat() {
    }


    public Chat(String userId, String author, String url, String message, boolean isMedia) {
        this.userId = userId;
        this.author = author;
        Url = url;
        this.message = message;
        this.isMedia = isMedia;
        this.time = new Date();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isMedia() {
        return isMedia;
    }

    public void setMedia(boolean media) {
        isMedia = media;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
