package fu.is1304.dv.fptsocial.dao.callback;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public interface FirebaseGetCollectionCallback {
    public void onComplete(List<QueryDocumentSnapshot> documentSnapshots);

    public void onFailed(Exception e);

}
