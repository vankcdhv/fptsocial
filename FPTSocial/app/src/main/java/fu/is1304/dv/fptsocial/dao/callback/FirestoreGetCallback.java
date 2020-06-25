package fu.is1304.dv.fptsocial.dao.callback;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public interface FirestoreGetCallback {
    public void onComplete(DocumentSnapshot documentSnapshot);

    public void onFailure(Exception e);
}

