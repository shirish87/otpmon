package com.buggycoder.android.otpmon;

import android.app.Application;

import timber.log.Timber;

public class OTPMonApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Timber.plant(new Timber.DebugTree());
    }
}
