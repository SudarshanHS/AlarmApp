package com.sudarshanhs.myalaram.Receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.sudarshanhs.myalaram.DataBase.AlarmDb;
import com.sudarshanhs.myalaram.MainActivity;
import com.sudarshanhs.myalaram.R;
import com.sudarshanhs.myalaram.module.Alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Sudarshan on 18-12-2016.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    private static final String TAG="alarmreciver";

    NotificationCompat.Builder notification;
    NotificationManager manager;
    @Override
    public void onReceive(final Context context, Intent intent)
    {

        try {

            List<Alarm> alarmList=new ArrayList<Alarm>();
            try{

                AlarmDb info = new AlarmDb(context);
                info.open();
                alarmList=info.getData();
                Collections.reverse(alarmList);
                info.close();

                Date d = new Date();
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                String today = null;
                if (day == 2) {
                    today = "Monday";
                } else if (day == 3) {
                    today = "Tuesday";
                } else if (day == 4) {
                    today = "Wednesday";
                } else if (day == 5) {
                    today = "Thursday";
                } else if (day == 6) {
                    today = "Friday";
                } else if (day == 7) {
                    today = "Saturday";
                } else if (day == 1) {
                    today = "Sunday";
                }

                int system_hour = d.getHours();
                int system_minute = d.getMinutes();
                String now= system_hour + ":" + system_minute;

                for(int i=0;i<alarmList.size();i++)
                {
                    Log.d(TAG,"id  in reciver   >>>"+alarmList.get(i).getId());

                    Alarm alarm= alarmList.get(i);
                    String hr=alarm.getTimeHr();
                    String min=alarm.getTimeMin();
                    String time=hr+":"+min;
                    String repeat=alarm.getRepeat();

                    Log.d(TAG,"hr >>"+hr+"\n min>>"+min+"\n time>>"+time+"\n repeat>>"+repeat+"\n today >>"+today+"\n now>>"+now);

                    if(repeat.equals("Once") || repeat.equals("Daily"))
                    {
                        Log.d(TAG, "inside daily, once");
                        if (repeat.equals("Once")) {
                            long id = Long.parseLong(alarm.getId());
                            AlarmDb alarmDb = new AlarmDb(context);
                            alarmDb.open();
                            alarmDb.updateState(id, "Done");
                            alarmDb.close();
                        }
                        announce(context);
                    }else if(time.equals(now) &&repeat.equals(today))
                    {
                        Log.d(TAG,"inside weakly  >>"+today);
                        announce(context);
                    }


                }


            }catch (Exception e)
            {
                Log.d(TAG,"Exception    >>>"+Log.getStackTraceString(e));
            }

        }catch (Exception e)
        {
            Log.d(TAG,"Exception   >>>"+ Log.getStackTraceString(e));
        }

    }

    public  void announce(Context context)
    {
        try {
            PowerManager pm;
            PowerManager.WakeLock wl;
            pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            wl.acquire();

            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(1500);

            final MediaPlayer alaramTone = MediaPlayer.create(context,
                    R.raw.alarm);
            alaramTone.start();

            startNotification(context);
        }catch (Exception e)
        {
            Log.d(TAG,"Exception   >>>"+Log.getStackTraceString(e));
        }

    }


    protected void startNotification(Context context) {

        try{
        // TODO Auto-generated method stub
        notification = new NotificationCompat.Builder(context);
        notification.setContentTitle("Alarm");
        notification.setContentText("New Alarm.");
        notification.setSmallIcon(R.mipmap.ic_launcher);

            Intent notificationIntent = new Intent(context, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(contentIntent);

        manager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification.build());




        }catch (Exception e)
        {
            Log.d(TAG,"Exception   >>>"+Log.getStackTraceString(e));
        }

    }
}