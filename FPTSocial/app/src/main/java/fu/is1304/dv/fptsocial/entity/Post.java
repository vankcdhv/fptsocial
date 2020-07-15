package fu.is1304.dv.fptsocial.entity;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;


public class Post implements Serializable {
    private String id;
    private String uid;
    private String author;
    private String title;
    private String content;
    private String image;
    private Date postDate;

    public Post() {
    }

    public Post(String id, String uid, String author, String title, String content, String image, Date postDate) {
        this.id = id;
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.content = content;
        this.image = image;
        this.postDate = postDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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
}
