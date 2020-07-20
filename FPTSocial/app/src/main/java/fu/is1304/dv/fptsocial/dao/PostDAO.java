package fu.is1304.dv.fptsocial.dao;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreDeleteDocCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.Notification;
import fu.is1304.dv.fptsocial.entity.Post;

public class PostDAO {
    private static PostDAO instance;

    public static PostDAO getInstance() {
        if (instance == null) instance = new PostDAO();
        return instance;
    }

    public PostDAO() {
    }

    //Get all post of all people
    public void getAllPost(final FirebaseGetCollectionCallback callback) {

        DataProvider.getInstance().getDatabase()
                .collection(Const.POST_COLLECTION)
                .orderBy("postDate", Query.Direction.DESCENDING)
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

    public void getListPost(DocumentSnapshot lastPost, final FirebaseGetCollectionCallback callback) {
        if (lastPost != null) {
            DataProvider.getInstance().getDatabase()
                    .collection(Const.POST_COLLECTION)
                    .orderBy("postDate", Query.Direction.DESCENDING)
                    .startAfter(lastPost)
                    .limit(Const.NUMBER_ITEMS_OF_NEW_FEED)
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
        } else {
            DataProvider.getInstance().getDatabase()
                    .collection(Const.POST_COLLECTION)
                    .orderBy("postDate", Query.Direction.DESCENDING)
                    .limit(Const.NUMBER_ITEMS_OF_NEW_FEED)
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
    }

    public void postStatus(Post post, final FirestoreSetCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.POST_COLLECTION)
                .document()
                .set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    public void updatePost(Post post, final FirestoreSetCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.POST_COLLECTION)
                .document(post.getId())
                .set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    public void deleteStatus(Post post, final FirestoreDeleteDocCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.POST_COLLECTION)
                .document(post.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onComplete();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailed(e);
                    }
                });
    }
}
