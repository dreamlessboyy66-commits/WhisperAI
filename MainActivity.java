package com.imtiaz.whisper;

import android.app.Activity;
import android.content.*;
import android.graphics.Color;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int REQ_CAPTURE = 1001;
    private EditText keyInput, promptInput;
    private SeekBar transparency;
    private TextView status;
    private SharedPreferences prefs;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("whisper", MODE_PRIVATE);
        buildUi();
    }

    private int dp(float v){ return (int)(v*getResources().getDisplayMetrics().density+0.5f); }

    private TextView label(String s){
        TextView t=new TextView(this); t.setText(s); t.setTextColor(Color.WHITE);
        t.setTextSize(16); t.setPadding(0,dp(10),0,dp(5)); return t;
    }

    private void buildUi(){
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20),dp(18),dp(20),dp(20)); root.setBackgroundColor(Color.rgb(11,23,34));

        TextView title=label("Whisper AI"); title.setTextSize(30); title.setTypeface(null,1);
        root.addView(title);

        TextView info=label("Floating AI assistant • Screenshot → Gemini → Answer");
        info.setTextColor(Color.rgb(150,180,215)); root.addView(info);

        root.addView(label("Gemini API key"));
        keyInput=new EditText(this); keyInput.setHint("Paste your Gemini API key");
        keyInput.setHintTextColor(Color.rgb(120,145,170)); keyInput.setTextColor(Color.WHITE);
        keyInput.setSingleLine(true); keyInput.setInputType(129);
        keyInput.setText(prefs.getString("key",""));
        root.addView(keyInput,new LinearLayout.LayoutParams(-1,dp(52)));

        root.addView(label("Optional instruction"));
        promptInput=new EditText(this); promptInput.setHint("e.g. Explain simply / give only the answer");
        promptInput.setHintTextColor(Color.rgb(120,145,170)); promptInput.setTextColor(Color.WHITE);
        promptInput.setSingleLine(false); promptInput.setMaxLines(3);
        promptInput.setText(prefs.getString("prompt",""));
        root.addView(promptInput,new LinearLayout.LayoutParams(-1,dp(70)));

        root.addView(label("Overlay transparency"));
        transparency=new SeekBar(this); transparency.setMax(100);
        transparency.setProgress(prefs.getInt("alpha",92)); root.addView(transparency);

        Button start=new Button(this); start.setText("START WHISPER OVERLAY");
        start.setOnClickListener(v -> startFlow());
        root.addView(start,new LinearLayout.LayoutParams(-1,dp(55)));

        Button stop=new Button(this); stop.setText("STOP OVERLAY");
        stop.setOnClickListener(v -> stopService(new Intent(this,OverlayService.class)));
        root.addView(stop,new LinearLayout.LayoutParams(-1,dp(55)));

        status=label("Status: Ready"); status.setTextColor(Color.rgb(150,180,215));
        root.addView(status);

        TextView note=label("How it works:\n1) Start overlay and grant the Android screen-capture permission.\n2) Open any app/document.\n3) Tap the blue Whisper bubble.\n4) Gemini reads the current screen and returns an answer in the overlay.");
        note.setTextColor(Color.rgb(150,180,215)); note.setTextSize(14); root.addView(note);

        setContentView(root);
    }

    private void startFlow(){
        String key=keyInput.getText().toString().trim();
        if(key.isEmpty()){ keyInput.setError("Gemini API key required"); return; }
        prefs.edit().putString("key",key)
                .putString("prompt",promptInput.getText().toString())
                .putInt("alpha",transparency.getProgress()).apply();

        if(!Settings.canDrawOverlays(this)){
            Intent i=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:"+getPackageName()));
            startActivity(i);
            Toast.makeText(this,"Allow 'Display over other apps', then press START again.",Toast.LENGTH_LONG).show();
            return;
        }

        MediaProjectionManager m=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        startActivityForResult(m.createScreenCaptureIntent(),REQ_CAPTURE);
    }

    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQ_CAPTURE && resultCode==RESULT_OK && data!=null){
            Intent s=new Intent(this,OverlayService.class);
            s.putExtra("resultCode",resultCode);
            s.putExtra("projectionData",data);
            s.putExtra("alpha",transparency.getProgress());
            startForegroundService(s);
            status.setText("Status: Whisper is running");
            Toast.makeText(this,"Whisper overlay started.",Toast.LENGTH_SHORT).show();
        } else if(requestCode==REQ_CAPTURE){
            status.setText("Status: Screen capture permission cancelled");
        }
    }
}