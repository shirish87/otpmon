package com.buggycoder.android.otpmon.lib;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.buggycoder.android.otpmon.R;
import com.buggycoder.android.otpmon.model.SMS;

import rx.functions.Action1;
import timber.log.Timber;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;

public class NotifManager implements Action1<SMS> {

    public static final String ACTION_COPY = "com.buggycoder.android.otpmon.COPY_OTP";
    public static final int NOTIF_ID = 0;

    private final Context mContext;
    private final NotificationManager mNotificationManager;

    public interface CopyIntentExtras {
        String OTP = "otp";
        String MESSAGE = "message";
        String NOTIF_ID = "notifId";
    }

    public NotifManager(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }


    @Override
    public void call(SMS sms) {
        Timber.d("Posting Notif");

        String otp = sms.getOTP();
        String message = sms.getMessage();

        Intent dismissIntent = new Intent(ACTION_COPY);
        dismissIntent.putExtra(CopyIntentExtras.NOTIF_ID, NOTIF_ID);

        PendingIntent dIntent = PendingIntent.getBroadcast(mContext, 0, dismissIntent, FLAG_CANCEL_CURRENT);

        Intent intent = new Intent(ACTION_COPY);
        intent.putExtra(CopyIntentExtras.OTP, otp)
                .putExtra(CopyIntentExtras.MESSAGE, message)
                .putExtra(CopyIntentExtras.NOTIF_ID, NOTIF_ID);

        PendingIntent pIntent = PendingIntent.getBroadcast(mContext, 0, intent, FLAG_CANCEL_CURRENT);

        Notification n = new Notification.Builder(mContext)
                .setContentIntent(dIntent)
                .setContentTitle(mContext.getString(R.string.notif_title) + ": " + otp)
                .setContentText(message)
                .setStyle(new Notification.BigTextStyle().bigText(message))
                .setSmallIcon(R.drawable.ic_account_key_dark)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_content_copy_white_24dp,
                        mContext.getString(R.string.notif_action_copy), pIntent)
                .build();

        mNotificationManager.notify(NOTIF_ID, n);
    }
}
