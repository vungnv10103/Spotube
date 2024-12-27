package com.chagnahnn.spotube.service.firebase;

import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

@SuppressWarnings("unused")
public class FirebaseAuthFactory {
    private final FirebaseAuth mFirebaseAuth;

    public FirebaseAuthFactory() {
        this.mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public void register(String email, String password, OnCompleteListener<AuthResult> listener) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    public void login(String email, String password, OnCompleteListener<AuthResult> listener) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    public boolean isEmailVerified() {
        return getCurrentUser().isEmailVerified();
    }

    public void sendEmailVerification(OnCompleteListener<Void> listener) {
        getCurrentUser().sendEmailVerification().addOnCompleteListener(listener);
    }

    public void sendPasswordResetEmail(String email, OnCompleteListener<Void> listener) {
        mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(listener);
    }

    public void updatePassword(String newPassword, OnCompleteListener<Void> listener) {
        getCurrentUser().updatePassword(newPassword).addOnCompleteListener(listener);
    }

    public void updateProfile(String displayName, String photoUrl, OnCompleteListener<Void> listener) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(Uri.parse(photoUrl))
                .build();
        getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(listener);
    }


    public void reAuthenticate(String email, String password, OnCompleteListener<Void> listener) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);
        getCurrentUser().reauthenticate(credential).addOnCompleteListener(listener);
    }

    public void setLanguageCode(String code) {
        mFirebaseAuth.setLanguageCode(code);
    }

    public void delete(OnCompleteListener<Void> listener) {
        getCurrentUser().delete().addOnCompleteListener(listener);
    }

    public void logout() {
        mFirebaseAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return mFirebaseAuth.getCurrentUser();
    }
}
