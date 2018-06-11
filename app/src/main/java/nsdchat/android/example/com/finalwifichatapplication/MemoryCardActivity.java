package nsdchat.android.example.com.finalwifichatapplication;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MemoryCardActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String[] READ_AND_WRITE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int RC_READ_AND_WRITE = 123;
    ;

    private static final String TAG = "MemoryCardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_card);

        methodRequiresTwoPermission();

    }


    @AfterPermissionGranted(RC_READ_AND_WRITE)
    private void methodRequiresTwoPermission() {
        if (EasyPermissions.hasPermissions(this, READ_AND_WRITE)) {
            // Already have permission, do the thing
            // ...
            getAllFiles();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.camera_and_location_rationale),
                    RC_READ_AND_WRITE, READ_AND_WRITE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Toast.makeText(this, "Result Called", Toast.LENGTH_SHORT).show();

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(this, R.string.returned_from_app_settings_to_activity, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        Toast.makeText(this, "onPermissionsGranted", Toast.LENGTH_SHORT).show();
        getAllFiles();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        Toast.makeText(this, "onPermissionsDenied", Toast.LENGTH_SHORT).show();

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void getAllFiles() {

//        String path = Environment.getExternalStorageDirectory().toString();
        String path = Environment.getExternalStorageDirectory().toString() + "/vrvideos";

        ArrayList<String> alPath = new ArrayList<String>();
        ArrayList<String> alName = new ArrayList<String>();

        File directory = new File(path);
        File[] file = directory.listFiles();
        for (int i = 0; i < file.length; i++) {

            Log.e(TAG, "getAllFiles: " + file[i].getName() + "   " + file[i].getAbsolutePath());

        }
    }

}
