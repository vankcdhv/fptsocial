package fu.is1304.dv.fptsocial.business;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fu.is1304.dv.fptsocial.dao.callback.FirebaseAuthCallback;

public class AuthController {
    private static AuthController instance;
    private FirebaseAuth firebaseAuth;

    public AuthController() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static AuthController getInstance() {
        if (instance == null) instance = new AuthController();
        return instance;
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    public void loginByUsernameAndPass(String email, String pass, final FirebaseAuthCallback firebaseAuthCallback) {
        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        firebaseAuthCallback.onComplete(task);
                    }
                });

    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public String getUID() {
        return firebaseAuth.getCurrentUser().getUid();
    }
}
