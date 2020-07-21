package fu.is1304.dv.fptsocial.dao;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.entity.FriendMessage;

public class MessageDAO {
    private static MessageDAO instance;

    public static MessageDAO getInstance() {
        if (instance == null) instance = new MessageDAO();
        return instance;
    }

    public MessageDAO() {
    }

//    public void getListFriendMessage(final FirebaseGetCollectionCallback callback){
//        DataProvider.getInstance().getDatabase()
//                .collection(Const.MESSAGE_COLLECTION)
//                .document(AuthController.getInstance().getUID())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            List<QueryDocumentSnapshot> list = new ArrayList<>();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                list.add(document);
//                            }
//                            callback.onComplete(list);
//                        } else {
//                            callback.onFailed(task.getException());
//                        }
//                    }
//                });
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        FriendMessage fm = DatabaseUtils.convertDocumentSnapshotToFriendMessage(documentSnapshot);
//                    }
//                });
//    }

//    public void getLastestMessageByUid(String uidm , final FirebaseGetCollectionCallback callback){
//        DataProvider.getInstance().getDatabase()
//                .collection(Const.MESSAGE_COLLECTION)
//                .document(AuthController.getInstance().getUID())
//                .collection(uidm)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                    }
//                });
//    }

}
