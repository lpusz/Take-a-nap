package com.wordpress.antycode.nappster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        StringBuilder builder = new StringBuilder("\nCircle Progress\n");
        builder.append("https://github.com/lzyzsd/CircleProgress\n");
        builder.append("\nCircular Seek Bar\n");
        builder.append("https://github.com/devadvance/circularseekbar\n");
        builder.append("\nGoogle Material Icons\n");
        builder.append("https://material.io/icons/");

        TextView copyrights = (TextView) findViewById(R.id.thanksTo_desc_textView);
        copyrights.setMovementMethod(LinkMovementMethod.getInstance());
        copyrights.setText(builder.toString());
    }
}
