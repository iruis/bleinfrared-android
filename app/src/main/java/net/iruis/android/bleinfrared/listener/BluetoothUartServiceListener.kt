package net.iruis.android.bleinfrared.listener

interface BluetoothUartServiceListener {
    fun requestPermissions(permissions: Array<String>)
    fun connected()
    fun disconnected()
}