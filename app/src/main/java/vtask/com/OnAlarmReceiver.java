package vtask.com;

/**
 * Created by akhil on 8/28/2016.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.util.Log;

public class OnAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = ComponentInfo.class.getCanonicalName();


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received wake up from alarm manager.");
        Log.d("OnAlarmReceiver", "Received wake up from alarm manager.");
        long rowid = intent.getExtras().getLong(ReminderDb.ID);
        WakeReminderIntentService.acquireStaticLock(context);
        Intent i = new Intent(context, ReminderService.class);
        i.putExtra(ReminderDb.ID, rowid);
        context.startService(i);
    }
}
