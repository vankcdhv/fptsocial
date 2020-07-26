package fu.is1304.dv.fptsocial.entity;

import java.io.Serializable;
import java.util.Date;


public class Post implements Serializable {
    private String id;
    private String uid;
    private String title;
    private String content;
    private String image;
    private Date postDate;
    private long countLike;

    public Post() {
    }


    public Post(String id, String uid, String title, String content, String image, Date postDate, long countLike) {
        this.id = id;
        this.uid = uid;
        this.title = title;
        this.content = content;
        this.image = image;
        this.postDate = postDate;
        this.countLike = countLike;
    }

    public Post(String uid, String title, String content, String image, Date postDate, long countLike) {
        this.uid = uid;
        this.title = title;
        this.content = content;
        this.image = image;
        this.postDate = postDate;
        this.countLike = countLike;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getCountLike() {
        return countLike;
    }

    public void setCountLike(long countLike) {
        this.countLike = countLike;
    }
}
