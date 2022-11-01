package com.practice.wallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.util.Collections;


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
                binding.shimmerSwipeLayout.setVisibility(View.GONE);
                binding.linearLayout.setVisibility(View.VISIBLE);

                /** Check Internet conn */
                if (!isConnected()){
                    binding.shimmerLayout.startShimmerAnimation();
                    binding.shimmerSwipeLayout.setVisibility(View.VISIBLE);
                    binding.linearLayout.setVisibility(View.GONE);
                }

            }
        },2000);


        showItemInRecyclerView();

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                showItemInRecyclerView();
                binding.swipeRefresh.setRefreshing(false);
            }
        });



        binding.internetSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (isConnected()){

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.shimmerSwipeLayout.setVisibility(View.GONE);
                            binding.linearLayout.setVisibility(View.VISIBLE);
                            binding.internetSwipe.setRefreshing(false);
                        }
                    },2000);


                }
                else {
                    binding.internetSwipe.setRefreshing(false);
                    return;
                }



            }
        });

    }


    private void showItemInRecyclerView(){

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
                Collections.shuffle(arrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /** Check Internet connection */
    public boolean isConnected(){

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);

        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())){

            return true;
        }
        else {
            return false;
        }

    }



}