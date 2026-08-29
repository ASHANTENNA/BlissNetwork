package com.thenew.blissnetwork;

import android.service.quicksettings.TileService;
import android.service.quicksettings.Tile;

public class BluetoothFixTile extends TileService {
    
    @Override
    public void onStartListening() {
        super.onStartListening();
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setState(Tile.STATE_ACTIVE);
            tile.updateTile();
        }
    }

    @Override
    public void onClick() {
        super.onClick();

        // Bash script to check state and toggle accordingly
        final String toggleScript = 
            "STATE=$(settings get global bluetooth_on)\n" +
            "if [ \"$STATE\" -eq \"1\" ]; then\n" +
            "    svc bluetooth disable\n" +
            "    hciconfig hci0 down\n" +
            "else\n" +
            "    hciconfig hci0 down\n" +
            "    pm disable com.android.bluetooth\n" +
            "    sleep 1\n" +
            "    pm enable com.android.bluetooth\n" +
            "    sleep 1\n" +
            "    svc bluetooth enable\n" +
            "    sleep 1\n" +
            "    hciconfig hci0 up\n" +
            "fi";

        // Run the script in a background thread to prevent UI freezing
        new Thread(new Runnable(){
            @Override
            public void run(){
                RootUtil.execute(toggleScript);
            }
        }).start();
    }
}

