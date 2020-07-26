package fu.is1304.dv.fptsocial.dao;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.Post;

public class LikeDAO {
    private static LikeDAO instance;

    public static LikeDAO getInstance() {
        if (instance == null) {
            instance = new LikeDAO();
        }
        return instance;
    }

    public void checkIsLikePost(final String uid, String postID, final FirebaseGetCollectionCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.POST_LIKE_COLLECTION)
                .document(postID)
                .collection(Const.POST_LIKE_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<QueryDocumentSnapshot> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(uid))
                                    list.add(document);
                            }
                            callback.onComplete(list);
                        } else {
                            callback.onFailed(task.getException());
                        }
                    }
                });
    }


    public void likePost(final String postID, final FirestoreSetCallback callback) {
        PostDAO.getInstance().getPostByID(postID, new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                final Post post = DatabaseUtils.convertDocumentSnapshotToPost(documentSnapshot);
                checkIsLikePost(AuthController.getInstance().getUID(), postID, new FirebaseGetCollectionCallback() {
                    @Override
                    public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                        if (documentSnapshots == null || documentSnapshots.size() == 0 || !documentSnapshots.get(0).getBoolean("liked")) {
                            post.setCountLike(post.getCountLike());
                            PostDAO.getInstance().updatePost(post, new FirestoreSetCallback() {
                                @Override
                                public void onSuccess(String id) {
                                    Map<String, Boolean> object = new HashMap<>();
                                    object.put("liked", true);
                                    DataProvider.getInstance().getDatabase()
                                            .collection(Const.POST_LIKE_COLLECTION)
                                            .document(postID)
                                            .collection(Const.POST_LIKE_COLLECTION)
                                            .document(AuthController.getInstance().getUID())
                                            .set(object)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        callback.onSuccess(post.getCountLike() + "");
                                                    }
                                                }
                                            });
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    callback.onFailure(e);
                                }
                            });
                        }
                    }


                    @Override
                    public void onFailed(Exception e) {
                        callback.onFailure(e);
                    }
                });
                post.setCountLike(post.getCountLike() + 1);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void unLikePost(final String postID, final FirestoreSetCallback callback) {
        PostDAO.getInstance().getPostByID(postID, new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                final Post post = DatabaseUtils.convertDocumentSnapshotToPost(documentSnapshot);
                checkIsLikePost(AuthController.getInstance().getUID(), postID, new FirebaseGetCollectionCallback() {
                    @Override
                    public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                        if (!(documentSnapshots == null || documentSnapshots.size() == 0 || !documentSnapshots.get(0).getBoolean("liked"))) {
                            post.setCountLike(post.getCountLike() - 2);
                            PostDAO.getInstance().updatePost(post, new FirestoreSetCallback() {
                                @Override
                                public void onSuccess(String id) {
                                    Map<String, Boolean> object = new HashMap<>();
                                    object.put("liked", true);
                                    DataProvider.getInstance().getDatabase()
                                            .collection(Const.POST_LIKE_COLLECTION)
                                            .document(postID)
                                            .collection(Const.POST_LIKE_COLLECTION)
                                            .document(AuthController.getInstance().getUID())
                                            .set(object)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        callback.onSuccess(post.getCountLike() + "");
                                                    }
                                                }
                                            });
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    callback.onFailure(e);
                                }
                            });
                        }
                    }


                    @Override
                    public void onFailed(Exception e) {
                        callback.onFailure(e);
                    }
                });
                post.setCountLike(post.getCountLike() + 1);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
