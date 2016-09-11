package vtask.com;

import android.app.Activity;

/**
 * Created by akhil on 8/28/2016.
 */


import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ReminderListActivity extends AppCompatActivity {


    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    private ReminderDb mDbHelper;

    private List<ReminderItem> movieList;
    private RecyclerView recyclerView;
    private ReminderAdapter mAdapter;

    TextView noShow;

    private NotificationReceiver nReceiver;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noShow = (TextView) findViewById(R.id.empty);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mDbHelper = new ReminderDb(this);
        movieList = mDbHelper.getAllEvents();
        if (movieList.isEmpty()) {

            noShow.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noShow.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            mAdapter = new ReminderAdapter(movieList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);

            mDbHelper.getAllEvents();
            mAdapter.notifyDataSetChanged();

        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add Task", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(ReminderListActivity.this, ReminderEditActivity.class);
                startActivity(intent);
            }
        });

        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("vtask.com.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver, filter);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ReminderList Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    class NotificationReceiver extends BroadcastReceiver implements TextToSpeech.OnInitListener {

        private TextToSpeech tts;

        @Override
        public void onReceive(Context context, Intent intent) {
            String package_name = intent.getStringExtra("package_name") + "n";
            String packager_name = intent.getStringExtra("package_name");
            System.out.println(packager_name);
            System.out.println(MyApplication.readFromPreferences(MyApplication.getAppContext(), "notify_title", "You have Reminder"));
            Toast.makeText(ReminderListActivity.this, intent.getStringExtra("notification_title"), Toast.LENGTH_LONG).show();
            speakOut();
        }

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {

                int result = tts.setLanguage(Locale.US);

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                } else {
                    // btnSpeak.setEnabled(true);
                    speakOut();
                }

            } else {
                Log.e("TTS", "Initilization Failed!");
            }
        }


        private void speakOut() {
            tts.speak(MyApplication.readFromPreferences(ReminderListActivity.this, "notify_title", "hello").toString(), TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

/*
    private void createReminder() {
        Intent i = new Intent(this, ReminderEditActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, ReminderEditActivity.class);
        i.putExtra(RemindersDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }*/
}
