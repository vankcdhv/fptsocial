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
import java.util.List;

import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.entity.FriendMessage;

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


}
