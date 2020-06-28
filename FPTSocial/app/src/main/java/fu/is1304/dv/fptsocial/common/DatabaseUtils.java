package fu.is1304.dv.fptsocial.common;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import fu.is1304.dv.fptsocial.entity.User;

public class DatabaseUtils {
    public static User convertDocumentSnapshotToUser(DocumentSnapshot result) {
        Gson gson = new Gson();
        User user = gson.fromJson(gson.toJson(result.getData()), User.class);
        return user;
    }

    public static String validateEmailAndPass(String email, String password, String repass) {
        if (email.isEmpty() || password.isEmpty() || repass.isEmpty()) {
            return Const.VALIDATE_CODE_EMPTY;
        }
        if (!password.equals(repass)) {
            return Const.VALIDATE_CODE_NOT_MATCH;
        }

        return Const.VALIDATE_CODE_CORRECT;
    }
}