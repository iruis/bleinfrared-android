package net.iruis.android.bleinfrared

import android.Manifest
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import net.iruis.android.bleinfrared.Consts.Companion.CLIENT_UUID
import net.iruis.android.bleinfrared.Consts.Companion.RX_UUID
import net.iruis.android.bleinfrared.Consts.Companion.TX_UUID
import net.iruis.android.bleinfrared.Consts.Companion.UART_UUID

class BluetoothUartGattCallback(
    private val context: Context, private val listener: BluetoothUartConnectionListener
) : BluetoothGattCallback() {
    companion object {
        const val TAG = "BluetoothUartGattCallback"
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        Log.i(TAG, "onConnectionStateChange")

        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        if (status == BluetoothGatt.GATT_FAILURE) {
            Log.i(TAG, "onConnectionStateChange == GATT_FAILURE")
            //return
        }
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Log.i(TAG, "onConnectionStateChange != GATT_SUCCESS")
            //return
        }

        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i(TAG, "onConnectionStateChange CONNECTED")
            gatt?.discoverServices()
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.i(TAG, "onConnectionStateChange STATE_DISCONNECTED")
            listener.disconnected()
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        super.onServicesDiscovered(gatt, status)
        Log.i(TAG, "onServicesDiscovered")

        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        if (status != BluetoothGatt.GATT_SUCCESS) {
            return
        }

        val gattService = gatt!!.getService(UART_UUID)
        val bluetoothTx = gattService.getCharacteristic(TX_UUID)
        val bluetoothRx = gattService.getCharacteristic(RX_UUID)
        gatt.setCharacteristicNotification(bluetoothRx, true)

        val descriptor = bluetoothRx.getDescriptor(CLIENT_UUID)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            @Suppress("DEPRECATION")
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            @Suppress("DEPRECATION")
            gatt.writeDescriptor(descriptor)
        } else {
            gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        }
        listener.connected(gatt, bluetoothRx, bluetoothTx)
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray,
        status: Int
    ) {
        super.onCharacteristicRead(gatt, characteristic, value, status)
        Log.i(TAG, "onCharacteristicRead")
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int
    ) {
        super.onCharacteristicWrite(gatt, characteristic, status)
        Log.i(TAG, "onCharacteristicWrite")
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray
    ) {
        super.onCharacteristicChanged(gatt, characteristic, value)
        Log.i(TAG, "onCharacteristicChanged")
    }

    override fun onDescriptorRead(
        gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int, value: ByteArray
    ) {
        super.onDescriptorRead(gatt, descriptor, status, value)
        Log.i(TAG, "onDescriptorRead")
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int
    ) {
        super.onDescriptorWrite(gatt, descriptor, status)
        Log.i(TAG, "onDescriptorWrite")
    }
}