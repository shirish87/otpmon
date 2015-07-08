package com.buggycoder.android.otpmon.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.buggycoder.android.otpmon.service.OTPMonService;

public class NewSMSBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        OTPMonService.start(context, intent);
    }
}
