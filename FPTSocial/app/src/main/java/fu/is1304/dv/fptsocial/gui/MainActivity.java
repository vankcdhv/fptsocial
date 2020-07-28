package fu.is1304.dv.fptsocial.gui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

import fu.is1304.dv.fptsocial.R;
import fu.is1304.dv.fptsocial.business.AuthController;
import fu.is1304.dv.fptsocial.common.Const;
import fu.is1304.dv.fptsocial.common.DatabaseUtils;
import fu.is1304.dv.fptsocial.dao.FriendDAO;
import fu.is1304.dv.fptsocial.dao.callback.FirebaseGetCollectionCallback;
import fu.is1304.dv.fptsocial.entity.Friend;
import fu.is1304.dv.fptsocial.gui.fragment.MessengerFragment;
import fu.is1304.dv.fptsocial.gui.fragment.NewfeedFragment;
import fu.is1304.dv.fptsocial.gui.fragment.NotificationFragment;
import fu.is1304.dv.fptsocial.gui.fragment.ProfileFragment;
import fu.is1304.dv.fptsocial.gui.service.NotifyService;
import fu.is1304.dv.fptsocial.gui.viewmodel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private MainActivityViewModel viewModel;
    private BottomNavigationView navigation;
    private NewfeedFragment newfeedFragment;
    private MessengerFragment messengerFragment;
    private NotificationFragment notificationFragment;
    private ProfileFragment profileFragment;
    private EditText txtSearchKey;
    private ImageButton btnSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }

    private void initComponents() {
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        loadListFriend();

        txtSearchKey = findViewById(R.id.txtSearchKey);

        newfeedFragment = new NewfeedFragment();
        messengerFragment = new MessengerFragment();
        notificationFragment = new NotificationFragment();
        profileFragment = new ProfileFragment();
        setToolbar();

        if (AuthController.getInstance().getCurrentUser() == null) openLoginActivity();
        if (!AuthController.getInstance().getCurrentUser().isEmailVerified()) {
            openLoginActivity();
        }
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        loadFragment(newfeedFragment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startNotifyService();
        }
        //navigation.setSelectedItemId(R.id.navigation_profile);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startNotifyService() {
        Intent intent = new Intent(this, NotifyService.class);
        startService(intent);
        //startForegroundService(intent);
    }


    private void loadListFriend() {
        FriendDAO.getInstance().getAllFriendOfUser(AuthController.getInstance().getUID(), new FirebaseGetCollectionCallback() {
            @Override
            public void onComplete(List<QueryDocumentSnapshot> documentSnapshots) {
                List<Friend> friendList = DatabaseUtils.convertListDocSnapToListFriend(documentSnapshots);
                viewModel.setListFriend(friendList);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(MainActivity.this, "Load friend fail", Toast.LENGTH_LONG).show();
            }
        });
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

    private void openWallActivity() {
        Intent intent = new Intent(this, WallActivity.class);
        startActivity(intent);
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
            case Const.REQUEST_CODE_CHOSE_STATUS_IMAGE:
                newfeedFragment.changeImage(resultCode, data);
                break;
        }
    }

    public void openSearchActivity(View view) {
        String keyword = txtSearchKey.getText().toString();
        if (keyword != null && keyword.length() > 0) {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("keyword", keyword);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.must_input_text, Toast.LENGTH_LONG).show();
        }
    }
}