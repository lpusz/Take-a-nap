package com.wordpress.antycode.nappster;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.wordpress.antycode.nappster.MainActivity.NAP_LENGTH;

public class TimerActivity extends AppCompatActivity {
    public static final int BACK_DELAY = 3000;
    public static final int MILLIS_IN_MINUTE = 60000;  //change to lower if want to speed up counting

    private CountDownTimer countDownTimer;
    private CircleProgress arcProgress;
    private long originalNapTime;
    private long napTimeInMillis;
    private long savedNapTime;
    private boolean doubleBackToExitPressedOnce;
    private boolean paused = false;
    private boolean stopped = false;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;
    private Toast toast;
    private boolean notificationsState;
    private String countDownTime;
    private int test = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toast = Toast.makeText(this, getString(R.string.wcisnij_ponownie_powrot), Toast.LENGTH_SHORT);
        doubleBackToExitPressedOnce = false;

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        notificationsState = preference.getBoolean("notification_sync", false);

        arcProgress = (CircleProgress) findViewById(R.id.arc_progress);

        int napTimeInMinutes = calculateNapTime(getIntent().getExtras().getString(NAP_LENGTH));
        startCounting(napTimeInMinutes);
    }

    private void startCounting(int napTimeInMinutes) {
        napTimeInMillis = napTimeInMinutes * MILLIS_IN_MINUTE;
        originalNapTime = napTimeInMillis;
        savedNapTime = napTimeInMillis;
        createCountDownTimer(napTimeInMillis, napTimeInMillis);
    }

    public void performButtonAction(View view) {
        Button continuePauseButton = (Button) view;
        String buttonText = continuePauseButton.getText().toString();

        if (buttonText.equals(getString(R.string.pauza))) {
            pauseCounting();
        } else if (buttonText.equals(getString(R.string.resetuj))) {
            resetCounting();
        } else {
            resumeCounting();
        }
    }

    private void pauseCounting() {
        setButtonProperties(R.id.start_pause_button, R.string.kontynuuj, R.drawable.ic_play_circle_outline_white_24dp);
        countDownTimer.cancel();
    }

    private void resetCounting() {
        stopped = false;
        napTimeInMillis = originalNapTime;
        setButtonProperties(R.id.start_pause_button, R.string.pauza, R.drawable.ic_pause_circle_outline_white_24dp);
        createCountDownTimer(napTimeInMillis, napTimeInMillis);
    }

    private void resumeCounting() {
        setButtonProperties(R.id.start_pause_button, R.string.pauza, R.drawable.ic_pause_circle_outline_white_24dp);
        createCountDownTimer(savedNapTime, napTimeInMillis);
    }

    private void stopCounting() {
        stopped = true;
        napTimeInMillis = originalNapTime;
        arcProgress.setProgress(100);
        setButtonProperties(R.id.start_pause_button, R.string.resetuj, R.drawable.ic_replay_white_24dp);
        countDownTimer.cancel();
    }

    public void stopCountingOnClick(View view) {
        stopCounting();
    }

    private void createCountDownTimer(long timeInFuture, final long generalNapTime) {
        setTextViewProperties(R.string.extend_nap);
        countDownTimer = new CountDownTimer(timeInFuture, 1000) {
            public void onTick(long millisUntilFinished) {
                if (test == 0) {
                    setButtonProperties(R.id.start_pause_button, R.string.pauza, R.drawable.ic_pause_circle_outline_white_24dp);
                }

                countDownTime = getTimeLeftString(millisUntilFinished);
                long progress = ((100L * (generalNapTime - millisUntilFinished)) / generalNapTime);

                getSupportActionBar().setTitle(getString(R.string.pozostaly_czas) + " " + countDownTime);

                Log.v("spec", "-------------------");
                Log.v("progressTest", Long.toString(progress));
                Log.v("napTimeInMillis", Long.toString(napTimeInMillis));
                Log.v("GeneralNapTime = ", Long.toString(generalNapTime));
                Log.v("MillisUntilFinished = ", Long.toString(millisUntilFinished));
                Log.v("SavedTime", Long.toString(savedNapTime));

                arcProgress.setProgress((int) progress);
                if (paused) {
                    setNotificationTextAndNotify(countDownTime, 0);
                }
                savedNapTime = millisUntilFinished;
            }

            public void onFinish() {
                arcProgress.setProgress(100);
                getSupportActionBar().setTitle(getString(R.string.czas_uplynal));
                if (paused) {
                    setNotificationTextAndNotify("00:00", 0);
                }
                setButtonProperties(R.id.start_pause_button, R.string.resetuj, R.drawable.ic_replay_white_24dp);
                Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                startActivity(intent);
                stopCounting();
            }
        }.start();
    }

    private void setNotificationTextAndNotify(String contentText, int notifId) {
        if (notificationsState) {
            mBuilder.setContentText(contentText);
            notificationManager.notify(notifId, mBuilder.build());
        }
    }

    private String getTimeLeftString(long millisUntilFinished) {
        int napLengthMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
        int hours = (int) TimeUnit.MINUTES.toHours(napLengthMinutes);
        int minutes = napLengthMinutes % 60;
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(napLengthMinutes));

        if (napLengthMinutes <= 60) {
            return String.format(Locale.GERMANY, "%2d:%02d", napLengthMinutes, seconds);
        } else {
            return String.format(Locale.GERMANY, "%2d:%02d:%02d", hours, minutes, seconds);
        }
    }

    public void extendNap(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();

        int extensionTime = Integer.parseInt(extractInteger(buttonText, 3));
        long extensionTimeInMillis = TimeUnit.MINUTES.toMillis(extensionTime);

        long newNapTime = napTimeInMillis + extensionTimeInMillis;
        if (newNapTime < TimeUnit.MINUTES.toMillis(720) && !stopped) {
            napTimeInMillis = newNapTime;
            countDownTimer.cancel();
            setButtonProperties(R.id.start_pause_button, R.string.pauza, R.drawable.ic_pause_circle_outline_white_24dp);
            createCountDownTimer(savedNapTime + extensionTimeInMillis, newNapTime);
        } else {
            if (!stopped)
                Toast.makeText(this, getString(R.string.osiagnieto_max_drzemki), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.nie_mozna_przedluzyc_drzemki), Toast.LENGTH_SHORT).show();
        }
    }

    private int calculateNapTime(String napName) {
        //To jest zrobione w ten sposób, ponieważ na guzikach jest np.: "5 minut" i to ma za zadania
        //wyciągnąć te inty z tego
        return Integer.parseInt(extractInteger(napName, 3));
    }

    @NonNull
    private String extractInteger(String string, int length) {
        StringBuilder builder = new StringBuilder();

        char currentChar;
        try {
            for (int i = 0; i < length; i++) {
                currentChar = string.charAt(i);
                if (Character.isDigit(currentChar))
                    builder.append(currentChar);
            }
        } catch (StringIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }

    private void setButtonProperties(int buttonId, int buttonText, int buttonDrawable) {
        Button button = (Button) findViewById(buttonId);
        button.setText(buttonText);
        button.setCompoundDrawablesRelativeWithIntrinsicBounds(getResources()
                .getDrawable(buttonDrawable), null, null, null);
    }

    private void setTextViewProperties(int stringId) {
        TextView textView = (TextView) findViewById(R.id.extend_nap_text_view);
        textView.setText(stringId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (!stopped) {
            switch (itemId) {
                case android.R.id.home:
                    clickTwiceToBack(BACK_DELAY);
                    break;
            }
            return true;
        } else {
            super.onBackPressed();
            return true;
        }
    }

    private NotificationCompat.Builder createNotification(int iconId, int titleId) {
        Intent notificationIntent = new Intent(getApplicationContext(), TimerActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        return (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(iconId)
                .setContentTitle(getString(titleId))
                .setContentText(getString(R.string.spauzowano))
                .setOngoing(true)
                .setContentIntent(pendingIntent);
    }

    private void cancelNotification(int notificationId) {
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;

        if ((!doubleBackToExitPressedOnce && !stopped) && notificationsState) {
            mBuilder = createNotification(R.drawable.ic_alarm_white_36dp, R.string.drzemka_w_toku);
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, mBuilder.build());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        paused = false;
        cancelNotification(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelNotification(0);
    }

    @Override
    public void onBackPressed() {
        if (!stopped) {
            clickTwiceToBack(BACK_DELAY);
        } else {
            super.onBackPressed();
        }
    }

    private void clickTwiceToBack(int delay) {
        if (doubleBackToExitPressedOnce) {
            toast.cancel();
            super.onBackPressed();
            countDownTimer.cancel();
        } else {
            toast.show();
        }

        this.doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, delay);
    }
}