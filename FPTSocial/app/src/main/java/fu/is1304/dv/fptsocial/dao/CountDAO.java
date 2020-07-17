package fu.is1304.dv.fptsocial.dao;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import fu.is1304.dv.fptsocial.common.Const;

public class CountDAO {
    private static CountDAO instance;

    public static CountDAO getInstance() {
        if (instance == null) instance = new CountDAO();
        return instance;
    }

    public CountDAO() {

    }

    public void getCount(String collection, final GetCountCallback callback) {
        DataProvider.getInstance().getDatabase()
                .collection(Const.COUNT_COLLECTION)
                .document(collection)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        callback.onComplete((long) documentSnapshot.getData().get("count"));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailed(e);
                    }
                });

    }

    public void setCount(String collection, int count) {
        Map<String, Long> object = new HashMap<>();
        object.put("count", (long) count);
        DataProvider.getInstance().getDatabase()
                .collection(Const.COUNT_COLLECTION)
                .document(collection)
                .set(object);
    }

    public interface GetCountCallback {
        public void onComplete(long count);

        public void onFailed(Exception e);
    }
}
