package fu.is1304.dv.fptsocial.common;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.entity.Notification;
import fu.is1304.dv.fptsocial.entity.Post;
import fu.is1304.dv.fptsocial.entity.User;

public class DatabaseUtils {
    //Convert document snapshot to User
    public static User convertDocumentSnapshotToUser(DocumentSnapshot result) {
        Gson gson = new Gson();
        User user = gson.fromJson(gson.toJson(result.getData()), User.class);
        return user;
    }

    //Convert document snapshot to Post
    public static Post convertDocumentSnapshotToPost(DocumentSnapshot result) {
        Post post;

        String id = result.getId();
        String title = (String) result.getData().get("title");
        String content = (String) result.getData().get("content");
        String image = (String) result.getData().get("image");
        Date postDate = ((Timestamp) result.getData().get("postDate")).toDate();
        String uid = (String) result.getData().get("uid");
        post = new Post(id, uid, title, content, image, postDate);

        return post;
    }

    //Convert list Query document snapshot to list post
    public static List<Post> convertListDocSnapToListPost(List<QueryDocumentSnapshot> documentSnapshots) {
        final List<Post> list = new ArrayList<>();
        for (final QueryDocumentSnapshot snapshot : documentSnapshots) {
            list.add(convertDocumentSnapshotToPost(snapshot));
        }

        return list;
    }

    //Convert document snapshot to Notification
    public static Notification convertDocumentSnapshotToNotification(DocumentSnapshot result) {
        Notification notification;

        String id = result.getId();
        String message = (String) result.getData().get("message");
        String uid = (String) result.getData().get("uid");
        Boolean isSeen = (Boolean) result.getData().get("seen");
        Date time = ((Timestamp) result.getData().get("time")).toDate();

        notification = new Notification(id, message, time, uid, isSeen);

        return notification;
    }

    //Convert list Query document snapshot to list notification
    public static List<Notification> convertListDocSnapToListNotification(List<QueryDocumentSnapshot> documentSnapshots) {
        final List<Notification> list = new ArrayList<>();
        for (final QueryDocumentSnapshot snapshot : documentSnapshots) {
            list.add(convertDocumentSnapshotToNotification(snapshot));
        }

        return list;
    }

    //Validate email and password login and register
    public static String validateEmailAndPass(String email, String password, String repass) {
        if (email.isEmpty() || password.isEmpty() || repass.isEmpty()) {
            return Const.VALIDATE_CODE_EMPTY;
        }
        if (!password.equals(repass)) {
            return Const.VALIDATE_CODE_NOT_MATCH;
        }

        return Const.VALIDATE_CODE_CORRECT;
    }

    public static List<String> convertListDocSnapToListUID(List<QueryDocumentSnapshot> documentSnapshots) {
        List<String> res = new ArrayList<>();
        for (QueryDocumentSnapshot snapshot : documentSnapshots) {
            res.add(snapshot.getId());
        }
        return res;

    }
}
