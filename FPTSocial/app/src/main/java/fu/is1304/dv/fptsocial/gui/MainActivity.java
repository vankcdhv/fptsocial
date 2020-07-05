package fu.is1304.dv.fptsocial.gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.gui.viewmodel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private MainActivityViewModel viewModel;
    private BottomNavigationView navigation;

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
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    viewProfile();
                    return true;
                case R.id.navigation_shop:
                    //viewProfile();
                    return true;
                case R.id.navigation_gifts:
                    //viewProfile();
                    return true;
                case R.id.navigation_cart:
                    //viewProfile();
                    return true;
            }
            return false;
        }
    };

    private void setToolbar() {
        actionBar = getSupportActionBar();
        actionBar.hide();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void btnLogoutClick(View v) {
        AuthController.getInstance().signOut();
        openLoginActivity();
    }

    public void viewProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("mode", Const.MODE_UPDATE_PROFILE);
        startActivity(intent);
    }
}