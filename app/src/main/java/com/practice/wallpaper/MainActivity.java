package com.practice.wallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.practice.wallpaper.adapter.WallpaperAdapter;
import com.practice.wallpaper.databinding.ActivityMainBinding;
import com.practice.wallpaper.model.WallpaperModel;

import java.util.ArrayList;

import okhttp3.internal.http2.Header;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ArrayList<WallpaperModel> arrayList = new ArrayList<>();
    WallpaperAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setNavigationBarColor(getResources().getColor(R.color.light_background));

        /** Shimmer Animation */
        binding.shimmerLayout.startShimmerAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                binding.shimmerLayout.stopShimmerAnimation();
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);

            }
        },2000);


        /** Recycler View */
        adapter = new WallpaperAdapter(this,arrayList);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        binding.recyclerView.setAdapter(adapter);

        database.getReference().child("Wallpaper").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                arrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    WallpaperModel model = dataSnapshot.getValue(WallpaperModel.class);
                    model.getName(dataSnapshot.getKey());
                    arrayList.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}