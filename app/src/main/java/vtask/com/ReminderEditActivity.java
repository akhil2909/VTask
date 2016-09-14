package vtask.com;

/**
 * Created by akhil on 8/28/2016.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


public class ReminderEditActivity extends Activity {

    //
    // Dialog Constants
    //
    private static final int DATE_PICKER_DIALOG = 0;
    private static final int TIME_PICKER_DIALOG = 1;

    //
    // Date Format
    //
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "kk:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";

    private EditText mTitleText;
    private EditText mBodyText;
    private TextView _date;
    private TextView _time;
    private TextView _repeat;
    private Button mConfirmButton;
    private Long mRowId;
    private ReminderDb mDbHelper;
    private Calendar myCalendar;
    final String[] items = {
            "Every day", "Every Week", "Every Month", "Every Year"
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new ReminderDb(this);

        setContentView(R.layout.reminder_edit);

        myCalendar = Calendar.getInstance();
        mTitleText = (EditText) findViewById(R.id.title);
        //  mBodyText = (EditText) findViewById(R.id.body);
        _date = (TextView) findViewById(R.id.reminder_date);
        _time = (TextView) findViewById(R.id.reminder_time);
        _repeat  = (TextView)findViewById(R.id.repeat);
        mConfirmButton = (Button) findViewById(R.id.confirm);

        //myCalendar = Calendar.getInstance();

        _repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ReminderEditActivity.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        _repeat.setText(items[item]);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        //initially save current date
        _date.setText(getCurrentDate());
        _time.setText(getCurrentTime());

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        _date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ReminderEditActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int min) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hours);
                myCalendar.set(Calendar.MINUTE, min);
                String myformat = "HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(myformat, Locale.ENGLISH);
                _time.setText(sdf.format(myCalendar.getTime()));

            }
        };

        _time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(ReminderEditActivity.this, time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask();
            }
        });


        mRowId = savedInstanceState != null ? savedInstanceState.getLong(RemindersDbAdapter.KEY_ROWID)
                : null;

        //registerButtonListenersAndSetDefaultText();
    }

    private void saveTask() {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat inputFormatter1 = new SimpleDateFormat("EEE, MMM d, yyyy");
            Date date1 = inputFormatter1.parse(_date.getText().toString());
            SimpleDateFormat outputFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
            String date = outputFormatter1.format(date1); //
            //String date = sdf.format(_date.getText().toString()).toString();
            String time = _time.getText().toString();
            ReminderItem ri = new ReminderItem();
            ri.setTitle(mTitleText.getText().toString());
            ri.setDateTime(date + ":" + time);
            ri.setDate(_date.getText().toString());
            ri.setTime(_time.getText().toString());
            ri.setRepeat(_repeat.getText().toString());
            mRowId = mDbHelper.insertEvent(ri);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd : HH:mm");
            Date theDate = null;
            theDate = simpleDateFormat.parse(date + " : " + time);
            myCalendar.setTime(theDate);
            new ReminderManager(this).setReminder(mRowId, myCalendar);
            Toast.makeText(this, "Task Added", Toast.LENGTH_LONG).show();
            Intent i = new Intent(ReminderEditActivity.this, ReminderListActivity.class);
            startActivity(i);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private void updateLabel() {

        String myFormat = "EEE, MMM d, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        _date.setText(sdf.format(myCalendar.getTime()));
    }

    public String getCurrentDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("EEE, MMM d, yyyy");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    private String getCurrentTime() {

        String myformat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myformat, Locale.ENGLISH);
        return sdf.format(myCalendar.getTime());

    }

    private void setRowIdFromIntent() {
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(RemindersDbAdapter.KEY_ROWID)
                    : null;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDbHelper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // mDbHelper.open();
        setRowIdFromIntent();
        // populateFields();
    }

   /*  @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case DATE_PICKER_DIALOG:
                return showDatePicker();
            case TIME_PICKER_DIALOG:
                return showTimePicker();
        }
        return super.onCreateDialog(id);
    }

    private DatePickerDialog showDatePicker() {


        DatePickerDialog datePicker = new DatePickerDialog(ReminderEditActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateButtonText();
            }
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        return datePicker;
    }

    private TimePickerDialog showTimePicker() {

        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                updateTimeButtonText();
            }
        }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);

        return timePicker;
    }

   private void registerButtonListenersAndSetDefaultText() {

        mDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_DIALOG);
            }
        });


        mTimeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(TIME_PICKER_DIALOG);
            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                saveState();
                setResult(RESULT_OK);
                Toast.makeText(ReminderEditActivity.this, getString(R.string.task_saved_message), Toast.LENGTH_SHORT).show();
                finish();
            }

        });

        updateDateButtonText();
        updateTimeButtonText();
    }

    private void populateFields()  {



        // Only populate the text boxes and change the calendar date
        // if the row is not null from the database.
        if (mRowId != null) {
            Cursor reminder = mDbHelper.fetchReminder(mRowId);
            startManagingCursor(reminder);
            mTitleText.setText(reminder.getString(
                    reminder.getColumnIndexOrThrow(RemindersDbAdapter.KEY_TITLE)));
            mBodyText.setText(reminder.getString(
                    reminder.getColumnIndexOrThrow(RemindersDbAdapter.KEY_BODY)));


            // Get the date from the database and format it for our use.
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
            Date date = null;
            try {
                String dateString = reminder.getString(reminder.getColumnIndexOrThrow(RemindersDbAdapter.KEY_DATE_TIME));
                date = dateTimeFormat.parse(dateString);
                myCalendar.setTime(date);
            } catch (ParseException e) {
                Log.e("ReminderEditActivity", e.getMessage(), e);
            }
        } else {
            // This is a new task - add defaults from preferences if set.
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String defaultTitleKey = getString(R.string.pref_task_title_key);
            String defaultTimeKey = getString(R.string.pref_default_time_from_now_key);

            String defaultTitle = prefs.getString(defaultTitleKey, null);
            String defaultTime = prefs.getString(defaultTimeKey, null);

            if(defaultTitle != null)
                mTitleText.setText(defaultTitle);

            if(defaultTime != null)
                myCalendar.add(Calendar.MINUTE, Integer.parseInt(defaultTime));

        }

        updateDateButtonText();
        updateTimeButtonText();

    }

    private void updateTimeButtonText() {
        // Set the time button text based upon the value from the database
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        String timeForButton = timeFormat.format(myCalendar.getTime());
       // mTimeButton.setText(timeForButton);
    }

    private void updateDateButtonText() {
        // Set the date button text based upon the value from the database
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String dateForButton = dateFormat.format(myCalendar.getTime());
       // mDateButton.setText(dateForButton);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(RemindersDbAdapter.KEY_ROWID, mRowId);
    }



    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        String reminderDateTime = dateTimeFormat.format(myCalendar.getTime());

        if (mRowId == null) {

            long id = mDbHelper.createReminder(title, body, reminderDateTime);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateReminder(mRowId, title, body, reminderDateTime);
        }

        new ReminderManager(this).setReminder(mRowId, myCalendar);
    }*/

}
