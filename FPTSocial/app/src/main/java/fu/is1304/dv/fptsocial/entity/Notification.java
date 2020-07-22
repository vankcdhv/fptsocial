package fu.is1304.dv.fptsocial.entity;

import java.io.Serializable;
import java.util.Date;

public class Notification implements Serializable {
    private String id;
    private String title;
    private String message;
    private Date time;
    private String uid;
    private String postID;
    private boolean seen;

    public Notification() {
    }

    public Notification(String title, String message, Date time, String uid, boolean isSeen) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.seen = isSeen;
        this.uid = uid;
    }

    public Notification(String title, String message, Date time, String uid, String postID, boolean seen) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.uid = uid;
        this.postID = postID;
        this.seen = seen;
    }

    public Notification(String id, String title, String message, Date time, String uid, String postID, boolean isSeen) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.time = time;
        this.seen = isSeen;
        this.uid = uid;
        this.postID = postID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }
}
