package com.wordpress.antycode.nappster;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

/**
 * @author Łukasz Pusz
 * @version 1.0.0
 * @since 0.1.0
 */

public class MainActivity extends AppCompatActivity {
    public static final String NAP_LENGTH = "NAP_LENGTH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setupGridViews();
    }

    private void createShowPopup() {
        LayoutInflater mInflater = (LayoutInflater) getBaseContext().getSystemService(
                LAYOUT_INFLATER_SERVICE);

        final View mView = mInflater.inflate(R.layout.fast_nap_popup, null);
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        popDialog.setTitle(R.string.select_nap_time);
        popDialog.setView(mView);

        final SeekBar seekBar = (SeekBar) mView.findViewById(R.id.customNap_length_seekBar);
        setupSeekBarChangeListener(mView, seekBar);

        popDialog.setPositiveButton(R.string.start_nap,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent timerIntent = new Intent(getApplicationContext(), TimerActivity.class);
                        timerIntent.putExtra(NAP_LENGTH, Integer.toString(seekBar.getProgress()));
                        startActivity(timerIntent);
                    }
                });

        popDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        popDialog.create();
        popDialog.show();
    }

    private void setupSeekBarChangeListener(final View mView, SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            TextView napLengthLabel = (TextView) mView.findViewById(R.id.customNap_length_textView);
            int minutes, hours;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int minimumProgress = 1;

                if (i < minimumProgress) {
                    seekBar.setProgress(minimumProgress);
                    i = 1;
                }

                minutes = i % 60;
                hours = i / 60;
                napLengthLabel.setText(String.format(Locale.US, "%02d:%02d h", hours, minutes));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_fast_custom_nap:
                createShowPopup();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupGridViews() {
        String[] leftNapsArray = getResources().getStringArray(R.array.naps_array_left);
        String[] rightNapsArray = getResources().getStringArray(R.array.naps_array_right);

        GridView gridViewLeft = (GridView) findViewById(R.id.gridview1);
        GridView gridViewRight = (GridView) findViewById(R.id.gridview2);

        gridViewLeft.setAdapter(new NapsAdapterLeft(this, leftNapsArray));
        gridViewRight.setAdapter(new NapsAdapterRight(this, rightNapsArray));
    }

    public void startMainNap(View view) {
        Intent timerIntent = new Intent(this, TimerActivity.class);
        timerIntent.putExtra(NAP_LENGTH, getResources().getString(R.string.main_nap_name_PL));
        startActivity(timerIntent);
    }
}
