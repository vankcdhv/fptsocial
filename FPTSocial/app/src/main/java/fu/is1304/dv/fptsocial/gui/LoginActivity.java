package fu.is1304.dv.fptsocial.gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.AsyncListUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fu.is1304.dv.fptsocial.R;

public class LoginActivity extends AppCompatActivity {
    private EditText txtEmail;
    private EditText txtPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                loginComplete();
                            } else {
                                Toast.makeText(LoginActivity.this, "Email và mật khẩu không đúng", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Email và mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
        }
    }



    private void loginComplete() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}