package lk.waplak.dakma;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import lk.waplak.dakma.utility.AndroidUtill;

public class SplashActivity extends AppCompatActivity {
    ProgressBar bar;
    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_splash);
        bar = findViewById(R.id.progressBar01);
        txt = findViewById(R.id.txtrere);
        final SpannableStringBuilder sb = new SpannableStringBuilder("Copyright © 2017 Dekma Institute. All Rights Reserved. | Powered by innosoft");
        TextView txtt = findViewById(R.id.footerMark);
        // Span to set text color to some RGB value
        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(255, 255, 255));
        // Span to make text bold
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        // Set the text color for first 4 characters
        sb.setSpan(fcs, 0, 68, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        txtt.setText(sb);
        if(AndroidUtill.isNetworkConnected(SplashActivity.this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                }
            }, 3000);
        }else{
            Toast.makeText(
                    SplashActivity.this,
                    "No internet connection", Toast.LENGTH_LONG).show();
            finish();
        }
    }

}
