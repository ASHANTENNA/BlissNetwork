package com.thenew.blissnetwork;

import android.service.quicksettings.TileService;
import android.service.quicksettings.Tile;

public class WifiFixTile extends TileService {
    
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

        new Thread(new Runnable(){
            @Override
            public void run(){
                RootUtil.execute("killall wificond");
            }
        }).start();
    }
}

