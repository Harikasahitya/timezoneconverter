package com.example.timezoneconverter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeZoneAdapter extends RecyclerView.Adapter<TimeZoneAdapter.ViewHolder>{


    private String[] timeZones;

    public TimeZoneAdapter(String[] timeZones) {
        this.timeZones = timeZones;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String timeZone = timeZones[position];
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(tz);
        String currentTime = sdf.format(new Date());

        holder.timeZoneTextView.setText(timeZone);
        holder.timeTextView.setText(currentTime);
    }

    @Override
    public int getItemCount() {
        return timeZones.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView timeZoneTextView;
        TextView timeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            timeZoneTextView = itemView.findViewById(android.R.id.text1);
            timeTextView = itemView.findViewById(android.R.id.text2);
        }
    }
}