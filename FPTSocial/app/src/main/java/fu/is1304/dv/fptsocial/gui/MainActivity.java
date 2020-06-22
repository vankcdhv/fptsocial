package fu.is1304.dv.fptsocial.gui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.common.Const;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextView labelEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        init();
    }

    private void init(){
        labelEmail = findViewById(R.id.labelEmail);
        firebaseAuth = FirebaseAuth.getInstance();
        setUserInfo();
    }

    public void logout(View v){
        firebaseAuth.signOut();
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Const.REQUEST_CODE_LOGIN:
                setUserInfo();
                break;
        }
    }

    private void setUserInfo(){
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser==null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, Const.REQUEST_CODE_LOGIN);
        } else {
            labelEmail.setText(firebaseUser.getEmail());
        }
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