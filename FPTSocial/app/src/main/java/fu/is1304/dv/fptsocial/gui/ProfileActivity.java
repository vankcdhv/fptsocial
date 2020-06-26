package fu.is1304.dv.fptsocial.gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.StorageDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageGetByteCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.User;
import fu.is1304.dv.fptsocial.gui.LoginActivity;

public class ProfileActivity extends AppCompatActivity {
    private EditText etFirstname;
    private EditText etLastname;
    private EditText etCourse;
    private EditText etGender;
    private TextView tvMajor;
    private EditText etDob;
    private User currentUser;
    private ImageView imgAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        setData();
    }

    public void init(){
        etFirstname = findViewById(R.id.etFirstname);
        etLastname = findViewById(R.id.etLastname);
        etCourse = findViewById(R.id.etCourse);
        etGender = findViewById(R.id.etGender);
        tvMajor = findViewById(R.id.tvMajor);
        etDob = findViewById(R.id.etDob);
        imgAvatar = findViewById(R.id.imgAvatar);



    }

    public void setData(){
        UserDAO.getInstance().getCurrentUser(new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                currentUser = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                etCourse.setText(currentUser.getCourse()+"");
                etFirstname.setText(currentUser.getFirstName());
                etLastname.setText(currentUser.getLastName());
                etGender.setText(currentUser.getGender());
                tvMajor.setText(currentUser.getDepartment());
                etDob.setText(currentUser.getDob());
                if(currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()){
                    StorageDAO.getInstance().getImage(currentUser.getAvatar(), new FirestorageGetByteCallback() {
                        @Override
                        public void onComplete(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imgAvatar.setImageBitmap(bitmap);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    public void save(View view){
        String UID = currentUser.getUID();
        String firstName = etFirstname.getText().toString();
        String lastName = etLastname.getText().toString();
        String gender = etGender.getText().toString();
        String dob = etDob.getText().toString();
        int course = Integer.parseInt(etCourse.getText().toString());
        String department = currentUser.getDepartment();
        String avatar = currentUser.getAvatar();
        String cover = currentUser.getCover();
        String startDate = currentUser.getStartDate();

        User user = new User(AuthController.getInstance().getCurrentUser().getUid(), firstName, lastName, gender, dob, course,
                department, avatar, cover, startDate);

        UserDAO.getInstance().updateUserData(user, new FirestoreSetCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ProfileActivity.this, "Update Successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ProfileActivity.this, "Update Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void back(View view){
        finish();
    }

}