package fu.is1304.dv.fptsocial.entity;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {
    private String id;
    private String uid;
    private String content;
    private Date time;
    private boolean blocked;

    public Comment() {
    }

    public Comment(String uid, String content, Date time, boolean blocked) {
        this.uid = uid;
        this.content = content;
        this.time = time;
        this.blocked = blocked;
    }

    public Comment(String id, String uid, String content, Date time, boolean blocked) {
        this.id = id;
        this.uid = uid;
        this.content = content;
        this.time = time;
        this.blocked = blocked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
