package vtask.com;

/**
 * Created by akhil on 8/28/2016.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

public class ReminderService extends WakeReminderIntentService {

    public ReminderService() {
        super("ReminderService");
    }

    @Override
    void doReminderWork(Intent intent) {
        Log.d("ReminderService", "Doing work.");

        Long rowId = intent.getExtras().getLong(ReminderDb.ID);

        ReminderDb reminderDb = new ReminderDb(this);
        ReminderItem ri = reminderDb.getEvent(rowId);

        NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, ReminderEditActivity.class);
        notificationIntent.putExtra(ReminderDb.ID, rowId);

        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder builder = new Notification.Builder(ReminderService.this);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(ri.getTitle())
                .setContentText("Your notification content here.")
                .setContentIntent(pi);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = builder.getNotification();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(android.R.drawable.stat_sys_warning, notification);
    }
}
