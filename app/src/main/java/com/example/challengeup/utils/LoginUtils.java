package com.example.challengeup.utils;

import android.app.Activity;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LoginUtils {

    private static final int RC_SIGN_IN = 123;
    private Activity activity;

    public LoginUtils(Activity activity) {
        this.activity = activity;
    }

    public void createSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
//                new AuthUI.IdpConfig.FacebookBuilder().build(),
//                new AuthUI.IdpConfig.TwitterBuilder().build()
        );

        // Create and launch sign-in intent
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener(task -> {
                    // ...
                });
    }

    public void delete() {
        AuthUI.getInstance()
                .delete(activity)
                .addOnCompleteListener(task -> {
                    // ...
                });
    }

    public void themeAndLogo() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();

        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
//                        .setLogo(R.drawable.my_great_logo)
//                        .setTheme(R.style.MySuperAppTheme)
                        .build(),
                RC_SIGN_IN);
    }

    public void privacyAndTerms() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();

        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html")
                        .build(),
                RC_SIGN_IN);
    }
}
