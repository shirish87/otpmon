package com.buggycoder.android.otpmon.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.buggycoder.android.otpmon.service.OTPMonService;
import com.buggycoder.android.otpmon.service.OTPMonService.Actions;

public class BootBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        OTPMonService.start(context, Actions.BOOT);
    }
}
