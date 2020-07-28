package fu.is1304.dv.fptsocial.gui.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.bumptech.glide.Glide;
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
import fu.is1304.dv.fptsocial.gui.ChangePassActivity;
import fu.is1304.dv.fptsocial.gui.LoginActivity;
import fu.is1304.dv.fptsocial.gui.ProfileActivity;
import fu.is1304.dv.fptsocial.gui.fragmentcallback.ProfileCallBack;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView labelFullName;
    private EditText etFirstname;
    private EditText etLastname;
    private EditText etCourse;
    private Spinner spinnerGender;
    private Spinner edMajor;
    private TextView etDob;
    private User currentUser;
    private CircleImageView imgAvatar;
    private Button btnLogout, btnSave;
    private Uri ava;
    private ArrayAdapter genderAdapter;
    private ProgressBar pbLoading;
    private boolean isListening;
    private Button btnChangePass;

    private ArrayAdapter<String> departmentAdapter;
    private List<String> listDepartment;

    private ProfileCallBack callBack;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_layout, container, false);
        init(v);
        return v;
    }

    public void init(View view) {
        currentUser = new User();
        Date date = new Date();
        currentUser.setStartDate(new SimpleDateFormat("dd/MM/yyyy").format(date));
        labelFullName = view.findViewById(R.id.labelFullName);
        etFirstname = view.findViewById(R.id.etFirstname);
        etLastname = view.findViewById(R.id.etLastname);
        etCourse = view.findViewById(R.id.etCourse);
        spinnerGender = view.findViewById(R.id.spinnerGender);
        edMajor = view.findViewById(R.id.edMajor);
        etDob = view.findViewById(R.id.etDob);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        btnSave = view.findViewById(R.id.btnProfileSave);
        edMajor.setEnabled(false);
        genderAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, new String[]{getString(R.string.male), getString(R.string.female)});
        spinnerGender.setAdapter(genderAdapter);
        btnSave = view.findViewById(R.id.btnProfileSave);
        btnLogout = view.findViewById(R.id.btnLogout);
        pbLoading = view.findViewById(R.id.progressBarProfileLoading);
        btnChangePass = view.findViewById(R.id.btnChangePass);

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnChangePass(v);
            }
        });


        listDepartment = new ArrayList<>();
        DepartmentDAO.getInstance().getAllDepartment(new FirebaseGetCollectionCallback() {
            @Override
            public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                    String name = documentSnapshot.getString("name");
                    listDepartment.add(name);
                }
                departmentAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, listDepartment);
                edMajor.setAdapter(departmentAdapter);
                setData();
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
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthController.getInstance().signOut();
                openLoginActivity();
            }
        });
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAvatar(v);
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

    public void setData() {
        isListening = false;
        UserDAO.getInstance().getCurrentUser(new FirestoreGetCallback() {
            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {
                currentUser = DatabaseUtils.convertDocumentSnapshotToUser(documentSnapshot);
                etCourse.setText(currentUser.getCourse() + "");
                etFirstname.setText(currentUser.getFirstName());
                etLastname.setText(currentUser.getLastName());
                if (currentUser.getGender().equals(getString(R.string.male))) {
                    spinnerGender.setSelection(0);
                } else {
                    spinnerGender.setSelection(1);
                }
                edMajor.setSelection(listDepartment.indexOf(currentUser.getDepartment()));
                etDob.setText(currentUser.getDob());
                if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
                    Glide.with(getActivity()).load(currentUser.getAvatar()).into(imgAvatar);
                } else {
                    if (currentUser.getGender().equals(getString(R.string.female))) {
                        imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.nu));
                    }
                }
                isListening = true;
            }

            @Override
            public void onFailure(Exception e) {

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
                Toast.makeText(getActivity(), R.string.update_successfuly, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                loading(false);
                Toast.makeText(getActivity(), R.string.have_error, Toast.LENGTH_SHORT).show();
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
                loading(false);
                Toast.makeText(getActivity(), R.string.upload_image_post_failed, Toast.LENGTH_LONG).show();
            }
        });
    }


    public void selectAvatar(View view) {
        Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        myIntent.setType("image/*");
        getActivity().startActivityForResult(myIntent, Const.REQUEST_CODE_CHOSE_AVA);
    }

    public void changeAvatar(int resultCode, @Nullable Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            ava = data.getData();
            imgAvatar.setImageURI(ava);
            currentUser.setAvatar("images/avatar/" + AuthController.getInstance().getCurrentUser().getUid() + "/" + ava.getLastPathSegment());
        }
    }

    public void selectDate(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etDob.setText(dayOfMonth + "/" + month + "/" + year);
            }
        }, 2000, 01, 01);
        datePickerDialog.show();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void loading(boolean isLoading) {
        if (isLoading) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            pbLoading.setVisibility(View.VISIBLE);
        } else {

            pbLoading.setVisibility(View.INVISIBLE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }

    public void btnChangePass(View v) {
        Intent intent = new Intent(getContext(), ChangePassActivity.class);
        startActivity(intent);
    }
}