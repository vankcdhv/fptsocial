package fu.is1304.dv.fptsocial.gui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fu.is1304.dv.fptsocial.ProfileActivity;
import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.entity.User;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference dbReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;


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

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseUser == null) openLoginActivity();

        imgTest = findViewById(R.id.imgTest);
        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);

        //Get data from storage
        storageReference.child("images/4.JPG").getBytes(1024 * 1024 * 20)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        imgTest.setImageBitmap(bitmap);
                    }
                });
        //Get data from firestore (real time)
        firebaseFirestore.collection("users").document(firebaseUser.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Errorrr", "fail", e);
                            return;
                        } else {
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                Log.d("Data", "Current data: " + documentSnapshot.getData());
                                Gson gson = new Gson();
                                User user = gson.fromJson(gson.toJson(documentSnapshot.getData()), User.class);
                                txtName.setText(user.getFirstName() + " " + user.getLastName());
                                try {
                                    txtAge.setText(((new Date()).getYear() - (new SimpleDateFormat("dd/MM/yyyy").parse(user.getDob()).getYear())) + "");
                                } catch (ParseException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                Log.w("Errorrr", "Data null");
                            }
                        }
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
                    "Kỹ Thuật Phần Mềm", null, null, "24/06/2020");
        } catch (Exception e) {
            e.printStackTrace();
        }
        firebaseFirestore.collection("users").document(firebaseUser.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Done", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Loiii", "Error writing document", e);
                    }
                });
    }


    public void viewProfile(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
}