package fu.is1304.dv.fptsocial.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;

public class ChangePassActivity extends AppCompatActivity {
    private EditText txtEmail, txtPass, txtRePass;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        initComponents();
    }


    private void initComponents() {
        getSupportActionBar().hide();
        txtPass = findViewById(R.id.txtChangePassword);
        txtRePass = findViewById(R.id.txtChangeRePassword);
        pbLoading = findViewById(R.id.progressBarLoginLoading);
    }

    public void btnChangePass(View view) {
        loading(true);
        String email = ".";
        String password = txtPass.getText().toString();
        String repass = txtRePass.getText().toString();
        switch (DatabaseUtils.validateEmailAndPass(email, password, repass)) {
            case Const.VALIDATE_CODE_CORRECT:
                AuthController.getInstance().changePass(password, new FirestoreSetCallback() {
                    @Override
                    public void onSuccess(String id) {
                        loading(false);
                        Toast.makeText(ChangePassActivity.this, R.string.update_successfuly, Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        loading(false);
                        Toast.makeText(ChangePassActivity.this, R.string.have_error, Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case Const.VALIDATE_CODE_EMPTY:
                loading(false);
                Toast.makeText(this, "Email và mật khẩu không được để trống", Toast.LENGTH_LONG).show();
                break;
            case Const.VALIDATE_CODE_EMAIL_INCORRECT:
                loading(false);
                Toast.makeText(this, "Email không hợp lệ (Chỉ sử dụng mail FPT)", Toast.LENGTH_LONG).show();
                break;
            case Const.VALIDATE_CODE_NOT_MATCH:
                loading(false);
                Toast.makeText(this, "Nhập lại mật khẩu không đúng", Toast.LENGTH_LONG).show();
                break;
            case Const.VALIDATE_CODE_PASS_INCORRECT:
                loading(false);
                Toast.makeText(this, "Mật khẩu không hợp lệ", Toast.LENGTH_LONG).show();
                break;
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