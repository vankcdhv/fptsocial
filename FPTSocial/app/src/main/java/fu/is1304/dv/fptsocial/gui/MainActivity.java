package fu.is1304.dv.fptsocial.gui;

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

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
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
        FirebaseUser user = firebaseAuth.getCurrentUser();
        while (user==null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            user = firebaseAuth.getCurrentUser();
        }
        labelEmail.setText(user.getEmail());
    }

    public void logout(View v){
        firebaseAuth.signOut();
        init();
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