package fu.is1304.dv.fptsocial.dao;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
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

    public void getListPeopleChat(final String uid, final FirestoreGetCallback callback) {
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

    public void sendMessage(final Message message_send, final Message message_receive, final FirestoreSetCallback callback) {

        getListPeopleChat(message_send.getUid(), new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                final List<String> listPeople = new ArrayList<>();
                if (documentSnapshot.getData() != null) {
                    listPeople.addAll((Collection<? extends String>) documentSnapshot.getData().get("list"));
                }
                DataProvider.getInstance().getDatabase()
                        .collection(Const.MESSAGE_COLLECTION)
                        .document(AuthController.getInstance().getUID())
                        .collection(message_send.getUid())
                        .document()
                        .set(message_send)
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
                DataProvider.getInstance().getDatabase()
                        .collection(Const.MESSAGE_COLLECTION)
                        .document(message_send.getUid())
                        .collection(AuthController.getInstance().getUID())
                        .document()
                        .set(message_receive)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });

                if (!listPeople.contains(message_send.getUid())) {
                    setListPeople(message_send.getUid(), AuthController.getInstance().getUID());
                    setListPeople(AuthController.getInstance().getUID(), message_send.getUid());
                }
            }

            @Override
            public void onFailure(Exception e) {

            }


        });


    }

    public void setListPeople(final String friendUid, final String uid) {
        getListPeopleChat(uid, new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                final List<String> listPeople = new ArrayList<>();
                if (documentSnapshot.getData() != null) {
                    listPeople.addAll((Collection<? extends String>) documentSnapshot.getData().get("list"));
                }
                listPeople.add(friendUid);
                HashMap<String, Object> list = new HashMap<>();
                list.put("list", listPeople);
                DataProvider.getInstance().getDatabase()
                        .collection(Const.MESSAGE_COLLECTION)
                        .document(uid)
                        .set(list);
            }

            @Override
            public void onFailure(Exception e) {
                e.getCause();
            }
        });
    }

    public void realtimeChat(String uid, final FirebaseGetCollectionCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.MESSAGE_COLLECTION)
                .document(AuthController.getInstance().getUID())
                .collection(uid)
                .orderBy("timeSend", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            callback.onFailed(error);
                        } else {
                            List<QueryDocumentSnapshot> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : value) {
                                list.add(document);
                            }
                            callback.onComplete(list);
                        }
                    }
                });
    }

}
