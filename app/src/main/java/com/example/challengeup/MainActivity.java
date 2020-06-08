package com.example.challengeup;

import android.content.Intent;
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

import com.example.challengeup.backend.User;
import com.example.challengeup.databinding.ActivityMainBinding;
import com.example.challengeup.databinding.NavDrawerHeaderBinding;
import com.example.challengeup.result.ICallback;
import com.example.challengeup.result.Result;
import com.example.challengeup.utils.LoginUtils;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "MainActivity";

    ActivityMainBinding binding;
    private MainActivityViewModel mViewModel;
    private DrawerLayout drawerLayout;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil
                .setContentView(this, R.layout.activity_main);

        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
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
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mViewModel.isAuthenticated()) {
//            setupNavDrawer();
        } else {
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
                addUserToDBifAbsent(user);
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    private void addUserToDBifAbsent(FirebaseUser user) {
        ICallback addUserCallback = result -> {
            if (result instanceof Result.Success) {
                String newUserId = ((Result.Success<String>) result).data;
                Toast.makeText(
                        MainActivity.this,
                        "Success " + user.getUid() + " " + newUserId,
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(
                        MainActivity.this,
                        "Can't add user to db",
                        Toast.LENGTH_LONG)
                        .show();
            }
        };

        ICallback getUserCallback = result -> {
            if (result instanceof Result.Success) {
                User dbUser = ((Result.Success<User>) result).data;
                if (dbUser == null) {
                    User newUser = new User(user.getUid(), "TagTagTag",
                            "NickName", user.getEmail());
                    mViewModel.addUser(newUser, addUserCallback);
                }
            } else {
                Toast.makeText(
                        MainActivity.this,
                        "Can't get user from db",
                        Toast.LENGTH_LONG)
                        .show();
            }
        };

        mViewModel.getUserById(user.getUid(), getUserCallback);
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
        String userId = mViewModel.getFirebaseUser().getUid();
        User user = User.getUserById(userId);
        if (user != null) {
            String nick = user.getNick();
            String tag = user.getNick();

            NavDrawerHeaderBinding headerBinding = NavDrawerHeaderBinding
                    .bind(binding.navView.getHeaderView(0));
            headerBinding.setNick(nick);
            headerBinding.setTag(tag);
        }
    }
}