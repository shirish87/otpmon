package com.buggycoder.android.otpmon.service;

import android.content.Context;

import com.buggycoder.android.otpmon.R;
import com.buggycoder.android.otpmon.model.SMS;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class ICICIOTPProvider extends OTPProvider {

    private String mOTPSender, mOTPMessageContains;
    private Pattern mOTPPattern;

    protected ICICIOTPProvider(Context context) {
        super(context);

        mOTPPattern = Pattern.compile(context.getString(R.string.icici_otp_regex));
        mOTPSender = context.getString(R.string.icici_sender).toLowerCase();
        mOTPMessageContains = context.getString(R.string.icici_body_contains).toLowerCase();
    }

    @Override
    public String extractOTP(SMS sms) {
        if (sms != null &&
                sms.sender.toLowerCase().equals(mOTPSender) &&
                sms.hasMessage() &&
                sms.getMessage().toLowerCase().contains(mOTPMessageContains)) {
            Timber.d("Matched basic criteria");

            Matcher matcher = mOTPPattern.matcher(sms.getMessage());
            if (matcher.find()) {
                Timber.d("Matched all criteria");
                return matcher.group();
            }
        }

        return null;
    }
}
