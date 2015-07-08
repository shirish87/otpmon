package com.buggycoder.android.otpmon.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.buggycoder.android.otpmon.R;
import com.buggycoder.android.otpmon.lib.NotifManager;
import com.buggycoder.android.otpmon.lib.NotifManager.CopyIntentExtras;

import static android.content.Context.CLIPBOARD_SERVICE;

public class CopyOTPBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(CopyIntentExtras.NOTIF_ID)) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(intent.getIntExtra(CopyIntentExtras.NOTIF_ID, NotifManager.NOTIF_ID));
        }

        if (intent.hasExtra(CopyIntentExtras.OTP)) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);

            clipboard.setPrimaryClip(ClipData.newPlainText(context.getString(R.string.clip_title),
                    intent.getStringExtra(CopyIntentExtras.OTP)));

            Toast.makeText(context, context.getString(R.string.clip_toast_msg),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
