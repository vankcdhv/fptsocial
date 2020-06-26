package fu.is1304.dv.fptsocial.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.StorageDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageGetByteCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.User;

public class ProfileActivity extends AppCompatActivity {
    private EditText etFirstname;
    private EditText etLastname;
    private EditText etCourse;
    private EditText etGender;
    private EditText edMajor;
    private EditText etDob;
    private User currentUser;
    private ImageView imgAvatar;
    private String mode;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        if (mode.equals(Const.MODE_UPDATE_PROFILE)) {
            setData();
        }
    }

    public void init() {
        currentUser = new User();
        Date date = new Date();
        currentUser.setStartDate(new SimpleDateFormat("dd/MM/yyyy").format(date));
        etFirstname = findViewById(R.id.etFirstname);
        etLastname = findViewById(R.id.etLastname);
        etCourse = findViewById(R.id.etCourse);
        etGender = findViewById(R.id.etGender);
        edMajor = findViewById(R.id.edMajor);
        etDob = findViewById(R.id.etDob);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnBack = findViewById(R.id.btnBackProfile);
        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");
        edMajor.setEnabled(false);
        if (mode.equals(Const.MODE_CREATE_PROFILE)) {
            btnBack.setVisibility(View.INVISIBLE);
            edMajor.setEnabled(true);
        }
    }

    public void setData() {
        UserDAO.getInstance().getCurrentUser(new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                currentUser = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                etCourse.setText(currentUser.getCourse() + "");
                etFirstname.setText(currentUser.getFirstName());
                etLastname.setText(currentUser.getLastName());
                etGender.setText(currentUser.getGender());
                edMajor.setText(currentUser.getDepartment());
                etDob.setText(currentUser.getDob());
                if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
                    StorageDAO.getInstance().getImage(currentUser.getAvatar(), new FirestorageGetByteCallback() {
                        @Override
                        public void onComplete(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imgAvatar.setImageBitmap(bitmap);
                        }
                    });
                } else {
                    if (currentUser.getGender().equals(getString(R.string.female))) {
                        imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.nu));
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    public void save(View view) {

        String UID = AuthController.getInstance().getUID();
        String firstName = etFirstname.getText().toString();
        String lastName = etLastname.getText().toString();
        String gender = etGender.getText().toString();
        String dob = etDob.getText().toString();
        int course = Integer.parseInt(etCourse.getText().toString());
        String department = edMajor.getText().toString();
        String avatar = currentUser.getAvatar();
        String cover = currentUser.getCover();
        String startDate = currentUser.getStartDate();

        User user = new User(UID, firstName, lastName, gender, dob, course,
                department, avatar, cover, startDate);

        UserDAO.getInstance().updateUserData(user, new FirestoreSetCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ProfileActivity.this, "Update Successfully!", Toast.LENGTH_SHORT).show();
                if (mode.equals(Const.MODE_CREATE_PROFILE)) {
                    moveToMain();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ProfileActivity.this, "Update Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void moveToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void back(View view) {
        finish();
    }

}