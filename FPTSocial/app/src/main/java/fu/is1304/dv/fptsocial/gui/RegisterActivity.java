package fu.is1304.dv.fptsocial.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseAuthCallback;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseAuthActionCallBack;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtEmail, txtPass, txtRePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponents();
    }

    private void initComponents() {
        txtEmail = findViewById(R.id.txtRegEmail);
        txtPass = findViewById(R.id.txtRegPassord);
        txtRePass = findViewById(R.id.txtRegRePass);

        Intent data = getIntent();
        String email = data.getStringExtra("email");
        String pass = data.getStringExtra("password");
        txtEmail.setText(email);
        txtPass.setText(pass);
    }


    public void btnRegisterOnClick(View view) {
        String email = txtEmail.getText().toString();
        String password = txtPass.getText().toString();
        String repass = txtRePass.getText().toString();
        switch (DatabaseUtils.validateEmailAndPass(email, password, repass)) {
            case Const.VALIDATE_CODE_CORRECT:
                register(email, password);
                break;
            case Const.VALIDATE_CODE_EMPTY:
                Toast.makeText(this, "Email và mật khẩu không được để trống", Toast.LENGTH_LONG).show();
                break;
            case Const.VALIDATE_CODE_EMAIL_INCORRECT:
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_LONG).show();
                break;
            case Const.VALIDATE_CODE_NOT_MATCH:
                Toast.makeText(this, "Nhập lại mật khẩu không đúng", Toast.LENGTH_LONG).show();
                break;
            case Const.VALIDATE_CODE_PASS_INCORRECT:
                Toast.makeText(this, "Mật khẩu không hợp lệ", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void register(String email, String password) {
        AuthController.getInstance().registerByEmailAndPass(email, password, new FirebaseAuthCallback() {
            @Override
            public void onComplete(AuthResult result) {
                Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                AuthController.getInstance().sendVerifyEmail(new FirebaseAuthActionCallBack() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(RegisterActivity.this, "Kiểm tra mail và xác thực địa chỉ email của bạn", Toast.LENGTH_LONG).show();
                        moveToLogin();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(RegisterActivity.this, "Email không đúng, thử lại bằng email khác", Toast.LENGTH_LONG).show();
                        AuthController.getInstance().removeCurrentUser(new FirebaseAuthActionCallBack() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onFailure(Exception e) {

                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(RegisterActivity.this, "Đăng ký không thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void moveToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("mode", Const.MODE_CREATE_PROFILE);
        intent.putExtra("uid", AuthController.getInstance().getUID());
        startActivity(intent);
        finish();
    }

    private void moveToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}