package fu.is1304.dv.fptsocial.entity;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private String sentUserId;
    private String content;
    private Date timeSent;
    private boolean isSend;

    public Message() {
    }

    public Message(String sentUserId, String content, Date timeSent, boolean isSend) {
        this.sentUserId = sentUserId;
        this.content = content;
        this.timeSent = timeSent;
        this.isSend = isSend;
    }

    public String getSentUserId() {
        return sentUserId;
    }

    public void setSentUserId(String sentUserId) {
        this.sentUserId = sentUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = timeSent;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }
}
