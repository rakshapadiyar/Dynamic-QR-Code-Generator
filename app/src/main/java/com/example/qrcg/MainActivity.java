package com.example.qrcg;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    WebView w;
    private long downloadID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        w=(WebView)findViewById(R.id.w);
        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setDomStorageEnabled(true);


        w.loadUrl("https://qrcode-avysh.herokuapp.com");


        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
        {

            Toast.makeText(MainActivity.this, "Please allow permissions", Toast.LENGTH_LONG).show();
            Log.d("permission", "permission denied to WRITE_EXTERNAL_STORAGE - requesting it");
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, 1);

        }


        w.setWebViewClient(new WebViewController());
        WebSettings ws = w.getSettings();
        ws.setJavaScriptEnabled(true);
        w.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    String root = Environment.getExternalStorageDirectory().toString();
                    System.out.println("Meri dholna sunn " +root);

                    System.out.println("              SHELDON LEE COOPER 7\n");

                    File file = new File(root + "/new-folders");
                    if(!file.exists())
                    {
                        file.mkdirs();
                    }
                    if (url != null) {
                        String attachment = parseBase64(url);  //it went down
                        byte[] byteArr = Base64.decode(attachment, Base64.DEFAULT);
                      //  System.out.println("ra_ "+attachment);
                        File f = new File(file,"sample.jpg");
                        //f.createNewFile();
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(byteArr);

                        fo.close();
                        Toast.makeText(getApplicationContext(), "File downloadinggggggg", Toast.LENGTH_SHORT).show();

                        beginDownload();


//                        try{
//                            Thread.sleep(5000);
//                            //tenth
//                        //    System.out.println("              SHELDON LEE COOPER 10\n"+ i++);
//
//                        }
//                        catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }

                        //My previous share
//                        try {
//                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                            Uri screenshotUri = Uri.parse(Environment.getExternalStorageDirectory() + "/new-folders");
//                            System.out.println("       IMP IMP IMP       SHELDON LEE COOPER 11\n" + 100);
//
//                            sharingIntent.setType("image/*");
//                            sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
//                            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            startActivity(Intent.createChooser(sharingIntent, "Share using"));
//                        }
//                        catch (Exception e)
//                        {
//                            System.out.println("Error");
//
//                        }

                        //sIRS Share Code
//                        try {
//
//                            String finalShareTextContent = URLDecoder.decode(shareTextContent, "UTF-8");
//
//                            File f = getTheFileFromStorage();
//                            String text = "Look at my awesome picture";
//
//                            Uri imageUri = FileProvider.getUriForFile(AdvanceShareActivity.this, getApplicationContext().getPackageName()+".provider", f);
//
//                            Intent shareIntent = new Intent();
//                            shareIntent.setAction(Intent.ACTION_SEND);
//                            shareIntent.putExtra(Intent.EXTRA_TEXT, finalShareTextContent);
//                            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
//                            shareIntent.setType("image/*");
//                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//
//                            startActivity(Intent.createChooser(shareIntent, "Share images..."));
//
//                        } catch (Exception e) {
//                            Log.e("ERROR", "Error in AdvanceShareActivity (openSharableApps) --> " + e.getMessage());
//                        }

                        //New share----------------------------------------------
//                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        shareIntent.setType("*/*");
////set your message
//                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Ello there mate");
//
//                        String imagePath = Environment.getExternalStorageDirectory() + File.separator + "sample.jpg";
//
//                        File imageFileToShare = new File(imagePath);
//
//                        Uri uri = Uri.fromFile(imageFileToShare);
//
//                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

//                        try{
//
//                            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.shareforma);
//                            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
//                            File file = new File(extStorageDirectory, "sample.PNG");
//                            FileOutputStream outStream = new FileOutputStream(file);
//                            bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//                            outStream.flush();
//                            outStream.close();
//                        }
//                        catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        String msgText = "Sample Message";
//
//                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
//                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        shareIntent.setType("image/*");
//
//                        //set your message
//                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, msgText);
//
//                        String imagePath = Environment.getExternalStorageDirectory() + File.separator + "image_name.jpg";
//
//                        File imageFileToShare = new File(imagePath);
//
//                        Uri uri = Uri.fromFile(imageFileToShare);
//
//                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//
//                        startActivity(Intent.createChooser(shareIntent, msgText));

                    }


                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    //---------------------------------------------------------------------------------------------------------------------------------------
   //Never called
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {

                System.out.println("              SHELDON LEE COOPER 13\n");

                String u="https://qrcode-avysh.herokuapp.com";
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(u));
                //Toast.makeText(MainActivity.this, "i AM HERE 1", Toast.LENGTH_LONG).show();

                startActivity(i);
               // Toast.makeText(MainActivity.this, "I am here 2", Toast.LENGTH_LONG).show()
               beginDownload();
                //Toast.makeText(MainActivity.this, "I am here 3", Toast.LENGTH_LONG).show();

                //Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_LONG).show();

                //SHARE CODE ATTEMPT
//                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                Uri screenshotUri = Uri.parse(Environment.getExternalStorageDirectory().toString() + "/new-folder");
//                sharingIntent.setType("*/*");
//                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
//                startActivity(Intent.createChooser(sharingIntent, "Share using"));

            }
        }
    };


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
        unregisterReceiver(onDownloadComplete);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------

    private void beginDownload(){

        System.out.println("              SHELDON LEE COOPER 16\n");



        String url="https://qrcode-avysh.herokuapp.com";
        //[5:53 PM, 6/09/2020] NMAM Sushanth: ?
        //[5:53 PM, 6/09/2020] NMAM Sushanth: https://qrcode-avysh.herokuapp.com
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
            System.out.println("       IMP IMP IMP       SHELDON LEE COOPER 11\n" + 100);

            sharingIntent.setType("image/jpg");
            sharingIntent.putExtra(Intent.EXTRA_EMAIL,"radhikapadiyar@gmail.com");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"aVYSH");

            //File myAttachmentfile =getFileStreamPath(Environment.getExternalStorageDirectory() + "/new-folders/sample.jpg");
         sharingIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file://"+screenshotUri));
           // sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));

            //WEIRDDDDDDDDDDD
//            File imagePath=new File(Environment.getExternalStorageDirectory()+"/new-folders");
//            File newFile=new File(imagePath,"sample.jpg");
//            Uri contentUri=getUriForFile(getContext(),"com")


        }
        catch (Exception e)
        {
            System.out.println("Error");

        }

    }

    //--------------------------------------------------------------------------------------------------------------------------------------------


    }




