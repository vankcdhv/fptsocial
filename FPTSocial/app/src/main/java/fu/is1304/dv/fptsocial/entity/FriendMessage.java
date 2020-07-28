package fu.is1304.dv.fptsocial.entity;

import java.io.Serializable;
import java.util.Date;

public class FriendMessage implements Serializable {
    private String uid;
    private String lastestMessage;
    private Date time;

    public FriendMessage() {
    }

    public FriendMessage(String uid, String lastestMessage, Date time) {
        this.uid = uid;
        this.lastestMessage = lastestMessage;
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLastestMessage() {
        return lastestMessage;
    }

    public void setLastestMessage(String lastestMessage) {
        this.lastestMessage = lastestMessage;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
