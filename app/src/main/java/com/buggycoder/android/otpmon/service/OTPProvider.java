package com.buggycoder.android.otpmon.service;

import android.content.Context;

import com.buggycoder.android.otpmon.model.SMS;

public abstract class OTPProvider {
    protected final Context mContext;


    protected OTPProvider(Context context) {
        mContext = context;
    }

    public abstract String extractOTP(SMS sms);
}
