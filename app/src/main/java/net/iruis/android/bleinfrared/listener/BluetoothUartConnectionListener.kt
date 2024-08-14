package net.iruis.android.bleinfrared.listener

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic

interface BluetoothUartConnectionListener {
    fun connected(
        gatt: BluetoothGatt,
        gattRx: BluetoothGattCharacteristic,
        gattTx: BluetoothGattCharacteristic
    )

    fun disconnected()
}