package nsdchat.android.example.com.finalwifichatapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by yasar on 28/10/17.
 */

public class RuntimePermissionRequest {

    public static boolean checkSinglePermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
        } else {
            return false;
        }
    }

    public static boolean checkMultiplePermission(Context context, String[] permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            for (int i = 0; i < permission.length; i++) {
                if (ContextCompat.checkSelfPermission(context, permission[i]) != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public static void requestPermissionSingle(final Context context, final String permission, final int requestCode, final String title, final String message) {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                permission)) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{permission},
                                    requestCode);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{permission},
                    requestCode);
        }


    }

    public static void requestPermissionMultiple(final Context context, final String[] permission, final int requestCode, final String title, final String message) {


        int count = 0;
        for (int i = 0; i < permission.length; i++) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    permission[i])) {
                count++;
            }
        }
        if (count > 0) {

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            ActivityCompat.requestPermissions((Activity) context,
                                    permission,
                                    requestCode);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            ActivityCompat.requestPermissions((Activity) context,
                                    permission,
                                    requestCode);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();


        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    permission,
                    requestCode);

        }


    }

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
