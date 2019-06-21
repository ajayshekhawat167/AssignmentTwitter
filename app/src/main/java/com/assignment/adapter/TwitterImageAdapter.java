package com.assignment.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.assignment.R;

import com.assignment.network.model.HashTagImage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TwitterImageAdapter extends RecyclerView.Adapter<TwitterImageAdapter.ViewHolder> {

    private ArrayList<HashTagImage> hashTagImageArrayList;
    private Context context;

    public TwitterImageAdapter(Context context, ArrayList<HashTagImage> hashTagImageArrayList) {
        this.hashTagImageArrayList = hashTagImageArrayList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImage;
        private TextView mTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mImage        = (ImageView) itemView.findViewById(R.id.image);
            mTitle        = (TextView) itemView.findViewById(R.id.title);

        }
    }

    @Override
    public TwitterImageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_twitter_image, viewGroup, false);

        return new TwitterImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TwitterImageAdapter.ViewHolder holder, int position) {
        HashTagImage hashTagImage = hashTagImageArrayList.get(position);

        holder.mTitle.setText(hashTagImage.title);

        Picasso.with(context)
                .load(hashTagImage.image)
                .into(holder.mImage);

    }

    @Override
    public int getItemCount() {
        return hashTagImageArrayList.size();
    }
}
