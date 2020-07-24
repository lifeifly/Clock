package com.example.clock1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends AppCompatActivity {
    private TabHost tabHost;
    private StopWatch stopWatch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabHost=(TabHost)findViewById(android.R.id.tabhost);
        tabHost.setup();//初始化
        tabHost.addTab(tabHost.newTabSpec("tabClock").setIndicator("时钟").setContent(R.id.tabClock));
        tabHost.addTab(tabHost.newTabSpec("tabAlarm").setIndicator("闹钟").setContent(R.id.tabAlarm));
        tabHost.addTab(tabHost.newTabSpec("tabTimer").setIndicator("计时器").setContent(R.id.tabTimer));
        tabHost.addTab(tabHost.newTabSpec("tabStopWatch").setIndicator("秒表").setContent(R.id.tabStopWatch));
        stopWatch=(StopWatch)findViewById(R.id.tabStopWatch);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopWatch.onDestroy();
    }
}
