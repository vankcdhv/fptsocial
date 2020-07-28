package fu.is1304.dv.fptsocial.dao;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;

public class DepartmentDAO {
    private static DepartmentDAO instance;

    public static DepartmentDAO getInstance(){
        if(instance==null) instance = new DepartmentDAO();
        return instance;
    }

    public void getAllDepartment(final FirebaseGetCollectionCallback callback){
        DataProvider.getInstance().getDatabase()
                .collection(Const.DEPARTMENT_COLLECTION)
                .orderBy("name")
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
