package com.practice.wallpaper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.practice.wallpaper.databinding.ActivitySetWallpaperBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;


public class SetWallpaperActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySetWallpaperBinding binding;
    LinearLayout homeScreen, lockScreen, bothScreen;

    AlertDialog dialog;

    OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetWallpaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        transparentStatusBarAndNavigation();

        String imageUri = getIntent().getStringExtra("imageUri");
        Picasso.get().load(imageUri).into(binding.wallpaperImage);


        binding.setWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectScreen();
            }
        });


        // Download image to phone
        binding.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.wallpaperImage.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();


                String permission;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    permission = Manifest.permission.ACCESS_MEDIA_LOCATION;
                }
                else {
                    permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                }

                Dexter.withContext(getApplicationContext())
                        .withPermission(permission)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                saveImageToGallery(bitmap);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                                permissionToken.continuePermissionRequest();
                            }
                        }).check();

            }
        });

    }


    private void saveImageToGallery(Bitmap bitmap){

        FileOutputStream fileOutputStream;

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,System.currentTimeMillis()+".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+File.separator+"Wallpaper");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

                fileOutputStream = (FileOutputStream) resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                Objects.requireNonNull(fileOutputStream);

                Toast.makeText(this, "Wallpaper Saved", Toast.LENGTH_SHORT).show();
            }

            else {

                    File outFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+File.separator+"Wallpaper");
                    if (!outFile.exists()) {
                        outFile.mkdirs();
                    }

                    File file = new File(outFile,System.currentTimeMillis()+".jpg");
                    outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                    outputStream.flush();
                    outputStream.close();

                    Toast.makeText(SetWallpaperActivity.this, "Wallpaper Saved", Toast.LENGTH_SHORT).show();


            }

        }
        catch (Exception e){

            Toast.makeText(this, "Wallpaper not saved \n"+e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }


    private void selectScreen(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.select_screen_layout,null);
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        homeScreen = view.findViewById(R.id.homeScreen);
        lockScreen = view.findViewById(R.id.lockScreen);
        bothScreen = view.findViewById(R.id.bothScreen);

        homeScreen.setOnClickListener(this);
        lockScreen.setOnClickListener(this);
        bothScreen.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        Bitmap bitmap = ((BitmapDrawable) binding.wallpaperImage.getDrawable()).getBitmap();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

        switch (v.getId()){
            case R.id.homeScreen:

                dialog.dismiss();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_SYSTEM);
                    }
                    else {
                        wallpaperManager.setBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                break;


            case R.id.lockScreen:
                dialog.dismiss();
                try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);
                }
                else {
                    wallpaperManager.setBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
                Toast.makeText(this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                break;


            case R.id.bothScreen:
                dialog.dismiss();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);
                        wallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_SYSTEM);
                    }
                    else {
                        wallpaperManager.setBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                break;
        }

    }



    private void transparentStatusBarAndNavigation(){

        // Transparent Status Bar / Full Screen Activity
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21){
            SetWindowsFlag(this,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        // Make fully transparent status bar
        if (Build.VERSION.SDK_INT >= 21) {
            SetWindowsFlag(this,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }


    }


    private static void SetWindowsFlag(Activity activity, final int Bits, Boolean on) {

        Window window = activity.getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();

        if (on) {
            windowParams.flags |=Bits;
        }
        else {
            windowParams.flags &= ~Bits;
        }
        window.setAttributes(windowParams);

    }


}