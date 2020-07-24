package fu.is1304.dv.fptsocial.dao;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreDeleteDocCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.Comment;

public class CommentDAO {
    private static CommentDAO instance;

    public static CommentDAO getInstance() {
        if (instance == null) instance = new CommentDAO();
        return instance;
    }

    public void getAllCommentOfPost(String postID, final FirebaseGetCollectionCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.COMMENT_COLLECTION)
                .document(postID)
                .collection(Const.COMMENT_COLLECTION)
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<QueryDocumentSnapshot> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document);
                            }
                            callback.onComplete(list);
                        } else {
                            callback.onFailed(task.getException());
                        }
                    }
                });
    }

    public void createComment(String postID, Comment comment, final FirestoreSetCallback callback) {
        final DocumentReference reference = DataProvider.getInstance().getDatabase()
                .collection(Const.COMMENT_COLLECTION)
                .document(postID)
                .collection(Const.COMMENT_COLLECTION)
                .document();
        reference.set(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess(reference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    public void deleteComment(Comment comment, final FirestoreDeleteDocCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.COMMENT_COLLECTION)
                .document(comment.getPostID())
                .collection(Const.COMMENT_COLLECTION)
                .document(comment.getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.onComplete();
                        } else {
                            callback.onFailed(task.getException());
                        }
                    }
                });
    }
}
