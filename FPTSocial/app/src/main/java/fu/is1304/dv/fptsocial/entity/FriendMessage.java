package fu.is1304.dv.fptsocial.entity;

import java.io.Serializable;

public class FriendMessage implements Serializable {
    private String uid;
    private String lastestMessage;

    public FriendMessage() {
    }

    public FriendMessage(String uid, String lastestMessage) {
        this.uid = uid;
        this.lastestMessage = lastestMessage;
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
}
