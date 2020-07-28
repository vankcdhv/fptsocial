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
import java.util.Collection;
import java.util.List;

import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.FriendMessage;
import fu.is1304.dv.fptsocial.entity.Message;

public class MessageDAO {
    private static MessageDAO instance;

    public static MessageDAO getInstance() {
        if (instance == null) instance = new MessageDAO();
        return instance;
    }

    public MessageDAO() {
    }

    public void getListFriendMessage(final FirestoreGetCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.MESSAGE_COLLECTION)
                .document(AuthController.getInstance().getUID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        callback.onComplete(documentSnapshot);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    public void getLastMessageByUID(String uid, final FirebaseGetCollectionCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.MESSAGE_COLLECTION)
                .document(AuthController.getInstance().getUID())
                .collection(uid)
                .orderBy("timeSend", Query.Direction.DESCENDING)
                .limit(1)
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

    public void getMessageByUID(String uid, final FirebaseGetCollectionCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.MESSAGE_COLLECTION)
                .document(AuthController.getInstance().getUID())
                .collection(uid)
                .orderBy("timeSend", Query.Direction.ASCENDING)
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

    public void checkExistedChat(final String uid, final CheckCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.MESSAGE_COLLECTION)
                .document(AuthController.getInstance().getUID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> listPeople = new ArrayList<>();
                        listPeople.addAll((Collection<? extends String>) documentSnapshot.getData().get("list"));
                        if (listPeople.contains(uid)) callback.onSuccess(true);
                        else callback.onSuccess(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailed(e);
                    }
                });

    }

    public void sendMessage(final Message message_send, final Message message_receive, final FirestoreSetCallback callback) {

        checkExistedChat(message_send.getUid(), new CheckCallback() {
            @Override
            public void onSuccess(boolean result) {
                if (result) {
                    final DocumentReference reference_send = DataProvider.getInstance().getDatabase()
                            .collection(Const.MESSAGE_COLLECTION)
                            .document(AuthController.getInstance().getUID())
                            .collection(message_send.getUid())
                            .document();
                    reference_send.set(message_send)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    callback.onSuccess(reference_send.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    callback.onFailure(e);
                                }
                            });
                    final DocumentReference reference_receive = DataProvider.getInstance().getDatabase()
                            .collection(Const.MESSAGE_COLLECTION)
                            .document(message_send.getUid())
                            .collection(AuthController.getInstance().getUID())
                            .document();

                    reference_receive.set(message_receive)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //callback.onSuccess(reference_receive.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //callback.onFailure(e);
                                }
                            });
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });


    }

    public interface CheckCallback {
        public void onSuccess(boolean result);

        public void onFailed(Exception e);
    }

}
