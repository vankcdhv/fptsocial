package fu.is1304.dv.fptsocial.dao;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;

public class FriendDAO {
    private static FriendDAO instance;

    public static FriendDAO getInstance() {
        if (instance == null) instance = new FriendDAO();
        return instance;
    }

    public FriendDAO() {
    }

    public void getAllFriendOfUser(String uid, final FirebaseGetCollectionCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.FRIEND_COLLECTION)
                .document(uid)
                .collection(Const.FRIEND_COLLECTION)
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

    public void checkIsFriend(String uid, final FirebaseGetCollectionCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.FRIEND_COLLECTION)
                .document(AuthController.getInstance().getUID())
                .collection(Const.FRIEND_COLLECTION)
                .orderBy("uid")
                .startAt(uid)
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
}
