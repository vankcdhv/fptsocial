package fu.is1304.dv.fptsocial.gui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.gui.fragment.MessengerFragment;
import fu.is1304.dv.fptsocial.gui.fragment.NewfeedFragment;
import fu.is1304.dv.fptsocial.gui.fragment.NotificationFragment;
import fu.is1304.dv.fptsocial.gui.fragment.ProfileFragment;
import fu.is1304.dv.fptsocial.gui.viewmodel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private MainActivityViewModel viewModel;
    private BottomNavigationView navigation;
    private NewfeedFragment newfeedFragment;
    private MessengerFragment messengerFragment;
    private NotificationFragment notificationFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }

    private void initComponents() {
        newfeedFragment = new NewfeedFragment();
        messengerFragment = new MessengerFragment();
        notificationFragment = new NotificationFragment();
        profileFragment = new ProfileFragment();
        setToolbar();
        //Init components

        if (AuthController.getInstance().getCurrentUser() == null) openLoginActivity();
        if (!AuthController.getInstance().getCurrentUser().isEmailVerified()) {
            openLoginActivity();
        }
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        loadFragment(newfeedFragment);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_shop:
                    loadFragment(newfeedFragment);
                    return true;
                case R.id.navigation_gifts:
                    loadFragment(messengerFragment);
                    return true;
                case R.id.navigation_cart:
                    loadFragment(notificationFragment);
                    //viewProfile();
                    return true;
                case R.id.navigation_profile:
                    //viewProfile();
                    loadFragment(profileFragment);
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

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment, "CURRENT_FRAGMENT");
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Const.REQUEST_CODE_CHOSE_AVA:
                profileFragment.changeAvatar(resultCode, data);
                break;
        }
    }
}