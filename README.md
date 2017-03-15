# Use Messenger to communicate between processes

## Client
```java
    private Messenger mClientMessenger;
    private Messenger mServerMessenger;
    // 1. define ServiceConnection instance
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServerMessenger = new Messenger(service); // get the Messenger representing the server used by client here
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServerMessenger = null;
        }
    };
    
    // 2. create Handler where we handle server's message via overriding handleMessage()
    ClientHandler clientHandler = new ClientHandler();
    // 3. create Messenger representing the client used by server
    mClientMessenger = new Messenger(clientHandler);
    // 4. bindService
    Intent intent = getExplicitIntent(this, new Intent("cn.ben.countingService"));
    bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
```
**Note**: from Android 5.0 later, the intent used to start a service must be explicit.
```java
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
```
