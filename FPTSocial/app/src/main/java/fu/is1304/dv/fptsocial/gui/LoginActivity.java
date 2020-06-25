package fu.is1304.dv.fptsocial.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseAuthCallback;

public class LoginActivity extends AppCompatActivity {
    private EditText txtEmail;
    private EditText txtPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestPermissions();
        initComponents();
    }

    private void initComponents() {
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            loginComplete();
        }
    }

    public void btnLoginOnClick(View view) {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            AuthController.getInstance().loginByUsernameAndPass(email, password, new FirebaseAuthCallback() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        loginComplete();
                    } else {
                        Toast.makeText(LoginActivity.this, "Email or password is uncorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "Email và mật khẩu không đúng", Toast.LENGTH_SHORT).show();
        }

    }

    private void loginComplete() {
        Intent intent = new Intent(this, MainActivity.class);
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