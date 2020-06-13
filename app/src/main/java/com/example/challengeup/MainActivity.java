package com.example.challengeup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.databinding.ActivityMainBinding;
import com.example.challengeup.databinding.NavDrawerHeaderBinding;
import com.example.challengeup.dto.UserDTO;
import com.example.challengeup.fragment.CreateDialogFragment;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.Result;
import com.example.challengeup.utils.LoginUtils;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.example.challengeup.viewModel.factory.MainActivityFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements CreateDialogFragment.CreateDialogListener {

    private static final int RC_SIGN_IN = 123;

    private MainActivityViewModel mViewModel;
    private DrawerLayout mDrawerLayout;
    private BottomNavigationView mBottomNav;
    private NavigationView mNavView;
    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);

        Container container = ((ApplicationContainer) getApplication()).mContainer;
        SharedPreferences preferences = getSharedPreferences(USER_DATA_KEY, MODE_PRIVATE);
        File file = new File(getFilesDir(), AVATAR_FILE);
        mViewModel = new ViewModelProvider(this, new MainActivityFactory(
                container.mRequestExecutor,
                preferences,
                file)
        ).get(MainActivityViewModel.class);

        mDrawerLayout = binding.drawerLayout;
        mBottomNav = binding.bottomNavigationView;
        mNavView = binding.navView;

        setSupportActionBar(binding.toolbar);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.timeChallenges, R.id.challenges, R.id.newsFeed, R.id.profile)
                .setOpenableLayout(mDrawerLayout)
                .build();

        mNavController = Navigation.findNavController(this, R.id.navHostFragment);

        NavigationUI.setupWithNavController(binding.toolbar, mNavController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, mNavController);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, mNavController);

        setupDestinations();
        setupNavDrawer();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mViewModel.isAuthenticated()) {
            LoginUtils loginUtils = new LoginUtils(this);
            loginUtils.createSignInIntent();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (item.getItemId()) {
            case R.id.addMenuItem: {
                CreateDialogFragment dialog = new CreateDialogFragment();
                dialog.show(getSupportFragmentManager(), CREATE_DIALOG_TAG);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onChallengeClick() {
        mNavController.navigate(R.id.createChallenge);
    }

    @Override
    public void onPostClick() {
        Toast.makeText(this, "Feature is in progress", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = mViewModel.getFirebaseUser();
                addUserToDbIfAbsent(user);
            }
        }
    }

    private void addUserToDbIfAbsent(FirebaseUser firebaseUser) {
        ICallback getUserCallback = getUserResult -> {
            if (getUserResult instanceof Result.Success) {
                //noinspection unchecked
                UserEntity user = ((Result.Success<UserEntity>) getUserResult).data;

                if (user == null) {
                    user = new UserEntity("IronTony",
                            "IronTonyStark", firebaseUser.getEmail());
                    mViewModel.addUser(user, addUserResult -> {
                        if (addUserResult instanceof Result.Success) {
                            //noinspection unchecked
                            String userId = ((Result.Success<String>) addUserResult).data;
                            mViewModel.saveUserIdToSharedPreferences(userId);
                            mViewModel.refreshUserFromSharedPreferences();
                        }
                    });
                }

//                if (user.getPhoto() != null) {
//                    mViewModel.setUserAvatar(user.getPhoto());
//                    mViewModel.saveUserAvatar(user.getPhoto());
//                } else {
//                    TODO firebaseUser.getPhotoUrl();
//                }

                UserDTO userDTO = new UserDTO(user.getId(),
                        user.getNick(), user.getTag(), user.getInfo());

                mViewModel.saveUserToSharedPreferences(userDTO);
                mViewModel.refreshUserFromSharedPreferences();
            }
        };

        mViewModel.getUserByEmail(firebaseUser.getEmail(), getUserCallback);
        mViewModel.setLoadingUser();
    }

    private void setupDestinations() {
        final List<Integer> navDrawerUnlockedFragmentIds = new LinkedList<Integer>() {
            {
                add(Objects.requireNonNull(mNavController
                        .getGraph().findNode(R.id.timeChallenges)).getId());
                add(Objects.requireNonNull(mNavController
                        .getGraph().findNode(R.id.challenges)).getId());
                add(Objects.requireNonNull(mNavController
                        .getGraph().findNode(R.id.newsFeed)).getId());
                add(Objects.requireNonNull(mNavController
                        .getGraph().findNode(R.id.profile)).getId());
            }
        };

        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (navDrawerUnlockedFragmentIds.contains(destination.getId())) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mBottomNav.setVisibility(View.VISIBLE);
            } else {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mBottomNav.setVisibility(View.GONE);
            }
        });
    }

    private void setupNavDrawer() {
        NavDrawerHeaderBinding headerBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.nav_drawer_header, mNavView, false);
        mNavView.addHeaderView(headerBinding.getRoot());
        headerBinding.setViewModel(mViewModel);
        headerBinding.setLifecycleOwner(this);

        mViewModel.refreshUserFromSharedPreferences();
        mViewModel.refreshUserAvatar();
        // TODO if avatar is absent set placeholder image
    }

    public static final String USER_DATA_KEY = "com.example.challengeup.userdata";
    public static final String AVATAR_FILE = "AVATAR_FILE";
    public static final String CREATE_DIALOG_TAG = "CREATE_DIALOG_TAG";
}