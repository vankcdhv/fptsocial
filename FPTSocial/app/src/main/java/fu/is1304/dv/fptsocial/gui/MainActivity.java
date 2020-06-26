package fu.is1304.dv.fptsocial.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.StorageDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageGetByteCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.User;

public class MainActivity extends AppCompatActivity {

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
        //Init components
        imgTest = findViewById(R.id.imgTest);
        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        if (AuthController.getInstance().getCurrentUser() == null) openLoginActivity();

        //Get data from storage
        StorageDAO.getInstance().getImage("images/4.JPG", new FirestorageGetByteCallback() {
            @Override
            public void onComplete(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgTest.setImageBitmap(bitmap);
            }
        });

        UserDAO.getInstance().getUserRealtime(AuthController.getInstance().getUID(), new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                User user = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                txtName.setText(user.getFirstName() + " " + user.getLastName());
                try {
                    txtAge.setText(((new Date()).getYear() - (new SimpleDateFormat("dd/MM/yyyy").parse(user.getDob()).getYear())) + "");
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Lấy thông tin thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this, "Lấy thông tin thất bại", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void btnLogoutClick(View v) {
        AuthController.getInstance().signOut();
        openLoginActivity();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void btnAddOnClick(View v) {
        User user = new User(AuthController.getInstance().getCurrentUser().getUid(), "Lê Thiện", "Văn", "Nam", "18/02/1999", 13,
                "Kỹ Thuật Phần Mềm", null, null, "24/06/2020");

        UserDAO.getInstance().updateUserData(user, new FirestoreSetCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Update thông tin thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this, "Update thông tin thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void viewProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
}