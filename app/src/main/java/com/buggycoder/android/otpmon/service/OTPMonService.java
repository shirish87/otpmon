package com.buggycoder.android.otpmon.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;

import com.buggycoder.android.otpmon.lib.NotifManager;
import com.buggycoder.android.otpmon.lib.SMSParserUtils;
import com.buggycoder.android.otpmon.model.SMS;
import com.buggycoder.android.otpmon.receiver.BootBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

public class OTPMonService extends Service {
    private static final String TAG = OTPMonService.class.getSimpleName();
    private final IBinder mBinder = new OTPServiceBinder();

    private Subject<Intent, Intent> mSourceSubject;
    private Observable<SMS> mSourceObservable;
    private Subscription mUISubscription;
    private CompositeSubscription mSubscriptions;

    private List<OTPProvider> mOTPProviders;

    private Context mAppContext;

    public interface Actions {
        String BOOT = "boot";
        String START = "start";
        String NEW_SMS = "newSMS";
    }

    public interface Extras {
        String NEW_SMS_INTENT = "newSMSIntent";
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAppContext = getApplicationContext();
        mOTPProviders = new ArrayList<>();
        mOTPProviders.add(new ICICIOTPProvider(mAppContext));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unsubscribeAll();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = (intent != null) ? intent.getAction() : Actions.START;

        if (action != null) {
            switch (action) {
                case Actions.BOOT:
                    init();
                    BootBroadcastReceiver.completeWakefulIntent(intent);
                    break;

                case Actions.START:
                    init();
                    break;

                case Actions.NEW_SMS:
                    if (intent.hasExtra(Extras.NEW_SMS_INTENT)) {
                        Parcelable data = intent.getParcelableExtra(Extras.NEW_SMS_INTENT);
                        if (data instanceof Intent) {
                            Intent smsIntent = (Intent) data;

                            if (mSourceSubject != null) {
                                mSourceSubject.onNext(smsIntent);
                            } else {
                                // While this service was dead
                                // an SMS was received by the NewSMSBroadcastReceiver
                                mSourceSubject = BehaviorSubject.create(smsIntent);
                                init();
                            }
                        }
                    }

                    break;
            }
        }

        return START_STICKY;
    }


    private void init() {
        if (mSourceSubject == null) {
            mSourceSubject = PublishSubject.create();
        }

        mSourceObservable = mSourceSubject.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Intent, SMS>() {
                    @Override
                    public SMS call(Intent intent) {
                        try {
                            return SMSParserUtils.parseSMSIntent(intent);
                        } catch (Exception e) {
                            throw OnErrorThrowable.from(e);
                        }
                    }
                }).filter(new Func1<SMS, Boolean>() {
                    @Override
                    public Boolean call(SMS sms) {
                        for (OTPProvider provider : mOTPProviders) {
                            String otp = provider.extractOTP(sms);

                            if (otp != null) {
                                sms.setOTP(otp);
                                return true;
                            }
                        }

                        return false;
                    }
                }).share();

        mSubscriptions = new CompositeSubscription();
        mSubscriptions.add(mSourceObservable.subscribe(new NotifManager(mAppContext)));
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private boolean subscribe(Subscriber<SMS> subscriber) {
        if (mSourceObservable != null) {
            unsubscribe();
            mUISubscription = mSourceObservable.subscribe(subscriber);
            mSubscriptions.add(mUISubscription);
            return true;
        }

        return false;
    }

    private boolean unsubscribe() {
        if (mUISubscription != null && !mUISubscription.isUnsubscribed()) {
            mUISubscription.unsubscribe();
            mSubscriptions.remove(mUISubscription);
            return true;
        }

        return false;
    }

    private void unsubscribeAll() {
        mSubscriptions.unsubscribe();
    }


    public class OTPServiceBinder extends Binder {
        public boolean bind(SMSReceiveListener listener) {
            if (listener == null) {
                return false;
            }

            Subscriber<SMS> subscriber = listener.provideSMSSubscriber();
            return subscriber != null && OTPMonService.this.subscribe(subscriber);
        }

        public boolean unbind() {
            return unsubscribe();
        }
    }

    public interface SMSReceiveListener {
        Subscriber<SMS> provideSMSSubscriber();
    }


    public static void start(Context context) {
        start(context, Actions.START);
    }

    public static void start(Context context, String action) {
        Intent intent = new Intent(context, OTPMonService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    public static void start(Context context, Intent smsIntent) {
        Intent intent = new Intent(context, OTPMonService.class);
        if (smsIntent != null) {
            intent.setAction(Actions.NEW_SMS);
            intent.putExtra(Extras.NEW_SMS_INTENT, smsIntent);
        } else {
            intent.setAction(Actions.START);
        }

        context.startService(intent);
    }

}
