package fu.is1304.dv.fptsocial.gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.entity.User;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference dbReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


    private EditText txtName, txtAge;
    private ImageView imgTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();

    }

    private void initComponents() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbReference = firebaseDatabase.getReference();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        if (firebaseUser == null) openLoginActivity();

        imgTest = findViewById(R.id.imgTest);
        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);


        dbReference.child("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String key = snapshot.getKey();
                    Gson gson = new Gson();
                    User user = gson.fromJson(gson.toJson(snapshot.getValue()), User.class);
                    txtName.setText(user.getFirstName() + " " + user.getLastName());
                    txtAge.setText(((new Date()).getYear() - (new SimpleDateFormat("dd/MM/yyyy").parse(user.getDob()).getYear())) + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        storageReference.child("images/4.JPG").getBytes(1024 * 1024 * 20)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        imgTest.setImageBitmap(bitmap);
                    }
                });
    }


    public void btnLogoutClick(View v) {
        firebaseAuth.signOut();
        openLoginActivity();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void btnAddOnClick(View v) {
        String name = txtName.getText().toString();
        int age = Integer.parseInt(txtAge.getText().toString().trim());
        User user = null;
        try {
            user = new User(firebaseUser.getUid(), "Lê Thiện", "Văn", true, "18/02/1999", 13,
                    "Kỹ Thuật Phần Mềm", null, null,"24/06/2020");
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbReference.child("users").child(firebaseUser.getUid()).setValue(user);
    }

}