package fu.is1304.dv.fptsocial.dao;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.entity.User;

public class UserDAO {
    private static UserDAO instance;

    public static UserDAO getInstance() {
        if (instance == null) instance = new UserDAO();
        return instance;
    }


    public User getUserByUID(String uid) {
        final User[] user = new User[1];
        final CountDownLatch done = new CountDownLatch(1);
        DocumentReference reference = DataProvider.getInstance().getDatabase().collection(Const.USER_COLLECTION).document(uid);
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Gson gson = new Gson();
                            user[0] = gson.fromJson(gson.toJson(task.getResult().getData()), User.class);

                        }
                        done.countDown();
                    }

                });
        try {
            done.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user[0];
    }

    public User getCurrentUser() {
        User user = getUserByUID(DataProvider.getInstance().getCurrentUser().getUid());
        return user;
    }
}
