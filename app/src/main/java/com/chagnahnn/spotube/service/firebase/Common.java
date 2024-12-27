package com.chagnahnn.spotube.service.firebase;

public class Common {
    public static FirebaseAuthFactory mFirebaseAuthFactory;

    public static void initFirebaseService() {
        if (mFirebaseAuthFactory == null) {
            mFirebaseAuthFactory = new FirebaseAuthFactory();
        }
    }
}
