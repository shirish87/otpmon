package com.buggycoder.android.otpmon.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.buggycoder.android.otpmon.R;
import com.buggycoder.android.otpmon.model.SMS;
import com.buggycoder.android.otpmon.service.OTPMonService;

import rx.Subscriber;

public class MainActivity extends AppCompatActivity implements OTPMonService.SMSReceiveListener {

    private TextView mIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIntro = (TextView) findViewById(R.id.intro);
        OTPMonService.start(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(this, OTPMonService.class), mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    @Override
    public Subscriber<SMS> provideSMSSubscriber() {
        return new Subscriber<SMS>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(SMS sms) {
                if (sms.hasMessage() && mIntro != null) {
                    mIntro.setText(sms.getMessage());
                }
            }
        };
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        private OTPMonService.OTPServiceBinder mServiceBinder;

        public void onServiceConnected(ComponentName className, IBinder binder) {
            mServiceBinder = (OTPMonService.OTPServiceBinder) binder;
            mServiceBinder.bind(MainActivity.this);
        }

        public void onServiceDisconnected(ComponentName className) {
            if (mServiceBinder != null) {
                mServiceBinder.unbind();
            }
        }
    };
}
