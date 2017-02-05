package com.sudarshanhs.myalaram.Service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.sudarshanhs.myalaram.DataBase.AlarmDb;
import com.sudarshanhs.myalaram.Receiver.AlarmReceiver;
import com.sudarshanhs.myalaram.module.Alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sudarshan on 18-12-2016.
 */


public class BootService extends IntentService {
    static final String TAG="bootservice";
    public BootService() {
        super("BootService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        List<Alarm> alarmList=new ArrayList<Alarm>();
        try {

            AlarmDb info = new AlarmDb(getApplicationContext());
            info.open();
            alarmList = info.getData();
            Collections.reverse(alarmList);
            info.close();


            for (int i=0;i<alarmList.size();i++)
            {
                Alarm alarm=alarmList.get(i);


                if(alarm.getState().equals("Done")||alarm.getState().equals("Off"))
                {
                    // do nothing, Don't add once again
                }else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarm.getTimeHr()));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(alarm.getTimeMin()));


                    String days = alarm.getRepeat();

                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    PendingIntent pendingIntent;
                    Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(alarm.getId()), myIntent, 0);

                    if (days.contains("Once")) {
                        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                    }
                }

            }

        }catch (Exception e)
        {
            Log.d(TAG,"Exception   >>>"+ Log.getStackTraceString(e));
        }
    }
}
