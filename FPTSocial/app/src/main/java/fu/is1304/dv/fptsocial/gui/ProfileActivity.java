package fu.is1304.dv.fptsocial.gui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.DepartmentDAO;
import fu.is1304.dv.fptsocial.dao.StorageDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageGetByteCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageUploadCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.User;

public class ProfileActivity extends AppCompatActivity {
    private TextView labelFullName;
    private EditText etFirstname;
    private EditText etLastname;
    private EditText etCourse;
    private Spinner spinnerGender;
    private Spinner edMajor;
    private TextView etDob;
    private User currentUser;
    private CircleImageView imgAvatar;
    private Button btnBack, btnSave;
    private Uri ava;
    private ArrayAdapter<String> genderAdapter;
    private ProgressBar pbLoading;
    private boolean isListening;
    private ArrayAdapter<String> departmentAdapter;
    private List<String> listDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        init();
    }

    public void init() {
        isListening = true;
        currentUser = new User();
        Date date = new Date();
        currentUser.setStartDate(new SimpleDateFormat("dd/MM/yyyy").format(date));
        etFirstname = findViewById(R.id.etFirstname);
        etLastname = findViewById(R.id.etLastname);
        etCourse = findViewById(R.id.etCourse);
        spinnerGender = findViewById(R.id.spinnerGender);
        edMajor = findViewById(R.id.edMajor);
        etDob = findViewById(R.id.etDob);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnBack = findViewById(R.id.btnLogout);
        btnSave = findViewById(R.id.btnProfileSave);
        labelFullName = findViewById(R.id.labelFullName);
        pbLoading = findViewById(R.id.progressBarProfileLoading);

        btnBack.setVisibility(View.INVISIBLE);
        edMajor.setEnabled(true);

        genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{getString(R.string.male), getString(R.string.female)});
        spinnerGender.setAdapter(genderAdapter);

        listDepartment = new ArrayList<>();
        DepartmentDAO.getInstance().getAllDepartment(new FirebaseGetCollectionCallback() {
            @Override
            public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                    String name = documentSnapshot.getString("name");
                    listDepartment.add(name);
                }
                departmentAdapter = new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, listDepartment);
                edMajor.setAdapter(departmentAdapter);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });

        setEvent();
    }


    private void setEvent() {
        isListening = true;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(v);
            }
        });
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAvatar(v);
            }
        });
        etFirstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                labelFullName.setText(etFirstname.getText().toString() + " " + etLastname.getText().toString());
            }
        });
        etLastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                labelFullName.setText(etFirstname.getText().toString() + " " + etLastname.getText().toString());
            }
        });
        etCourse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isListening) return;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isListening) return;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isListening) return;
                isListening = false;
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int cource = 0;
                if (!(etCourse.getText() != null && etCourse.getText().toString().isEmpty())) {
                    cource = Integer.parseInt(etCourse.getText().toString().trim());
                    if (cource > (year - 2004)) {
                        cource = year - 2004;
                        etCourse.setText(cource + "");
                        etCourse.setSelection(etCourse.getText().length());
                    }
                    if (cource < 1) {
                        etCourse.setText("1");
                        etCourse.setSelection(etCourse.getText().length());
                    }
                }
                isListening = true;
            }
        });
        etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(v);
            }
        });
    }

    public void save(View view) {
        loading(true);
        String UID = AuthController.getInstance().getUID();
        String firstName = etFirstname.getText().toString();
        String lastName = etLastname.getText().toString();
        String gender = spinnerGender.getSelectedItem().toString();
        String dob = etDob.getText().toString();
        int course = Integer.parseInt(etCourse.getText().toString());
        String department = edMajor.getSelectedItem().toString();
        String avatar = currentUser.getAvatar();
        String cover = currentUser.getCover();
        String startDate = currentUser.getStartDate();

        User user = new User(UID, firstName, lastName, gender, dob, course,
                department, avatar, cover, startDate);

        if (ava != null) {
            uploadImage(user);
        } else {
            saveInfomation(user);
        }
    }

    private void saveInfomation(User user) {
        UserDAO.getInstance().updateUserData(user, new FirestoreSetCallback() {
            @Override
            public void onSuccess(String userID) {
                loading(false);
                Toast.makeText(ProfileActivity.this, R.string.update_successfuly, Toast.LENGTH_SHORT).show();
                moveToMain();
            }

            @Override
            public void onFailure(Exception e) {
                loading(false);
                Toast.makeText(ProfileActivity.this, R.string.have_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage(final User user) {
        StorageDAO.getInstance().upImage(currentUser.getAvatar(), ava, new FirestorageUploadCallback() {
            @Override
            public void onComplete(Uri uri) {
                String url = uri.toString();
                user.setAvatar(url);
                saveInfomation(user);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ProfileActivity.this, R.string.upload_image_post_failed, Toast.LENGTH_LONG).show();
                loading(false);

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

    public void changeAvatar(View view) {
        Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        myIntent.setType("image/*");
        startActivityForResult(myIntent, Const.REQUEST_CODE_CHOSE_AVA);
    }

    public void selectDate(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etDob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, 2000, 01, 01);
        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.REQUEST_CODE_CHOSE_AVA) {
            if (resultCode == RESULT_OK) {
                ava = data.getData();
                imgAvatar.setImageURI(ava);
                currentUser.setAvatar("images/avatar/" + AuthController.getInstance().getCurrentUser().getUid() + "/" + ava.getLastPathSegment());
            }
        }
    }

    public void loading(boolean isLoading) {
        if (isLoading) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            pbLoading.setVisibility(View.VISIBLE);
        } else {

            pbLoading.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }
}