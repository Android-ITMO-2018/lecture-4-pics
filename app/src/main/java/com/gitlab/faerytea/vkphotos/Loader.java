package com.gitlab.faerytea.vkphotos;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;

public class Loader extends IntentService {
    private static final String ACTION_LOAD = "com.gitlab.faerytea.vkphotos.action.load";
    private static final String EXTRA_URL = "com.gitlab.faerytea.vkphotos.extra.url";
    private static final String EXTRA_ID = "com.gitlab.faerytea.vkphotos.extra.id";
    private static final String LOG_TAG = Loader.class.getSimpleName();

    private final Queue<Pair<Integer, byte[]>> responses = new LinkedList<>();
    private final Handler main = new Handler(Looper.getMainLooper());
    private OnLoad callback;

    public Loader() {
        super("Loader");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void load(Context context, int id, String url) {
        Intent intent = new Intent(context, Loader.class);
        intent.setAction(ACTION_LOAD);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_ID, id);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            val action = intent.getAction();
            val url = intent.getStringExtra(EXTRA_URL);
            val id = intent.getIntExtra(EXTRA_ID, -1);
            if (ACTION_LOAD.equals(action) && url != null && id != -1) {
                loadPicture(id, url);
            }
        }
    }

    private void loadPicture(int id, @NonNull final String urlString) {
        byte[] res;
        try {
            val url = new URL(urlString);
            val connection = url.openConnection();
            connection.connect();
            res = new byte[connection.getContentLength()];
            try (val is = connection.getInputStream()) {
                int p = 0;
                int r;
                while ((r = is.read(res, p, res.length - p)) > 0) p += r;
            }
        } catch (IOException e) {
            e.printStackTrace();
            res = null;
        }
        val result = res;
        Log.d(LOG_TAG, "loadPicture: got: id: " + id + ", data.length = " + (res == null ? null : res.length));
        main.post(() -> deliver(id, result));
    }

    private void deliver(int id, @Nullable byte[] data) {
        if (callback != null) callback.onLoad(Pair.create(id, data));
        else responses.add(Pair.create(id, data));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind: ");
        return new MyBinder(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOG_TAG, "onUnbind: ");
        callback = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(LOG_TAG, "onRebind: ");
        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate: ");
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(LOG_TAG, "onStart: ");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy: ");
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MyBinder extends Binder {
        private final Loader service;

        public void setCallback(@NonNull final OnLoad callback) {
            new Handler(Looper.getMainLooper()).post(() -> {
                service.callback = callback;
                while (!service.responses.isEmpty())
                    service.callback.onLoad(service.responses.remove());
            });
        }
    }

    public interface OnLoad {
        void onLoad(@NonNull Pair<Integer, byte[]> data);
    }
}
