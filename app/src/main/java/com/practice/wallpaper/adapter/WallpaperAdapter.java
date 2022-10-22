package com.practice.wallpaper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.wallpaper.R;
import com.practice.wallpaper.SetWallpaperActivity;
import com.practice.wallpaper.model.WallpaperModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder> {

    Context context;
    ArrayList<WallpaperModel> list;

    public WallpaperAdapter(Context context, ArrayList<WallpaperModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper,parent,false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Picasso.get().load(list.get(position).getImageUri()).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SetWallpaperActivity.class);
                intent.putExtra("imageUri",list.get(position).getImageUri());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class WallpaperViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public WallpaperViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.item_image);
            imageView.setClipToOutline(true);

        }
    }

}
