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
    
    // 2. create Handler instance where we handle server's message via overriding handleMessage()
    ClientHandler clientHandler = new ClientHandler();
    // 3. create Messenger instance representing the client used by server
    mClientMessenger = new Messenger(clientHandler);
    // 4. bindService
    Intent intent = getExplicitIntent(this, new Intent("cn.ben.countingService"));
    bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
```
**Note**: From Android 5.0, the `Intent` used to start a `Service` must be explicit.
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
Assign `mClientMessenger` we created before to `Message.replyTo` when we send message to server at the first time. And remember to call `unBind` in an appropriate way.
```java
    Message message = Message.obtain();
    message.what = ...;
    message.replyTo = mClientMessenger;
    
    try {
        // 5. send message to server
        mServerMessenger.send(message);
    } catch (RemoteException e) {
        e.printStackTrace();
    }
```
## Server (Usually A `Service` instance)
```java
    private Messenger mClientMessenger;
    private final Messenger mServerMessenger;
    
    public MyService() {
        // initialize messenger representing server used by client
        ServerHandler serverHandler = new ServerHandler();
        mServerMessenger = new Messenger(serverHandler);
    }
    
    mClientMessenger.send(message); // send message to client
    
    @Override
    public IBinder onBind(Intent intent) {
        // return messenger's binder here
        return mServerMessenger.getBinder();
    }
    
    private class ServerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // get the messenger representing the client here
            mClientMessenger = msg.replyTo;
            super.handleMessage(msg);
        }
    }
```
