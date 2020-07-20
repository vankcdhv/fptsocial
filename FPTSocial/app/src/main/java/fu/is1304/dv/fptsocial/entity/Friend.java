package fu.is1304.dv.fptsocial.entity;

import java.io.Serializable;
import java.util.Date;

public class Friend implements Serializable {
    private String uid;
    private Date time;

    public Friend(String uid, Date time) {
        this.uid = uid;
        this.time = time;
    }

    public Friend() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
