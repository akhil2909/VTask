package vtask.com;

/**
 * Created by akhil on 8/28/2016.
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.database.Cursor;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {

    private static final String TAG = ComponentInfo.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {

        ReminderManager reminderMgr = new ReminderManager(context);

        ReminderDb rdb = new ReminderDb(context);
        List<ReminderItem> rList = rdb.getAllEvents();
        for (ReminderItem rItem : rList) {

            Log.d(TAG, "Adding alarm from boot.");
            // Log.d(TAG, "Row Id Column Index - " + rowIdColumnIndex);
            // Log.d(TAG, "Date Time Column Index - " + dateTimeColumnIndex);

            Long rowId = Long.valueOf(rItem.getId());
            String dateTime = rItem.getDateTime();


            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat(ReminderEditActivity.DATE_TIME_FORMAT);

            try {
                java.util.Date date = format.parse(dateTime);
                cal.setTime(date);

                reminderMgr.setReminder(rowId, cal);
            } catch (java.text.ParseException e) {
                Log.e("OnBootReceiver", e.getMessage(), e);
            }

        }

    }
}
