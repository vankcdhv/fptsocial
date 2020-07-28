package fu.is1304.dv.fptsocial.business;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fu.is1304.dv.fptsocial.dao.callback.FirebaseAuthCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseAuthActionCallBack;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;

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

    public void loginByEmailAndPass(String email, String pass, final FirebaseAuthCallback firebaseAuthCallback) {
        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseAuthCallback.onComplete(task.getResult());
                        } else {
                            firebaseAuthCallback.onFailure(task.getException());
                        }
                    }
                });

    }

    public void registerByEmailAndPass(String email, String pass, final FirebaseAuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            callback.onComplete(task.getResult());
                        } else {
                            callback.onFailure(task.getException());
                        }
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

    public void sendVerifyEmail(final FirebaseAuthActionCallBack callBack) {
        getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callBack.onComplete();
                } else {
                    callBack.onFailure(task.getException());
                }
            }
        });
    }

    public void removeCurrentUser(final FirebaseAuthActionCallBack callBack) {
        getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callBack.onComplete();
                } else {
                    callBack.onFailure(task.getException());
                }
            }
        });
    }

    public void changePass(String password, final FirestoreSetCallback callback) {
        getCurrentUser()
                .updatePassword(password)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }
}
