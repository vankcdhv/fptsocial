package fu.is1304.dv.fptsocial.dao;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;

import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.User;

public class UserDAO {
    private static UserDAO instance;

    public static UserDAO getInstance() {
        if (instance == null) instance = new UserDAO();
        return instance;
    }


    /*
     * Get information of user from Firebase cloudstore by uid
     * Callback is handler of result
     */
    public void getUserByUID(String uid, final FirestoreGetCallback callback) {

        DataProvider.getInstance().getDatabase()
                .collection(Const.USER_COLLECTION)
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            callback.onComplete(task.getResult());
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    /*
     * Get information of current user
     * Callback is handler of result
     */
    public void getCurrentUser(FirestoreGetCallback callback) {
        getUserByUID(AuthController.getInstance().getCurrentUser().getUid(), callback);
    }

    /*
     * set information for user
     * Callback is handler of result
     */
    public void setUserData(String uid, User user, final FirestoreSetCallback firestoreSetCallback) {
        DataProvider.getInstance()
                .getDatabase()
                .collection(Const.USER_COLLECTION)
                .document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firestoreSetCallback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        firestoreSetCallback.onFailure(e);
                    }
                });
    }

    /*
     * set information for current user
     * Callback is handler of result
     */
    public void updateUserData(User user, FirestoreSetCallback firestoreSetCallback) {
        setUserData(AuthController.getInstance().getUID(), user, firestoreSetCallback);
    }

    public void getUserRealtime(String uid, final FirestoreGetCallback firestoreGetCallback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.USER_COLLECTION)
                .document(uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e == null) {
                            firestoreGetCallback.onComplete(snapshot);
                        } else {
                            firestoreGetCallback.onFailure(e);
                        }
                    }
                });
    }

    public void searchUserByName(final String keyword, final FirebaseGetCollectionCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.USER_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<QueryDocumentSnapshot> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("lastName").contains(keyword) || document.getString("firstName").contains(keyword)) {
                                    list.add(document);
                                }
                            }
                            callback.onComplete(list);
                        } else {
                            callback.onFailed(task.getException());
                        }
                    }
                });
    }

}
