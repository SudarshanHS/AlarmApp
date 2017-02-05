package com.sudarshanhs.myalaram.Receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.sudarshanhs.myalaram.Service.BootService;

/**
 * Created by Sudarshan on 18-12-2016.
 */
public class BootReceiver extends BroadcastReceiver {

    static final String TAG="bootreciver";
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Intent i = new Intent(context, BootService.class);
            ComponentName service = context.startService(i);

            if (null == service) {
                Log.d(TAG, "Could not start service ");
                Toast.makeText(context, "Could not start service ", Toast.LENGTH_LONG);
            } else {
                Log.d(TAG, "Successfully started service ");
                Toast.makeText(context, "Successfully started service ", Toast.LENGTH_LONG);
            }
        }catch (Exception e)
        {
            Log.d(TAG,"Exception   >>>"+Log.getStackTraceString(e));
        }
    }

}
