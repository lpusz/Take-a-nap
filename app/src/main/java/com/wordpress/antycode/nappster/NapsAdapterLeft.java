package com.wordpress.antycode.nappster;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static com.wordpress.antycode.nappster.MainActivity.NAP_LENGTH;

public class NapsAdapterLeft extends BaseAdapter {
    private String[] napsArray;
    private Context superActivityContext;
    private LayoutInflater layoutInflater;
    private DisplayMetrics displayMetrics;

    public NapsAdapterLeft(MainActivity mainActivity, String[] napsArray) {
        superActivityContext = mainActivity;
        this.napsArray = napsArray;
        layoutInflater = (LayoutInflater) superActivityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        displayMetrics = mainActivity.getResources().getDisplayMetrics();
    }

    @Override
    public int getCount() {
        return napsArray.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class Holder {
        TextView textView;
        ImageView imageView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = layoutInflater.inflate(R.layout.nap_gridview_item, null);
        rowView.setBackgroundResource(R.drawable.rect_bg_left);

        holder.textView = (TextView) rowView.findViewById(R.id.textView1);
        holder.imageView = (ImageView) rowView.findViewById(R.id.imageView1);


        holder.textView.setText(napsArray[position]);
        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_HIGH:
                holder.imageView.setImageResource(R.drawable.ic_alarm_white_24dp);
                holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PT, 6);
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                holder.imageView.setImageResource(R.drawable.ic_alarm_white_36dp);
                holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PT, 7);
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                holder.imageView.setImageResource(R.drawable.ic_alarm_white_36dp);
                holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PT, 7);
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                holder.imageView.setImageResource(R.drawable.ic_alarm_white_36dp);
                holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PT, 8);
                break;
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent timerIntent = new Intent(superActivityContext, TimerActivity.class);
                timerIntent.putExtra(NAP_LENGTH, napsArray[position]);
                superActivityContext.startActivity(timerIntent);
            }
        });

        return rowView;
    }
}
