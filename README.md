# Use Messenger to communicate between processes

## Client
```java
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
    // 2. create Handler
    ClientHandler clientHandler = new ClientHandler();
    // 3. create Messenger representing the client used by server
    mClientMessenger = new Messenger(clientHandler);
    // 4. bindService
    bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
```
