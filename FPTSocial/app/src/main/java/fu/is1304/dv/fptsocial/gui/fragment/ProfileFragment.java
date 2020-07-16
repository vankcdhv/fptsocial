package fu.is1304.dv.fptsocial.gui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.StorageDAO;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageGetByteCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestorageUploadCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreGetCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.User;
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
    private EditText etFirstname;
    private EditText etLastname;
    private EditText etCourse;
    private Spinner spinnerGender;
    private EditText edMajor;
    private EditText etDob;
    private User currentUser;
    private ImageView imgAvatar;
    private Button btnBack, btnSave;
    private Uri ava;
    private ArrayAdapter genderAdapter;

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
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        init(v);
        return v;
    }

    public void init(View view) {
        currentUser = new User();
        Date date = new Date();
        currentUser.setStartDate(new SimpleDateFormat("dd/MM/yyyy").format(date));
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

        setEvent();

        setData();
    }

    private void setEvent() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(v);
            }
        });
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAvatar(v);
            }
        });
    }

    public void setData() {
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
                edMajor.setText(currentUser.getDepartment());
                etDob.setText(currentUser.getDob());
                if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
                    StorageDAO.getInstance().getImage(currentUser.getAvatar(), new FirestorageGetByteCallback() {
                        @Override
                        public void onStart() {
                            Glide.with(getActivity()).load(getActivity().getDrawable(R.drawable.loading_spin)).into(imgAvatar);
                        }

                        @Override
                        public void onComplete(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imgAvatar.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onFailed(Exception e) {

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
        String gender = spinnerGender.getSelectedItem().toString();
        String dob = etDob.getText().toString();
        int course = Integer.parseInt(etCourse.getText().toString());
        String department = edMajor.getText().toString();
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
            public void onSuccess() {
                Toast.makeText(getActivity(), "Update Successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), "Update Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage(final User user) {
        StorageDAO.getInstance().upImage(currentUser.getAvatar(), ava, new FirestorageUploadCallback() {
            @Override
            public void onComplete(UploadTask.TaskSnapshot taskSnapshot) {
                saveInfomation(user);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), "Update your avatar failed, your new profile is not update", Toast.LENGTH_LONG).show();
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
}