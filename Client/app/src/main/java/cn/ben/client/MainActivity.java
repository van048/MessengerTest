package cn.ben.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CS_START_COUNT = 0;
    private static final int SC_SHOW_NUM = 1;

    private Handler mClientHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SC_SHOW_NUM:
                    if (mTextView != null)
                        mTextView.setText(String.valueOf(msg.arg1));
                    return;
                default:
                    break;
            }

            super.handleMessage(msg);
        }
    };
    private Messenger mClientMessenger;
    private Messenger mServerMessenger;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServerMessenger = new Messenger(service);
            askServerToCount();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServerMessenger = null;
        }
    };

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        mClientMessenger = new Messenger(mClientHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getExplicitIntent(this, new Intent("cn.ben.countingService"));
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        unbindService(mServiceConnection);

        super.onPause();
    }

    @Nullable
    private Intent getExplicitIntent(@NonNull Context context, @NonNull Intent implicitIntent) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) return null;

        List<ResolveInfo> resolveInfoList = packageManager.queryIntentServices(implicitIntent, 0);
        // make sure it only has one service matched
        if (resolveInfoList == null || resolveInfoList.size() != 1) {
            return null;
        }
        ResolveInfo resolveInfo = resolveInfoList.get(0);
        if (resolveInfo == null || resolveInfo.serviceInfo == null) return null;

        String packageName = resolveInfo.serviceInfo.packageName;
        String className = resolveInfo.serviceInfo.name;
        ComponentName componentName = new ComponentName(packageName, className);

        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(componentName);
        return explicitIntent;
    }

    private void askServerToCount() {
        Message message = Message.obtain();
        message.what = CS_START_COUNT;
        message.replyTo = mClientMessenger;
        try {
            mServerMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
