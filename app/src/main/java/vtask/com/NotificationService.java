package vtask.com;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

/**
 * Created by akhil on 9/6/2016.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {


    private String TAG = this.getClass().getSimpleName();
    private NLServiceReceiver nlservicereciver;
    MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MSG-->","Me at onCreate");
        myApplication = MyApplication.getInstance();
        nlservicereciver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("vtask.com.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlservicereciver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlservicereciver);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if(sbn.getPackageName().equals("vtask.com")) {
            Log.d("MSG-->","Me at onNotificationPosted");
            Log.d(TAG, "**********  onNotificationPosted");
          //  MyApplication.saveToPreferences(MyApplication.getAppContext(), "package_name", sbn.getPackageName());
            MyApplication.saveToPreferences(MyApplication.getAppContext(), "notify_title", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString());
         //   MyApplication.saveToPreferences(MyApplication.getAppContext(), "package_name", sbn.getPackageName());
            Intent i = new Intent("vtask.com.NOTIFICATION_LISTENER_EXAMPLE");
            i.putExtra("notification_event", "onNotificationPosted :" + sbn.getPackageName() + "n");
            i.putExtra("package_name", sbn.getPackageName());
            i.putExtra("notification_title", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString());
            sendBroadcast(i);
        }


    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

  /*      Log.i(TAG, "********** onNOtificationRemoved");
        Log.i(TAG, "ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "t" + sbn.getPackageName());
        Intent i = new Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event", "onNotificationRemoved :" + sbn.getPackageName() + "n");
        sendBroadcast(i);*/
    }


    class NLServiceReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("command").equals("clearall")) {
                Log.d("MSG-->","Me at NLServiceReceiver if cmd clearall");
                NotificationService.this.cancelAllNotifications();
            } else if (intent.getStringExtra("command").equals("list")) {
                Log.d("MSG-->","Me at NLServiceReceiver if cmd list");
                Intent i1 = new Intent("vtask.com.NOTIFICATION_LISTENER_EXAMPLE");
                i1.putExtra("notification_event", "=====================");
                sendBroadcast(i1);
                int i = 1;
                for (StatusBarNotification sbn : NotificationService.this.getActiveNotifications()) {
                    Intent i2 = new Intent("vtask.com.NOTIFICATION_LISTENER_EXAMPLE");
                    i2.putExtra("notification_event", i + " " + sbn.getPackageName() + "n");
                    i2.putExtra("notification_title", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString());
                    i2.putExtra("package_name",sbn.getPackageName());
                    sendBroadcast(i2);
                    i++;
                }
                Intent i3 = new Intent("vtask.com.NOTIFICATION_LISTENER_EXAMPLE");
                i3.putExtra("notification_event", "===== Notification List ====");
                sendBroadcast(i3);

            }

        }
    }
}



