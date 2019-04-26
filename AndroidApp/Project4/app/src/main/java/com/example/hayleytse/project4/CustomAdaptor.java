package com.example.hayleytse.project4;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdaptor extends ArrayAdapter<MovieDataModel> {
    private ArrayList<MovieDataModel> movieSet;
    Context mContext;

    public CustomAdaptor(ArrayList<MovieDataModel> data, Context context) {
        super(context, R.layout.row_item, data);
        this.movieSet = data;
        this.mContext = context;
    }

    private static class ViewHolder {
        TextView txtTitle;
        TextView txtYear;
        TextView txtDir;
        TextView txtGenres;
        TextView txtStars;
        //ImageView info;
    }

//    @Override
//    public void onClick(View v) {
//
//        int position=(Integer) v.getTag();
//        Object object= getItem(position);
//        MovieDataModel movieDataModel=(MovieDataModel)object;
//
//        switch (v.getId())
//        {
//            case R.id.movie_info:
//                Snackbar.make(v, "Genres: " + movieDataModel.getGenreList() + "\n" +
//                            "Stars: " + movieDataModel.getStarList(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//
//                break;
//        }
//    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MovieDataModel movieDataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtTitle = convertView.findViewById(R.id.movieTitle);
            viewHolder.txtDir = convertView.findViewById(R.id.movieDirector);
            viewHolder.txtYear = convertView.findViewById(R.id.movieYear);
            viewHolder.txtGenres = convertView.findViewById(R.id.movieGenres);
            viewHolder.txtStars = convertView.findViewById(R.id.movieStars);
            //viewHolder.info = convertView.findViewById(R.id.movie_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtTitle.setText(movieDataModel.getTitle());
        viewHolder.txtDir.setText(movieDataModel.getDir());
        viewHolder.txtYear.setText(movieDataModel.getYear());
        viewHolder.txtGenres.setText(movieDataModel.getGenreList());
        viewHolder.txtStars.setText(movieDataModel.getStarList());
        //viewHolder.info.setOnClickListener(this);
        //viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
