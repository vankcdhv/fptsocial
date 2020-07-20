package fu.is1304.dv.fptsocial.entity;

import java.io.Serializable;
import java.util.Date;

public class Notification implements Serializable {
    private String id;
    private String message;
    private Date time;
    private String uid;
    private boolean seen;

    public Notification() {
    }

    public Notification(String message, Date time, String uid, boolean isSeen) {
        this.message = message;
        this.time = time;
        this.seen = isSeen;
        this.uid = uid;
    }

    public Notification(String id, String message, Date time, String uid, boolean isSeen) {
        this.id = id;
        this.message = message;
        this.time = time;
        this.seen = isSeen;
        this.uid = uid;
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
}
