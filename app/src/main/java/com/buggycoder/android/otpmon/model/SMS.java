package com.buggycoder.android.otpmon.model;

public class SMS {
    public final String sender;
    public final long receivedAt;
    public final int parts;
    private String mMessage;
    private String mOTP;

    public SMS(String sender, long receivedAt, int parts) {
        this.sender = sender;
        this.receivedAt = receivedAt;
        this.parts = parts;
    }

    public String appendMessage(String message) {
        if (message != null) {
            mMessage = (mMessage == null) ? message : mMessage.concat(message);
        }

        return mMessage;
    }

    public boolean hasMessage() {
        return (mMessage != null);
    }

    public String getMessage() {
        return mMessage;
    }

    public String getOTP() {
        return mOTP;
    }

    public void setOTP(String otp) {
        mOTP = otp;
    }
}
