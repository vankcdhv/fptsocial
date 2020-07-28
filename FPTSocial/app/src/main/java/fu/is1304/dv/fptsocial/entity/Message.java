package fu.is1304.dv.fptsocial.entity;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private String id;


    private String uid;
    private String content;
    private Date timeSend;
    private boolean send;

    public Message() {
    }

    public Message(String sentUserId, String content, Date timeSent, boolean isSend) {
        this.uid = sentUserId;
        this.content = content;
        this.timeSend = timeSent;
        this.send = isSend;
    }

    public Message(String id, String uid, String content, Date timeSend, boolean send) {
        this.id = id;
        this.uid = uid;
        this.content = content;
        this.timeSend = timeSend;
        this.send = send;
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

    public Date getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(Date timeSend) {
        this.timeSend = timeSend;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }
}
