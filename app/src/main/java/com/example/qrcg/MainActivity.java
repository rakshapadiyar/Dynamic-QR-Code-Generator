package com.example.qrcg;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {

    //View Declarations
    WebView myWebview;

    //Object Declarations
    ProgressDialog dialog;
    DownloadManager downloadManager;

    //Variable Declarations
    boolean isAppFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isAppFirstTime = false;
        initView();
        checkAndAskPermission();

    }

    private void initView() {

        try {

            myWebview = (WebView) findViewById(R.id.qr_code_generator_webview);
            downloadManager = (DownloadManager) MainActivity.this.getSystemService(DOWNLOAD_SERVICE);

        } catch (Exception e) {
            Log.e("ERROR", "Error in MainActivity (initView) -->" + e.getMessage());
        }
    }

    //Check permission and ask if needed,
    private void checkAndAskPermission() {

        try {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        } catch (Exception e) {
            Log.e("ERROR", "Error in MainActivity (askPermission) -->" + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        loadWebview();

        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0) {

                    int readExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                    int writeExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (readExternalStorage != PackageManager.PERMISSION_GRANTED || writeExternalStorage != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            if (isAppFirstTime) {
                                checkAndAskPermission();
                            }

                        } else {
                            showPromptToEnablePermission();
                        }
                    }
                }
            }
        }
    }

    private void loadWebview () {

        try {

            String termsConditionUrl = "https://avysh-2.herokuapp.com";

            myWebview.getSettings().setJavaScriptEnabled(true);
            myWebview.setWebViewClient(new QRCodeWebviewClientObj());
            myWebview.loadUrl(termsConditionUrl);
            myWebview.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                    try {

                        if (isRequiredPermissionsGiven()) {

                            dialog = ProgressDialog.show(MainActivity.this, "","Downloading \n Please wait..", true);
                            String base64String = url;
                            String base64Image = base64String.split(",")[1];
                            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                            File cachePath = null;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                cachePath = new File(MainActivity.this.getExternalFilesDir(null)+"/");
                            } else {
                                cachePath =new File( Environment.getExternalStorageDirectory().getAbsolutePath());

                            }


                            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                            decodedByte.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            stream.close();

                            File imagePath = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                imagePath = new File(MainActivity.this.getExternalFilesDir(null)+"/");
                            } else {
                                imagePath =new File( Environment.getExternalStorageDirectory().getAbsolutePath());

                            }

                            File newFile = new File(imagePath, "image.png");
                            Uri contentUri = FileProvider.getUriForFile(MainActivity.this, "com.nitte.qacode.fileprovider", newFile);

                            if (contentUri != null) {
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                                shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                                dialog.dismiss();
                                startActivity(Intent.createChooser(shareIntent, "Choose an app"));
                            }
                        } else {
                            isAppFirstTime = true;
                            checkAndAskPermission();
                        }

                    } catch (Exception e) {
                        Log.i("ERROR", "Errro -->" + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            Log.e("ERROR", "Error in MainActivity (loadWebview) -->" + e.getMessage());
        }
    }

    private class QRCodeWebviewClientObj extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            dialog = ProgressDialog.show(MainActivity.this, "","Please wait..", true);

        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
            // Redirect to deprecated method, so you can use it in all SDK versions

            onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            dialog.dismiss();
        }
    }

    //Check for the permission
    private boolean isRequiredPermissionsGiven() {

        boolean isPermitted = true;
        try {

            int readExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeExternalStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (readExternalStorage == PackageManager.PERMISSION_GRANTED && writeExternalStorage == PackageManager.PERMISSION_GRANTED) {
                isPermitted = true;
            } else {
                isPermitted = false;
            }

        } catch (Exception e) {
            Log.e("ERROR", "Error in MainActivity (isRequiredPermissionsGiven) -->" + e.getMessage());
        }

        return isPermitted;
    }

    //This executes, when the user perminanatly disable permission, we are takin him to app settngs
    public void showPromptToEnablePermission() {

        try {

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
            builder.setMessage("We need your permission, to download and share the QR Code image.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            finish();
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);

                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
            alert.getButton(alert.BUTTON_POSITIVE).setTextColor(Color.parseColor("#003F58"));

        } catch (Exception e) {
            Log.e("ERROR", "Error in AppLauncherActivity (showPromptToEnablePermission) --- >" + e.getMessage());
        }
    }
}
