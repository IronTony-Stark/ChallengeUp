package com.example.challengeup;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.databinding.ActivityMainBinding;
import com.example.challengeup.databinding.NavDrawerHeaderBinding;
import com.example.challengeup.dto.UserDTO;
import com.example.challengeup.fragment.dialog.BlockingLoaderDialog;
import com.example.challengeup.fragment.dialog.CreateDialogFragment;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.Result;
import com.example.challengeup.utils.LoginUtils;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.example.challengeup.viewModel.factory.MainActivityFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements
        CreateDialogFragment.CreateDialogListener,
        ILoadable, IBlockingLoadable, IUIConfig {

    private MainActivityViewModel mViewModel;
    private DrawerLayout mDrawerLayout;
    private BottomNavigationView mBottomNav;
    private NavigationView mNavView;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBlockingLoader = new BlockingLoaderDialog(this);

        ActivityMainBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);

        Container container = ((ApplicationContainer) getApplication()).mContainer;
        SharedPreferences preferences = getSharedPreferences(USER_DATA_KEY, MODE_PRIVATE);
        mViewModel = new ViewModelProvider(this, new MainActivityFactory(
                container.mRequestExecutor,
                preferences)
        ).get(MainActivityViewModel.class);

        mDrawerLayout = binding.drawerLayout;
        mBottomNav = binding.bottomNavigationView;
        mNavView = binding.navView;
        mLoader = binding.progressBar;

        setSupportActionBar(binding.toolbar);

        mAppBarConfiguration = new AppBarConfiguration
                .Builder(R.id.timeChallenges, R.id.challenges, R.id.newsFeed)
                .setOpenableLayout(mDrawerLayout)
                .build();

        mNavController = Navigation.findNavController(this, R.id.navHostFragment);

        NavigationUI.setupWithNavController(binding.toolbar, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, mNavController);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, mNavController);

        setupDestinations();
        setupNavDrawer();

        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
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
    public void startLoading() {
        mLoader.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishLoading() {
        mLoader.setVisibility(View.INVISIBLE);
    }

    @Override
    public void startBlockingLoading(int timeoutMillis) {
        mBlockingLoader.startBlockingLoading(timeoutMillis);
    }

    @Override
    public void finishBlockingLoading() {
        mBlockingLoader.finishBlockingLoading();
    }

    @Override
    public void setAppBarVisibility(boolean show) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (show) actionBar.show();
            else actionBar.hide();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = mViewModel.getFirebaseUser();
                getUserFromDbOrNavigateToFTUE(user);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Log.w("Main", "Permission granted");
            else
                Log.w("Main", "Permission denied");
        }
    }

    @SuppressWarnings("unchecked")
    private void getUserFromDbOrNavigateToFTUE(FirebaseUser firebaseUser) {
        ICallback getUserCallback = getUserResult -> {
            if (getUserResult instanceof Result.Success) {
                //noinspection unchecked
                UserEntity user = ((Result.Success<UserEntity>) getUserResult).data;

                finishBlockingLoading();

                if (user != null) {
                    if (user.getPhoto() != null) {
                        String userPhoto = user.getPhoto();
                        mViewModel.setUserAvatar(userPhoto);
                        mViewModel.saveUserAvatar(userPhoto);
                    } else if (firebaseUser.getPhotoUrl() != null) {
                        String firebaseUserPhoto = firebaseUser.getPhotoUrl().toString();
                        mViewModel.setUserAvatar(firebaseUserPhoto);
                        mViewModel.saveUserAvatar(firebaseUserPhoto);
                    }

                    UserDTO userDTO = new UserDTO(user.getId(),
                            user.getNick(), user.getTag(), user.getInfo());

                    mViewModel.saveUserToSharedPreferences(userDTO);
                    mViewModel.refreshUserFromSharedPreferences();
                } else {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setName(firebaseUser.getDisplayName());
                    mViewModel.setUser(userDTO);

                    if (firebaseUser.getPhotoUrl() != null) {
                        String firebaseUserPhoto = firebaseUser.getPhotoUrl().toString();
                        mViewModel.saveUserAvatar(firebaseUserPhoto);
                    }

                    NavOptions navOptions = new NavOptions
                            .Builder()
                            .setPopUpTo(R.id.newsFeed, true)
                            .build();
                    mNavController.navigate(R.id.ftue, null, navOptions);
                }
            } else {
                Toast.makeText(this,
                        "Something bad happened.. " +
                                "Please contact our support or other idiots who've made this shit",
                        Toast.LENGTH_SHORT).show();
            }
        };

        startBlockingLoading(0);

        mViewModel.getUserByEmail(firebaseUser.getEmail(), getUserCallback);
    }

    private void setupDestinations() {
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (mAppBarConfiguration.getTopLevelDestinations().contains(destination.getId())) {
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

        if (mViewModel.isAdmin()) {
            Menu menuNav = mNavView.getMenu();
            MenuItem item = menuNav.findItem(R.id.reportedChallenges);
            item.setVisible(true);
        }

        mViewModel.refreshUserFromSharedPreferences();
        mViewModel.refreshUserAvatar();
    }

    private ProgressBar mLoader;
    private BlockingLoaderDialog mBlockingLoader;

    private static final int RC_SIGN_IN = 123;
    private static final int MY_PERMISSIONS_REQUEST = 124;

    public static final String USER_DATA_KEY = "com.example.challengeup.userdata";
    public static final String CREATE_DIALOG_TAG = "CREATE_DIALOG_TAG";
}