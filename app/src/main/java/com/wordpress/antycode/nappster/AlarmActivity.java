package com.wordpress.antycode.nappster;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devadvance.circularseekbar.CircularSeekBar;

public class AlarmActivity extends AppCompatActivity {
    private Button[] buttons;
    private int[] equationData;
    private Ringtone alarmRingtone;
    private RandomEquationFactory equationFactory;
    private SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        aboveLockScreen();
        createPulsingTextView(R.id.pulsing_textview);

        preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String testRingtone = preference.getString("ringtone_sync", "DEFAULT_SOUND");
        Uri ringtoneUri = Uri.parse(testRingtone);

        int volume = preference.getInt("volume_sync", 0);
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_ALARM,  volume, 0);

        alarmRingtone = createAlarmRingtone(ringtoneUri);
        alarmRingtone.play();
        setViewStub();
    }

    private void setViewStub() {
        String activePattern = preference.getString("pattern_sync", "DEFAULT_PATTERN").toLowerCase();
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        switch (activePattern) {
            default:
                stub.setLayoutResource(R.layout.fragment_alarm_interrupter);
                stub.inflate();
                createDefaultUnlocker();
                break;
            case "equation":
                stub.setLayoutResource(R.layout.equation_alarm_interrupter);
                stub.inflate();
                createEquationUnlocker();
                break;
            case "równanie":
                stub.setLayoutResource(R.layout.equation_alarm_interrupter);
                stub.inflate();
                createEquationUnlocker();
                break;
        }
    }

    private void createDefaultUnlocker() {
        CircularSeekBar circularSeekBar = (CircularSeekBar) findViewById(R.id.circularAlarmInterrupter);
        circularSeekBar.setProgress(0);
        circularSeekBar.setOnSeekBarChangeListener(new MyCircularSeekBarListener(alarmRingtone, this));
    }

    private void createEquationUnlocker() {
        equationFactory = RandomEquationFactory.getInstance();
        equationData = equationFactory.getRandomEquationData();

        TextView equationView = (TextView) findViewById(R.id.equation_textView);
        buttons = createButtonsArray();

        String formattedEquation = equationFactory.getFormattedEquationString(equationData);
        equationView.setText(formattedEquation);

        asignNumbersToButtons(equationFactory.getNormalRandom(buttons.length));
    }

    private Button[] createButtonsArray() {
        Button leftButton = (Button) findViewById(R.id.left_equation_button);
        Button centerButton = (Button) findViewById(R.id.center_equation_button);
        Button rightButton = (Button) findViewById(R.id.right_equation_button);
        return new Button[]{leftButton, centerButton, rightButton};
    }

    private void asignNumbersToButtons(int randomButtonNumber) {
        String tmp;
        for (int i = 0; i < buttons.length; i++) {
            if (i == randomButtonNumber) {
                tmp = Integer.toString(equationData[2]);
                buttons[i].setText(tmp);
                buttons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alarmRingtone.stop();
                        finish();
                    }
                });
            } else {
                int tmpRandom = equationFactory.getRandomNumber(equationData[2]);
                tmp = Integer.toString(tmpRandom);
                buttons[i].setText(tmp);
                buttons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createEquationUnlocker();
                    }
                });
            }
        }
    }

    private Ringtone createAlarmRingtone(Uri ringtoneUri) {
        Ringtone alarmRingtone;
        alarmRingtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
        alarmRingtone.setStreamType(AudioManager.STREAM_ALARM);

        return alarmRingtone;
    }

    private TextView createPulsingTextView(int textViewId) {
        TextView textView = (TextView) findViewById(textViewId);
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200); //You can manage the time of the blink with this parameter
        anim.setStartOffset(50);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        textView.startAnimation(anim);
        return textView;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getString(R.string.prosze_wylaczyc_alarm), Toast.LENGTH_SHORT).show();
    }

    public void aboveLockScreen() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }
}
