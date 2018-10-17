package com.gitlab.faerytea.vkphotos;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import static android.Manifest.permission.INTERNET;

public class MainActivity extends AppCompatActivity {
    private static final String[] LINKS = new String[] {
            "https://planshetuk.ru/wp-content/uploads/2015/12/120615_0627_16.jpg.pagespeed.ce.Irh24ZCxMs.jpg",
            "https://yt3.ggpht.com/a-/AN66SAzJAk9UBEgWOP82Uc_Nb8jaXgZAF0mw0OsEJg=s900-mo-c-c0xffffffff-rj-k-no",
            "https://novostel.ru/wp-content/uploads/2018/02/d0bcd0b0d0bbd0b2d0b0d180d18c-reddrop-d181d0bbd0b5d0b4d0b8d182-d0b7d0b0-d0bfd0bed0bbd18cd0b7d0bed0b2d0b0d182d0b5d0bbd18fd0bcd0b8-android-d0b8.jpg",
            "https://dixnews.ru/wp-content/uploads/2017/04/android.jpg",
            "https://securenews.ru/wp-content/uploads/2016/06/android-blue.jpg",
            "http://geek-nose.com/wp-content/uploads/2017/11/15e90b7e7ef493438c15f8e9dda84a80.jpg"
    };

    private Loader.MyBinder binder;
    private Adapter adapter = new Adapter(LINKS.length);
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MainActivity.this.binder = (Loader.MyBinder) service;
            binder.setCallback(p -> adapter.setElement(p.first, p.second));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView list = findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this));
        ContextCompat.checkSelfPermission(this, INTERNET);
        for (int i = 0; i < LINKS.length; ++i)
            Loader.load(this, i, LINKS[i]);
        bindService(new Intent(this, Loader.class), serviceConnection, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
