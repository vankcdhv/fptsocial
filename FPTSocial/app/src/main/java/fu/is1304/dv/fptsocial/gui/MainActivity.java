package fu.is1304.dv.fptsocial.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import fu.is1304.dv.fptsocial.R;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private EditText txtName, txtAge;
    private DatabaseReference dbReference;
    private FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();

    }
    private void initComponents() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser==null) openLoginActivity();
        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtPassword);
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbReference = firebaseDatabase.getReference("users");
    }

    public void btnLogoutClick(View v){
        firebaseAuth.signOut();
        openLoginActivity();
    }

    private void openLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void btnAddOnClick(View v){
        String name = txtName.getText().toString();
        int age = Integer.parseInt(txtAge.getText().toString().trim());

    }

}