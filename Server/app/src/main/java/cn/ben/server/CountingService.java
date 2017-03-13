package cn.ben.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class CountingService extends Service {

    private static final int CS_START_COUNT = 0;
    private static final int SC_SHOW_NUM = 1;

    private Messenger mClientMessenger;
    private final Messenger mServerMessenger;

    private final Thread mCountingThread = new Thread(new Runnable() {
        int i = 0;

        @Override
        public void run() {
            while (!mShouldStopCountingThread) {
                Message message = Message.obtain();
                message.what = SC_SHOW_NUM;
                message.arg1 = i++;
                try {
                    mClientMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });
    private boolean mShouldStopCountingThread;

    public CountingService() {
        ServerHandler serverHandler = new ServerHandler();
        mServerMessenger = new Messenger(serverHandler);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mServerMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mShouldStopCountingThread = true;
        return super.onUnbind(intent);
    }

    private class ServerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CS_START_COUNT:
                    mClientMessenger = msg.replyTo;
                    mShouldStopCountingThread = false;
                    mCountingThread.start();
                    return;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
