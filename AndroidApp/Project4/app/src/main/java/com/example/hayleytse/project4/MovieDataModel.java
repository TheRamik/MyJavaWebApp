package com.example.hayleytse.project4;

public class MovieDataModel {

    String mID;
    String mTitle;
    String mYear;
    String mDir;
    String mGenreList;
    String mStarList;

    public MovieDataModel(String mID, String mTitle, String mYear, String mDir, String mGenreList, String mStarList) {
        this.mID = mID;
        this.mTitle = mTitle;
        this.mYear = mYear;
        this.mDir = mDir;
        this.mGenreList = mGenreList;
        this.mStarList = mStarList;
    }

    public String getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getYear() {
        return mYear;
    }

    public String getDir() {
        return mDir;
    }

    public String getGenreList() {
        return mGenreList;
    }

    public String getStarList() {
        return mStarList;
    }
}
