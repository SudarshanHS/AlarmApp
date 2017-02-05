package com.sudarshanhs.myalaram;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sudarshanhs.myalaram.DataBase.AlarmDb;
import com.sudarshanhs.myalaram.Receiver.AlarmReceiver;
import com.sudarshanhs.myalaram.module.Alarm;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Button bSetAlarm;
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker tpAlarm;
    private  Button bAlarmDone,bRepeat;
    private RecyclerView recyclerView;
    static  final String shredPref="MyPref";
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private static  final String TAG="mainactivity";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            pref = getApplicationContext().getSharedPreferences(shredPref, MODE_PRIVATE);
            editor= pref.edit();

            editor.putString("repeat","Once");
            editor.commit();


            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialogToSetAlarm();
                }
            });


            recyclerView = (RecyclerView) findViewById(R.id.rvAlarmList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            bSetAlarm = (Button) findViewById(R.id.bSetAlarm);
            bSetAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogToSetAlarm();
                }
            });

            callLoaderClass();

        }catch (Exception e)
        {
            Log.d(TAG, "Exception in onCreate  >>"+Log.getStackTraceString(e));
        }

    }


    public  void showDialogToSetAlarm()
    {
        try {
            final Dialog dialog=new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.new_alarm_dialog);
            dialog.show();

            tpAlarm = (TimePicker)dialog.findViewById(R.id.tpAlarm);
            bAlarmDone= (Button)dialog.findViewById(R.id.bDoneAlarm);
            bRepeat= (Button)dialog.findViewById(R.id.bRepeat);

            bAlarmDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, tpAlarm.getCurrentHour());
                    calendar.set(Calendar.MINUTE, tpAlarm.getCurrentMinute());


                    String days=pref.getString("repeat","Once");

                    long id=0;
                    try {
                        AlarmDb entry = new AlarmDb(MainActivity.this);
                        entry.open();
                        id= entry.createEntry("" + tpAlarm.getCurrentHour(), "" + tpAlarm.getCurrentMinute(), days, "On");
                        entry.close();
                    }catch (Exception e)
                    {
                        Log.d(TAG,"Exception in inserting to db >>>"+Log.getStackTraceString(e));
                    }

                    Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(MainActivity.this, (int)id, myIntent, 0);

                    if(days.contains("Once"))
                    {
                        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
                    }else{
                       alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                    }


                    dialog.dismiss();
                    callLoaderClass();
                }
            });


            bRepeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listRepeatDays();
                }
            });


        }catch (Exception e) {
            Log.d(TAG,"Exception in showDialogToSetAlarm() >>"+Log.getStackTraceString(e));
        }
    }

    public void cancelAlarm(Context context, String pk)
    {
        try {

            int id = Integer.parseInt(pk);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_NO_CREATE);
            manager.cancel(pendingIntent);
        }catch (Exception e)
        {
            Log.d(TAG,"Exception   in cancelAlarm()  >>"+Log.getStackTraceString(e));
        }
    }


    public  void deleteAlarm(String pk)
    {
        try {
            int id = Integer.parseInt(pk);
            long rowId = id;

            AlarmDb alarmDb = new AlarmDb(MainActivity.this);
            alarmDb.open();
            alarmDb.deleteEntry(rowId);
            alarmDb.close();
        }catch (Exception e)
        {
            Log.d(TAG,"Exception   >>>"+Log.getStackTraceString(e));
        }
    }

    public void listRepeatDays()
    {
        try {
           final List<String> daysList=addDays();
            final String daysArr[]=new String[daysList.size()];
            for (int i=0;i<daysList.size();i++)
            {
                daysArr[i]=daysList.get(i);
            }

            AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
            build.setTitle("Choose");
            build.setSingleChoiceItems(daysArr, 0, null);

            build.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    String days = daysList.get(selectedPosition);

                        editor.putString("repeat",days);
                        editor.commit();
                }
            });

             AlertDialog dil = build.create();
            dil.show();

        }catch (Exception e){
            Log.d(TAG,"Exception in    downloadUsingManger()    >>"+Log.getStackTraceString(e));
        }
    }

    public   List<String> addDays()
    {
        List<String> daysList=new ArrayList<String>();
        daysList.add("Once");
        daysList.add("Daily");
        daysList.add("Sunday");
        daysList.add("Monday");
        daysList.add("Tuesday");
        daysList.add("Wednesday");
        daysList.add("Thursday");
        daysList.add("Friday");
        daysList.add("Saturday");
        return  daysList;
    }

    private class  LoadAlarmAsy extends AsyncTask<Void,Void,List<Alarm>>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Alarm> doInBackground(Void... params)
        {
            List<Alarm> alarmList=new ArrayList<Alarm>();
            try{
                AlarmDb info = new AlarmDb(MainActivity.this);
                info.open();
                alarmList=info.getData();
                Collections.reverse(alarmList);
                info.close();
            }catch (Exception e)
            {
                Log.d(TAG,"Exception    >>>"+Log.getStackTraceString(e));
            }
            return alarmList;
        }

        @Override
        protected void onPostExecute(List<Alarm> alarms)
        {
            super.onPostExecute(alarms);

                if(alarms.size()==0)
                {
                    recyclerView.setAdapter(null);
                }else
                {
                    AlarmAdapter aa=new AlarmAdapter(alarms);
                    recyclerView.setAdapter(aa);
                }
        }
    }

    public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.MyViewHolder> {

        private List<Alarm> alarmList;
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView tvTitle, tvTime, tvState;

            public CardView cvAlarm;

            public MyViewHolder(View view) {
                super(view);
                tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                tvTime = (TextView) view.findViewById(R.id.tvTime);
                tvState = (TextView) view.findViewById(R.id.tvState);
                cvAlarm=(CardView)view.findViewById(R.id.cvAlarm);
            }
        }

        public AlarmAdapter(List<Alarm> alarmlist) {
            this.alarmList = alarmlist;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.alarm_list_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final Alarm alarm = alarmList.get(position);
            holder.tvTitle.setText("Alarm "+alarm.getId());
            holder.tvTime.setText(""+alarm.getTimeHr()+":"+ alarm.getTimeMin());
            holder.tvState.setText(alarm.getRepeat());


            holder.cvAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlarm(alarm);
                }
            });
        }

        @Override
        public int getItemCount() {
            return alarmList.size();
        }
    }


    public  void callLoaderClass() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            new LoadAlarmAsy().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            new LoadAlarmAsy().execute();
        }
    }

    public  void showAlarm(final Alarm alarm)
    {
        try {
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.show_alarm_dialog);
            dialog.show();

            TextView tvTitleDialog=(TextView)dialog.findViewById(R.id.tvTitleDialog);
            TextView  tvTimeDialog=(TextView)dialog.findViewById(R.id.tvTimeDialog);
            TextView   tvStateDialog=(TextView)dialog.findViewById(R.id.tvStateDialog);

            tvTitleDialog.setText("Alarm "+alarm.getId());
            tvTimeDialog.setText(alarm.getTimeHr()+":"+alarm.getTimeMin());
            tvStateDialog.setText(alarm.getRepeat());

            Button bExitDialog=(Button)dialog.findViewById(R.id.bExitAlarmDialog);
            Button bDeleteAlarm=(Button)dialog.findViewById(R.id.bDeleteAlarm);

            bExitDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    callLoaderClass();
                }
            });

            bDeleteAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAlarm(alarm.getId());
                    cancelAlarm(MainActivity.this, alarm.getId());
                    dialog.dismiss();
                    callLoaderClass();
                }
            });

            Log.d(TAG, "alarm.getState()   >>>" + alarm.getState());

            dialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                    {
                        dialog.dismiss();
                        callLoaderClass();
                    }
                    return true;
                }
            });



        }catch (Exception e)
        {
            Log.getStackTraceString(e);
        }

    }


    }
