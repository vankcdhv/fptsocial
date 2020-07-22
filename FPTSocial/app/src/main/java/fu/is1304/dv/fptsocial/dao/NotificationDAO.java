package fu.is1304.dv.fptsocial.dao;

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
import java.util.List;

import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.Notification;

public class NotificationDAO {

    private static NotificationDAO instance;

    public static NotificationDAO getInstance() {
        if (instance == null) {
            instance = new NotificationDAO();
        }
        return instance;
    }

    public NotificationDAO() {
    }

    public void getAllNotification(String uid, final FirebaseGetCollectionCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.NOTIFICATION_COLLECTION)
                .document(uid)
                .collection(Const.NOTIFICATION_COLLECTION)
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(Const.NUMBER_ITEMS_OF_NOTIFICATION)
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

    public void updateNotification(String uid, Notification notification, final FirestoreSetCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.NOTIFICATION_COLLECTION)
                .document(uid)
                .collection(Const.NOTIFICATION_COLLECTION)
                .document(notification.getId())
                .set(notification)
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

    public void createNotification(String uid, Notification notification, final FirestoreSetCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.NOTIFICATION_COLLECTION)
                .document(uid)
                .collection(Const.NOTIFICATION_COLLECTION)
                .document()
                .set(notification)
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

    public void getNotifyRealtime(String uid, final OnEventChangeCallBack callBack) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.NOTIFICATION_COLLECTION)
                .document(uid)
                .collection(Const.NOTIFICATION_COLLECTION)
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        List<QueryDocumentSnapshot> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            list.add(document);
                        }
                        callBack.onChange(list);

                    }
                });
    }

    public void getNotifyByUID(String uid, int limit, final FirebaseGetCollectionCallback callBack) {
        if (limit == 0) return;
        DataProvider.getInstance().getDatabase()
                .collection(Const.NOTIFICATION_COLLECTION)
                .document(uid)
                .collection(Const.NOTIFICATION_COLLECTION)
                .orderBy("time", Query.Direction.DESCENDING)
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
                            callBack.onComplete(list);
                        } else {
                            callBack.onFailed(task.getException());
                        }
                    }
                });
    }

    public interface OnEventChangeCallBack {
        public void onChange(List<QueryDocumentSnapshot> documentSnapshots);
    }

}
