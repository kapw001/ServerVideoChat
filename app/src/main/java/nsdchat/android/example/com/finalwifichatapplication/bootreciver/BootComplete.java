package nsdchat.android.example.com.finalwifichatapplication.bootreciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import nsdchat.android.example.com.finalwifichatapplication.SplashScreenActivity;
import nsdchat.android.example.com.finalwifichatapplication.StudentActivity;

/**
 * Created by yasar on 27/1/18.
 */

public class BootComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, SplashScreenActivity.class);
            context.startActivity(serviceIntent);

            Toast.makeText(context, "Boot Started Student", Toast.LENGTH_SHORT).show();
        }
    }

}


