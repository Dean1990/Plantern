package com.deanlib.plantern.utils.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.IntentFilter;

/**
 * 
 */
public class BluetoothManager {

    private static BluetoothManager INSTANCE;

    public static synchronized BluetoothManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BluetoothManager();
        }
        return INSTANCE;
    }

    BluetoothReceiver BluetoothReceiver;


    /**
     * 注册广播接收器，用于监听蓝牙状态变化
     * 需要在 manifests 文件中 注册广播接收者 com.deanlib.plantern.utils.bluetooth.BluetoothReceiver
     * @param activity
     * @param listener
     */
    public void registerBluetoothReceiver(Activity activity, BluetoothReceiver.OnBlueToothStateListener listener) {
        //注册广播，蓝牙状态监听
        BluetoothReceiver = new BluetoothReceiver();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        activity.registerReceiver(BluetoothReceiver, filter);
        BluetoothReceiver.setOnBlueToothStateListener(listener);
    }

    public void unregisterBluetoothReceiver(Activity activity) {
        activity.unregisterReceiver(BluetoothReceiver);
    }
}
