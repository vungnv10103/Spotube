package com.chagnahnn.spotube.service.firebase;

public class FirebaseFactory {
    public static FirebaseAuthFactory mFirebaseAuthFactory;

    public static void initService() {
        if (mFirebaseAuthFactory == null) {
            mFirebaseAuthFactory = new FirebaseAuthFactory();
        }
    }
}
