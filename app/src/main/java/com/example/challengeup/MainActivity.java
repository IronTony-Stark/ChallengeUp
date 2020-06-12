package com.example.challengeup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

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
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.Result;
import com.example.challengeup.utils.LoginUtils;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.example.challengeup.viewModel.factory.MainActivityFactory;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    private ActivityMainBinding binding;
    private MainActivityViewModel mViewModel;
    private DrawerLayout drawerLayout;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);

        Container container = ((ApplicationContainer) getApplication()).mContainer;
        SharedPreferences preferences = getSharedPreferences(USER_DATA_KEY, MODE_PRIVATE);
        mViewModel = new ViewModelProvider(this, new MainActivityFactory(
                container.mRequestExecutor,
                preferences)
        ).get(MainActivityViewModel.class);

        drawerLayout = binding.drawerLayout;
        navController = Navigation.findNavController(this, R.id.navHostFragment);

        appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.timeChallenges, R.id.challenges, R.id.newsFeed, R.id.profile)
                .setOpenableLayout(drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

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
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration);
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

    @SuppressWarnings("unchecked")
    private void addUserToDbIfAbsent(FirebaseUser firebaseUser) {
        ICallback getUserCallback = result -> {
            if (result instanceof Result.Success) {
                UserEntity user = ((Result.Success<UserEntity>) result).data;

                if (user == null) {
                    user = new UserEntity("IronTonyStark", "Iron-Tony", firebaseUser.getEmail());
                    mViewModel.addUser(user, ignored -> {});
                }

                if (user.getPhoto() != null) {
                    Toast.makeText(this, user.getPhoto(), Toast.LENGTH_SHORT).show();
                    mViewModel.setUserAvatar(user.getPhoto());
                    mViewModel.saveUserAvatar(user.getPhoto());
                } else if (firebaseUser.getPhotoUrl() != null) {
//                    TODO firebaseUser.getPhotoUrl();
                }

                mViewModel.saveUserToSharedPreferences(user);
                mViewModel.refreshUserFromSharedPreferences();
            }
        };

        mViewModel.getUserByEmail(firebaseUser.getEmail(), getUserCallback);
        mViewModel.setLoadingUser();
    }

    private void setupDestinations() {
        final List<Integer> navDrawerUnlockedFragmentIds = new LinkedList<Integer>() {
            {
                add(Objects.requireNonNull(navController
                        .getGraph().findNode(R.id.timeChallenges)).getId());
                add(Objects.requireNonNull(navController
                        .getGraph().findNode(R.id.challenges)).getId());
                add(Objects.requireNonNull(navController
                        .getGraph().findNode(R.id.newsFeed)).getId());
                add(Objects.requireNonNull(navController
                        .getGraph().findNode(R.id.profile)).getId());
            }
        };

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (navDrawerUnlockedFragmentIds.contains(destination.getId()))
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            else
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        });
    }

    private void setupNavDrawer() {
        NavDrawerHeaderBinding headerBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.nav_drawer_header, binding.navView, false);
        binding.navView.addHeaderView(headerBinding.getRoot());
        headerBinding.setViewModel(mViewModel);
        headerBinding.setLifecycleOwner(this);

        mViewModel.refreshUserFromSharedPreferences();
        mViewModel.refreshUserAvatar();
        // TODO if avatar is absent set placeholder image
    }

    public static final String USER_DATA_KEY = "com.example.challengeup.userdata";
}