package com.example.qrcg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import static androidx.core.content.ContextCompat.startActivity;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!checkInternet(context)) {
            showCustomDialog(context);
            Toast.makeText(context, "Network Not Available To Do operations", Toast.LENGTH_LONG).show();

        }

    }

    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) {
            return true;
        } else {
            return false;
        }
    }
    public void showCustomDialog(final Context context) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage("Please Connect to the internet to proceed further")
                .setCancelable(false)
                .setPositiveButton("connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                        if (!checkInternet(context)) {
                            showCustomDialog(context);
                            Toast.makeText(context, "Network Not Available To Do operations", Toast.LENGTH_LONG).show();

                        }

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context,"Sorry, app doesn't work without Internet",Toast.LENGTH_LONG).show();
                        showCustomDialog(context);
                    }
                });
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("No Internet");
        alert.show();

    }

}




