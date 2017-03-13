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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ClientHandler mClientHandler;
    private Messenger mClientMessenger;
    private Messenger mServerMessenger;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServerMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServerMessenger = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClientHandler = new ClientHandler();
        mClientMessenger = new Messenger(mClientHandler);
    }

    @Nullable
    private Intent getExplicitIntent(@NonNull Context context, @NonNull Intent implicitIntent) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) return null;

        List<ResolveInfo> resolveInfoList = packageManager.queryIntentServices(implicitIntent, 0);
        // only have one service matches
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

    private static class ClientHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
