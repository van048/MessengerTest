package cn.ben.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public class CountingService extends Service {
    private ServerHandler mServerHandler;
    private Messenger mServerMessenger;

    public CountingService() {
        mServerHandler = new ServerHandler();
        mServerMessenger = new Messenger(mServerHandler);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mServerMessenger.getBinder();
    }

    private static class ServerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
