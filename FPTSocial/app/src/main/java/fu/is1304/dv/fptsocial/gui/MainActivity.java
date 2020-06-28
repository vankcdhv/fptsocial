package fu.is1304.dv.fptsocial.gui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.dao.UserDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirestoreSetCallback;
import fu.is1304.dv.fptsocial.entity.User;

public class MainActivity extends AppCompatActivity {
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();

    }

    private void initComponents() {
        setToolbar();
        //Init components

        if (AuthController.getInstance().getCurrentUser() == null) openLoginActivity();
        if (!AuthController.getInstance().getCurrentUser().isEmailVerified()) {
            openLoginActivity();
        }

    }

    private void setToolbar() {
        actionBar = getSupportActionBar();
        actionBar.hide();
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
        User user = new User(AuthController.getInstance().getCurrentUser().getUid(), "Lê Thiện", "Văn", "nam", "18/02/1999", 13,
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
        intent.putExtra("mode", Const.MODE_UPDATE_PROFILE);
        startActivity(intent);

    }
}