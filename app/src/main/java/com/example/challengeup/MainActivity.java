package com.example.challengeup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.challengeup.databinding.ActivityMainBinding;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Log.w(TAG, "Create Main Activity.");

        drawerLayout = binding.drawerLayout;
        navController = Navigation.findNavController(this, R.id.navHostFragment);

        appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.timeChallenges, R.id.challenges, R.id.newsFeed, R.id.profile)
                .setOpenableLayout(drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

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

            ActionBar actionBar = getSupportActionBar();
            if (destination.getId() == R.id.login) {
                if (actionBar != null)
                    actionBar.hide();
                binding.bottomNavigationView.setVisibility(View.GONE);
            } else {
                if (actionBar != null)
                    actionBar.show();
                binding.bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            LoginActivity loginActivity = new LoginActivity(this);
            loginActivity.createSignInIntent();
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
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.w(TAG, "Login successful.");
                // ...
            } else {
                Log.w(TAG, "Login unsuccessful.");
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}