package com.practice.wallpaper.model;

public class WallpaperModel {

    String name, category, imageUri;

    public WallpaperModel() {
    }

    public WallpaperModel(String name, String category, String imageUri) {
        this.name = name;
        this.category = category;
        this.imageUri = imageUri;
    }

    public String getName(String key) {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
