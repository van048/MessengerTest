# Use Messenger to communicate between processes

## Client
```java
    // 1. create Handler
    ClientHandler clientHandler = new ClientHandler();
    // 2. create Messenger representing the client used by server
    mClientMessenger = new Messenger(clientHandler);
```
```java
    private Messenger mClientMessenger;
    private Messenger mServerMessenger;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
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
```
