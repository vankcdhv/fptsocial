package fu.is1304.dv.fptsocial.entity;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {
    private String id;
    private String uid;
    private String content;
    private Date time;
    private String postID;

    public Comment() {
    }

    public Comment(String uid, String content, Date time, String postID) {
        this.uid = uid;
        this.content = content;
        this.time = time;
        this.postID = postID;
    }

    public Comment(String id, String uid, String content, Date time, String postID) {
        this.id = id;
        this.uid = uid;
        this.content = content;
        this.time = time;
        this.postID = postID;
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

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }
}
