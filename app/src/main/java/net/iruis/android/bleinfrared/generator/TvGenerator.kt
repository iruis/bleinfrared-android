package net.iruis.android.bleinfrared.generator

import net.iruis.android.bleinfrared.InfraredDataListener
import net.iruis.android.bleinfrared.InfraredSpec

class TvGenerator(private val listener: InfraredDataListener) {
    private var spec = InfraredSpec()

    init {
        spec.apply {
            lsb = true
            nibble = false
            leadMark = 4400
            leadSpace = 4400
            oneMark = 550
            oneSpace = 1650
            zeroMark = 550
            zeroSpace = 550
            endMark = 550
            endSpace = 0
        }
    }

    private fun generate(data: Int): ByteArray {
        val array = mutableListOf<Byte>(0x07, 0x07)
        array.add((data shr 0).toByte())
        array.add((data shr 8).toByte())
        return array.toByteArray()
    }

    fun power() {
        listener.onInfraredData(generate(0xFD02), spec)
    }

    fun menu() {
        listener.onInfraredData(generate(0xE51A), spec)
    }

    fun up() {
        listener.onInfraredData(generate(0x9F60), spec)
    }

    fun down() {
        listener.onInfraredData(generate(0x9E61), spec)
    }

    fun left() {
        listener.onInfraredData(generate(0x9A65), spec)
    }

    fun right() {
        listener.onInfraredData(generate(0x9D62), spec)
    }

    fun ok() {
        listener.onInfraredData(generate(0x9768), spec)
    }

    fun back() {
        listener.onInfraredData(generate(0xA758), spec)
    }

    fun exit() {
        listener.onInfraredData(generate(0xD22D), spec)
    }

    fun mute() {
        listener.onInfraredData(generate(0xF00F), spec)
    }

    fun channelUp() {
        listener.onInfraredData(generate(0xED12), spec)
    }

    fun channelDown() {
        listener.onInfraredData(generate(0xEF10), spec)
    }

    fun volumeUp() {
        listener.onInfraredData(generate(0xF807), spec)
    }

    fun volumeDown() {
        listener.onInfraredData(generate(0xF40B), spec)
    }

    fun externalInput() {
        listener.onInfraredData(generate(0xFE01), spec)
    }
}