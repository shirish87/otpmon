package com.buggycoder.android.otpmon.lib;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.buggycoder.android.otpmon.model.SMS;

public class SMSParserUtils {

    public static SMS parseSMSIntent(Intent intent) throws SMSParsingException {
        if (intent == null) {
            throw new SMSParsingException("empty");
        }

        Bundle bundle = intent.getExtras();
        if (!bundle.containsKey(SMSIntent.FIELD_PDUS)) {
            throw new SMSParsingException("Unexpected SMS format. Missing field: " + SMSIntent.FIELD_PDUS);
        }

        Object pduData = bundle.get(SMSIntent.FIELD_PDUS);
        if (!(pduData instanceof Object[])) {
            throw new SMSParsingException("Unexpected SMS format.");
        }

        Object[] pdus = (Object[]) pduData;
        int parts = pdus.length;

        if (parts == 0) {
            throw new SMSParsingException("Unexpected SMS format. Empty.");
        }

        SMS receivedSMS = null;

        for (Object pdu : pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            String msg = smsMessage.getDisplayMessageBody();

            if (!smsMessage.isStatusReportMessage() && msg != null && !msg.isEmpty()) {

                if (receivedSMS == null) {
                    long receivedAt = smsMessage.getTimestampMillis();

                    if (receivedAt < 1) {
                        // if the message doesn't contain a valid timestamp,
                        // we'll assume it's just arrived
                        receivedAt = System.currentTimeMillis();
                    }

                    String sender = smsMessage.getDisplayOriginatingAddress();
                    if (sender != null) {
                        receivedSMS = new SMS(sender, receivedAt, parts);
                    }
                }

                if (receivedSMS != null) {
                    // concat SMS bodies
                    receivedSMS.appendMessage(msg);
                }
            }
        }

        if (receivedSMS == null) {
            throw new SMSParsingException("Failed to process received SMS");
        }

        return receivedSMS;
    }
}

