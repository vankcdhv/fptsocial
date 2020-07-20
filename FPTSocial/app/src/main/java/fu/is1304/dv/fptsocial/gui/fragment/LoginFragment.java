package fu.is1304.dv.fptsocial.gui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.common.Const;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private EditText txtEmail;
    private EditText txtPassword;
    private CheckBox cbRemember;
    private ProgressBar pbLoading;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        initComponents(v);
        return v;
    }

    private void initComponents(View view) {
        txtEmail = view.findViewById(R.id.txtLoginEmail);
        txtPassword = view.findViewById(R.id.txtLoginPassword);
        cbRemember = view.findViewById(R.id.cbRememberMe);
        pbLoading = view.findViewById(R.id.progressBarLoginLoading);
        loadPassword();
    }

    public ProgressBar getPbLoading() {
        return pbLoading;
    }

    public void setPbLoading(ProgressBar pbLoading) {
        this.pbLoading = pbLoading;
    }

    public String getEmail() {
        return txtEmail.getText().toString();
    }

    public String getPassword() {
        return txtPassword.getText().toString();
    }

    public Boolean rememberIsChecked() {
        return cbRemember.isChecked();
    }

    public void setTxtEmail(String email) {
        txtEmail.setText(email);
    }

    public void setPasswrod(String passwrod) {
        txtPassword.setText(passwrod);
    }

    public void setCbRemember(Boolean isChecked) {
        cbRemember.setChecked(isChecked);
    }

    public void savePassword() {
        SharedPreferences preferences = this.getActivity().getSharedPreferences(Const.LOGIN_INFO_REFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (rememberIsChecked()) {
            editor.putString("email", getEmail()).putString("password", getPassword()).putBoolean("checkbox", rememberIsChecked());
            editor.commit();
        } else {
            editor.clear().commit();
        }
    }

    private void loadPassword() {
        SharedPreferences preferences = this.getActivity().getSharedPreferences(Const.LOGIN_INFO_REFERENCE, Context.MODE_PRIVATE);
        if (preferences != null) {
            String email = preferences.getString("email", "");
            String password = preferences.getString("password", "");
            Boolean checked = preferences.getBoolean("checkbox", false);
            setTxtEmail(email);
            setPasswrod(password);
            setCbRemember(checked);
        }
    }
}