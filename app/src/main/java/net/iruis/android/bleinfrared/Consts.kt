package net.iruis.android.bleinfrared

import java.util.UUID

class Consts {
    companion object {
        val UART_UUID: UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
        val TX_UUID: UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")
        val RX_UUID: UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")
        val CLIENT_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}