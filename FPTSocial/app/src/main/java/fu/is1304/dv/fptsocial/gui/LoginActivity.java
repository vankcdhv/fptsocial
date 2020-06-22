package fu.is1304.dv.fptsocial.gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.common.Const;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText txtUsername, txtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    public void init(){
        firebaseAuth = FirebaseAuth.getInstance();
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
    }

    public void btnLogin(View v){
        String email = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        if(!email.isEmpty() && !password.isEmpty()){
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                        //getResources();
                        Toast.makeText(LoginActivity.this, "Thành công", Toast.LENGTH_LONG).show();
                        setResult(Const.RESULT_CODE_COMPLETE);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Thất bại", Toast.LENGTH_LONG).show();
                    }
                });
        }
    }


}