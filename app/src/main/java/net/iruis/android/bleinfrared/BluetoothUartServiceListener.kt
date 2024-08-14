package net.iruis.android.bleinfrared

interface BluetoothUartServiceListener {
    fun requestPermissions(permissions: Array<String>)
    fun connected()
    fun disconnected()
}