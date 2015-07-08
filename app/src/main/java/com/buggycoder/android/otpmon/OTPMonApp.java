package com.buggycoder.android.otpmon;

import android.app.Application;

import timber.log.Timber;

public class OTPMonApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("onCreate");
        Timber.plant(new Timber.DebugTree());
    }
}
