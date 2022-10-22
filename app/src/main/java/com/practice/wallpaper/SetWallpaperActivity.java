package com.practice.wallpaper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.practice.wallpaper.databinding.ActivitySetWallpaperBinding;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class SetWallpaperActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySetWallpaperBinding binding;
    LinearLayout homeScreen, lockScreen, bothScreen;

    AlertDialog dialog;

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