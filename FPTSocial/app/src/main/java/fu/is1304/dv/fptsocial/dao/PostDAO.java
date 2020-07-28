package fu.is1304.dv.fptsocial.dao;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreDeleteDocCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.Notification;
import fu.is1304.dv.fptsocial.entity.Post;
import fu.is1304.dv.fptsocial.entity.User;

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

    public void postStatus(final Post post, final FirestoreSetCallback callback) {
        final DocumentReference reference = DataProvider.getInstance().getDatabase()
                .collection(Const.POST_COLLECTION)
                .document();
        reference.set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        final String postID = reference.getId();
                        CountDAO.getInstance().getCount(Const.POST_COLLECTION, new CountDAO.GetCountCallback() {
                            @Override
                            public void onComplete(long count) {
                                CountDAO.getInstance().setCount(Const.POST_COLLECTION, (int) (count + 1));
                                CountDAO.getInstance().getCount(Const.POST_COLLECTION + "_" + post.getUid(), new CountDAO.GetCountCallback() {
                                    @Override
                                    public void onComplete(long count) {
                                        CountDAO.getInstance().setCount(Const.POST_COLLECTION + "_" + post.getUid(), (int) (count + 1));
                                        callback.onSuccess(postID);
                                    }

                                    @Override
                                    public void onFailed(Exception e) {

                                    }
                                });
                            }

                            @Override
                            public void onFailed(Exception e) {

                            }
                        });
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
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    public void deleteStatus(final Post post, final FirestoreDeleteDocCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.POST_COLLECTION)
                .document(post.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CountDAO.getInstance().getCount(Const.POST_COLLECTION, new CountDAO.GetCountCallback() {
                            @Override
                            public void onComplete(long count) {
                                CountDAO.getInstance().setCount(Const.POST_COLLECTION, (int) (count - 1));
                                CountDAO.getInstance().getCount(Const.POST_COLLECTION + "_" + post.getUid(), new CountDAO.GetCountCallback() {
                                    @Override
                                    public void onComplete(long count) {
                                        CountDAO.getInstance().setCount(Const.POST_COLLECTION + "_" + post.getUid(), (int) (count - 1));
                                        callback.onComplete();
                                    }

                                    @Override
                                    public void onFailed(Exception e) {

                                    }
                                });
                            }

                            @Override
                            public void onFailed(Exception e) {

                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailed(e);
                    }
                });
    }

    public void getPostByID(String id, final FirestoreGetCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.POST_COLLECTION)
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().getData() != null)
                                callback.onComplete(task.getResult());
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public void getPostByUID(String uid, int limit, final FirebaseGetCollectionCallback callback) {
        if (limit < 1) return;
        DataProvider.getInstance().getDatabase()
                .collection(Const.POST_COLLECTION)
                .orderBy("uid")
                .orderBy("postDate", Query.Direction.DESCENDING)
                .startAt(uid)
                .limit(limit)
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

    public void searchPost(final String keyword, final FirebaseGetCollectionCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.POST_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final List<QueryDocumentSnapshot> list = new ArrayList<>();
                            final List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                            for (final DocumentSnapshot document : documentSnapshotList) {
                                String uid = document.getString("uid");
                                UserDAO.getInstance().getUserByUID(uid, new FirestoreGetCallback() {
                                    @Override
                                    public void onComplete(DocumentSnapshot documentSnapshot) {
                                        User user = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                                        if ((document.getString("title").toLowerCase()).contains(keyword.toLowerCase()) || (document.getString("content").toLowerCase()).contains(keyword.toLowerCase())
                                                || user.getLastName().contains(keyword) || user.getFirstName().contains(keyword)) {
                                            list.add((QueryDocumentSnapshot) document);
                                        }
                                        if (document.equals(documentSnapshotList.get(documentSnapshotList.size() - 1)))
                                            callback.onComplete(list);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        callback.onFailed(task.getException());

                                    }
                                });
                            }
                        } else {
                            callback.onFailed(task.getException());
                        }
                    }
                });
    }


}
