package com.example.qrcg;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    WebView w;
    private BroadcastReceiver mNetworkReceiver; //Object of BroadcastReceiver
    private long downloadID;
    public static int REQUEST_PERMISSION=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        w=(WebView)findViewById(R.id.w);
        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setDomStorageEnabled(true);

        mNetworkReceiver = new NetworkChangeReceiver();
        //registerNetworkBroadcastForNougat();


        permission_fn(); //Method to check if user has allowed the permission required

        w.loadUrl("https://qrcode-avysh.herokuapp.com");
        w.setWebViewClient(new WebViewController());
        WebSettings ws = w.getSettings();
        ws.setJavaScriptEnabled(true);

        w.setDownloadListener(new DownloadListener() { //Loads the file from the hosted location
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    String root = Environment.getExternalStorageDirectory().toString();
                    File file = new File(root + "/new-folders");
                    if(!file.exists())
                    {
                        file.mkdirs();
                    }

                    if (url != null) {
                        String attachment = parseBase64(url);
                        byte[] byteArr = Base64.decode(attachment, Base64.DEFAULT);
                        File f = new File(file,"sample.jpg");
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(byteArr);
                        fo.close();
                        Toast.makeText(getApplicationContext(), "File downloading", Toast.LENGTH_SHORT).show();
                        beginDownload();

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    } //OnCreate Ends here

    /*private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }*/



    //---------------------------------------------------------------------------------------------------------------------------------------
   //Never called
  /* private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //System.out.println("Everrrrrrrrrrrrr");
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                String u="https://qrcode-avysh.herokuapp.com";
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(u));
                startActivity(i);
            }
        }
    };*/


    //----------------------------------------------------------------------------------------------------------------------------------------

    private String parseBase64(String url) {
        try {

            Pattern pattern = Pattern.compile("((?<=base64,).*\\s*)",Pattern.DOTALL|Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group().toString();
            } else {
                return "";
            }
        }
        catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    private void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------

    private void beginDownload(){

        String url="https://qrcode-avysh.herokuapp.com";
        File file=new File(getExternalFilesDir(null),"Dummy");
        /*
        Create a DownloadManager.Request with all the information necessary to start the download
         */
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url))
                .setTitle("Downloaded file")// Title of the Download Notification
                .setDescription("Downloading")// Description of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                //.setRequiresCharging(false)// Set if charging is required to begin the download
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true);// Set if download is allowed on roaming network
        DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
        registerReceiver(
                new NetworkChangeReceiver(),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        );

        try {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            Uri screenshotUri = Uri.parse(Environment.getExternalStorageDirectory() + "/new-folders/sample.jpg");
            sharingIntent.setType("image/jpg");

            sharingIntent.putExtra(Intent.EXTRA_EMAIL,"xyzr@gmail.com");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"AVYSH");
            sharingIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file://"+screenshotUri));
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        catch (Exception e)
        {
            System.out.println("Error");

        }

    }

    //-----------------------------------------------------------------------------------------------------------------------------------


    private void permission_fn()
    {
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        else
            {
                requestStoragePermission();
            }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------------

    private  void requestStoragePermission()
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("Permission is needed to save files in your device...")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Sorry, app doesn't work without the requested permission",Toast.LENGTH_LONG).show();
                            finishAndRemoveTask(); //Closes the app
                        }
                    }).create().show();
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for enabling the permission", Toast.LENGTH_SHORT).show();
            }

            else {
                boolean showRationale = shouldShowRequestPermissionRationale( Manifest.permission.WRITE_EXTERNAL_STORAGE );
                if (! showRationale) {
                    Toast.makeText(getApplicationContext(),"Sorry, app doesn't work without the requested permission \n Reinstall the app",Toast.LENGTH_LONG).show();
                    finishAndRemoveTask();
                    // user also CHECKED "never ask again"
                    // you can either enable some fall back,
                    // disable features of your app
                    // or open another dialog explaining
                    // again the permission and directing to
                    // the app setting
                }

                else {
                    Toast.makeText(this, "Please allow the Permission", Toast.LENGTH_SHORT).show();
                    permission_fn();
                }


            }
        }

    }

    //--------------------------------------------------------------------------------------------------------------------------------------------


    }//MainActivity Ends here




