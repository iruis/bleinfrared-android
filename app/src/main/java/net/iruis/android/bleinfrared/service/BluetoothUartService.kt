package net.iruis.android.bleinfrared.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import net.iruis.android.bleinfrared.InfraredSpec
import net.iruis.android.bleinfrared.callback.BluetoothUartGattCallback
import net.iruis.android.bleinfrared.listener.BluetoothUartConnectionListener
import net.iruis.android.bleinfrared.listener.BluetoothUartServiceListener
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class BluetoothUartService : Service() {
    companion object {
        const val TAG = "BluetoothUartService"

        const val CONTROLLER_MAGIC: Int = 0xCAFE
        const val CONTROLLER_NAME: String = "Geekble Mini"

        val BLUETOOTH_PERMISSIONS =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) arrayOf(Manifest.permission.BLUETOOTH_ADMIN) else arrayOf(
                Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT
            )
    }

    private val binder = LocalBinder()
    private var listener: BluetoothUartServiceListener? = null

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothScanner: BluetoothLeScanner
    private lateinit var bluetoothScanCallback: ScanCallback

    private var gatt: BluetoothGatt? = null
    private var gattTx: BluetoothGattCharacteristic? = null

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothUartService = this@BluetoothUartService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()

        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothScanner = bluetoothAdapter.bluetoothLeScanner

        bluetoothScanCallback = object : ScanCallback() {
            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                Log.i(TAG, "onScanResult")
                if (result != null) {
                    connectDevice(result.device)
                }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                Log.i(TAG, "onBatchScanResults")
            }

            override fun onScanFailed(errorCode: Int) {
                Log.i(TAG, "onScanFailed")
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()

        bluetoothScanner.stopScan(bluetoothScanCallback)
    }

    fun setBluetoothUartServiceListener(listener: BluetoothUartServiceListener) {
        this.listener = listener
    }

    private fun bluetoothScan() {
        Log.i(TAG, "bluetoothScan")
        if (BLUETOOTH_PERMISSIONS.all { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }) {
            return
        }
        bluetoothScanner.startScan(
            listOf(ScanFilter.Builder().setDeviceName(CONTROLLER_NAME).build()),
            ScanSettings.Builder().setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).build(),
            bluetoothScanCallback
        )
    }

    @SuppressLint("MissingPermission")
    fun connectDevice(device: BluetoothDevice) {
        Log.i(TAG, "connectDevice")
        device.connectGatt(
            this,
            false,
            BluetoothUartGattCallback(this, object : BluetoothUartConnectionListener {
                override fun connected(
                    gatt: BluetoothGatt,
                    gattRx: BluetoothGattCharacteristic,
                    gattTx: BluetoothGattCharacteristic
                ) {
                    Log.i(TAG, "connected")

                    this@BluetoothUartService.gatt = gatt
                    this@BluetoothUartService.gattTx = gattTx

                    listener?.connected()
                }

                override fun disconnected() {
                    Log.i(TAG, "disconnected")
                    listener?.disconnected()

                    gatt = null
                    gattTx = null
                }
            })
        )
    }

    private fun generatePacket(data: ByteArray, spec: InfraredSpec): ByteArray {
        val stream = ByteArrayOutputStream()
        val writer = DataOutputStream(stream)

        writer.writeShort(CONTROLLER_MAGIC)
        writer.writeBoolean(spec.lsb)
        writer.writeBoolean(spec.nibble)
        writer.writeInt(spec.leadMark)
        writer.writeInt(spec.leadSpace)
        writer.writeInt(spec.oneMark)
        writer.writeInt(spec.oneSpace)
        writer.writeInt(spec.zeroMark)
        writer.writeInt(spec.zeroSpace)
        writer.writeInt(spec.endMark)
        writer.writeInt(spec.endSpace)

        val dataPut: (Byte) -> Unit = { value ->
            writer.writeByte(value.toInt())
        }
        data.forEach { dataPut(it) }

        writer.flush()

        return stream.toByteArray()
    }

    @SuppressLint("MissingPermission")
    fun send(data: ByteArray, spec: InfraredSpec) {
        val gatt = this.gatt
        val gattTx = this.gattTx
        if (gatt == null || gattTx == null) {
            return
        }

        val packet = generatePacket(data, spec)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            gattTx.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            @Suppress("DEPRECATION")
            gattTx.value = packet
            @Suppress("DEPRECATION")
            gatt.writeCharacteristic(gattTx)
        } else {
            gatt.writeCharacteristic(
                gattTx, packet, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            )
        }
    }

    fun resume() {
        if (BLUETOOTH_PERMISSIONS.all { application.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }) {
            listener?.requestPermissions(BLUETOOTH_PERMISSIONS)
        } else {
            bluetoothScan()
        }
    }

    @SuppressLint("MissingPermission")
    fun paused() {
        gatt?.disconnect()
        gatt?.close()

        gatt = null
        gattTx = null
    }
}