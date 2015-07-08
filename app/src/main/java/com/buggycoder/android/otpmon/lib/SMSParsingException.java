package com.buggycoder.android.otpmon.lib;

public class SMSParsingException extends Exception {


    public SMSParsingException() {
    }

    public SMSParsingException(String detailMessage) {
        super(detailMessage);
    }

    public SMSParsingException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public SMSParsingException(Throwable throwable) {
        super(throwable);
    }
}
