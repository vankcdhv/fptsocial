package fu.is1304.dv.fptsocial.gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseAuthCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.entity.User;
import fu.is1304.dv.fptsocial.gui.fragment.ChangePassFragment;
import fu.is1304.dv.fptsocial.gui.fragment.LoginFragment;

public class LoginActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private LoginFragment loginFragment;
    private ChangePassFragment changePassFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestPermissions();
        initComponents();
        checkLogin();
    }

    private void checkLogin() {
        if (AuthController.getInstance().getCurrentUser() != null) {
            checkUserInformationExisted();
        } else {
            loadFragment(loginFragment);
        }
    }

    private void initComponents() {
        setToolbar();
        loginFragment = new LoginFragment();
    }

    private void setToolbar() {
        actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void btnLoginOnClick(View view) {
        String email = loginFragment.getEmail();
        String password = loginFragment.getPassword();
        if (!email.isEmpty() && !password.isEmpty()) {
            loading(true);
            AuthController.getInstance().loginByEmailAndPass(email, password, new FirebaseAuthCallback() {
                @Override
                public void onComplete(AuthResult result) {
                    loginFragment.savePassword();
                    checkUserInformationExisted();
                }

                @Override
                public void onFailure(Exception e) {
                    loading(false);
                    Toast.makeText(LoginActivity.this, "Email or password is uncorrect", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "Email và mật khẩu không đúng", Toast.LENGTH_SHORT).show();
        }

    }

    public void btnRegisterClick(View view) {
        moveToRegister();
    }

    public void lableForgotPassClick(View view) {
        String email = loginFragment.getEmail();
        if (email != null && email.length() > 0) {
            AuthController.getInstance().getFirebaseAuth()
                    .sendPasswordResetEmail(email)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(LoginActivity.this, R.string.message_send_reset_password_email_success, Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, R.string.message_send_reset_password_email_failed, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void checkUserInformationExisted() {
        if (!AuthController.getInstance().getCurrentUser().isEmailVerified()) {
            loading(false);
            Toast.makeText(this, "Email chưa được xác thực", Toast.LENGTH_LONG).show();
            AuthController.getInstance().signOut();
            return;
        }
        UserDAO.getInstance().getCurrentUser(new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                User user = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                loading(false);
                if (user != null) {
                    loginComplete();
                } else {
                    moveToProfile();
                }
            }

            @Override
            public void onFailure(Exception e) {
                loading(false);
                Toast.makeText(LoginActivity.this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginComplete() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void moveToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("email", loginFragment.getEmail());
        intent.putExtra("password", loginFragment.getPassword());
        startActivity(intent);
    }

    private void moveToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("mode", Const.MODE_CREATE_PROFILE);
        intent.putExtra("uid", AuthController.getInstance().getUID());
        startActivity(intent);
        finish();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            return;
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.INTERNET)) {

        } else {
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 1000);
        }
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loginFragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loading(boolean isLoading) {
        if (isLoading) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (loginFragment.getPbLoading() != null)
                loginFragment.getPbLoading().setVisibility(View.VISIBLE);
        } else {
            if (loginFragment.getPbLoading() != null)
                loginFragment.getPbLoading().setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }


}