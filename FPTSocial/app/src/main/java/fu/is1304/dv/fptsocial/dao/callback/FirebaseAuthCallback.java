package fu.is1304.dv.fptsocial.dao.callback;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public interface FirebaseAuthCallback {
    public void onComplete(Task<AuthResult> task);
}
