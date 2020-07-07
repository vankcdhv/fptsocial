package fu.is1304.dv.fptsocial.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity {
    private EditText txtEmail;
    private EditText txtPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkLogin();
        initComponents();
        requestPermissions();
    }

    private void checkLogin() {
        if (AuthController.getInstance().getCurrentUser() != null) {
            checkUserInformationExisted();
        }
    }

    private void initComponents() {
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
    }

    public void btnLoginOnClick(View view) {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            AuthController.getInstance().loginByEmailAndPass(email, password, new FirebaseAuthCallback() {
                @Override
                public void onComplete(AuthResult result) {
                    checkUserInformationExisted();
                }

                @Override
                public void onFailure(Exception e) {
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

    private void checkUserInformationExisted() {
        if (!AuthController.getInstance().getCurrentUser().isEmailVerified()) {
            Toast.makeText(this, "Email chưa được xác thực", Toast.LENGTH_LONG).show();
            AuthController.getInstance().signOut();
            return;
        }
        UserDAO.getInstance().getCurrentUser(new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                User user = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                if (user != null) {
                    loginComplete();
                } else {
                    moveToProfile();
                }
            }

            @Override
            public void onFailure(Exception e) {
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
        intent.putExtra("email", txtEmail.getText().toString());
        intent.putExtra("password", txtPassword.getText().toString());
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

}