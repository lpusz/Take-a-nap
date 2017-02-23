package com.wordpress.antycode.nappster;

import android.media.Ringtone;
import android.support.v7.app.AppCompatActivity;

import com.devadvance.circularseekbar.CircularSeekBar;


public class MyCircularSeekBarListener implements CircularSeekBar.OnCircularSeekBarChangeListener {
    private AppCompatActivity alarmActivity;
    private Ringtone alarmRingtone;

    public MyCircularSeekBarListener(Ringtone alarmRingtone, AppCompatActivity alarmActivity) {
        this.alarmRingtone = alarmRingtone;
        this.alarmActivity = alarmActivity;
    }

    @Override
    public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
        if (progress >= 94 && progress <= 99) {
            alarmRingtone.stop();
            alarmActivity.finish();
        }
    }

    @Override
    public void onStopTrackingTouch(CircularSeekBar seekBar) {
        seekBar.setProgress(0);
    }

    @Override
    public void onStartTrackingTouch(CircularSeekBar seekBar) {

    }
}
