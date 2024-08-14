package net.iruis.android.bleinfrared.generator

import net.iruis.android.bleinfrared.listener.InfraredDataListener
import net.iruis.android.bleinfrared.InfraredSpec
import net.iruis.android.bleinfrared.model.AirConditionerModel

class AirConditionerGenerator(private val listener: InfraredDataListener) {
    private var spec = InfraredSpec()

    init {
        // https://m.blog.naver.com/chandong83/221517795944
        // https://github.com/Arduino-IRremote/Arduino-IRremote/blob/master/src/ir_LG.hpp
        spec.apply {
            lsb = false
            nibble = true
            leadMark = 3100
            leadSpace = 9800
            oneMark = 500
            oneSpace = 1500
            zeroMark = 500
            zeroSpace = 550
            endMark = 500
            endSpace = 0
        }
    }

    fun common(model: AirConditionerModel, powerOn: Boolean) {
        when (model.mode) {
            AirConditionerModel.Mode.Fan -> fan(powerOn, model.fanSpeed)
            AirConditionerModel.Mode.Cool -> cool(powerOn, model.temperature, model.fanSpeed)
            AirConditionerModel.Mode.Dry -> dry(powerOn, model.fanSpeed)
            AirConditionerModel.Mode.Ai -> ai(powerOn, model.ai)
        }
    }

    fun powerOff() {
        val data = mutableListOf<Byte>(0x08, 0x08, 0x0c, 0x00, 0x00, 0x05)

        appendCrc(data)

        listener.onInfraredData(data.toByteArray(), spec)
    }

    fun turbo() {
        val data = mutableListOf<Byte>(0x08, 0x08, 0x01, 0x00, 0x00, 0x08)

        appendCrc(data)

        listener.onInfraredData(data.toByteArray(), spec)
    }

    fun energySaving(isOn: Boolean) {
        val data = mutableListOf<Byte>(0x08, 0x08, 0x01, 0x00, 0x00)

        data.add(if (isOn) 0x04 else 0x05)

        appendCrc(data)

        listener.onInfraredData(data.toByteArray(), spec)
    }

    fun swingVertical(isOn: Boolean) {
        val data = mutableListOf<Byte>(0x08, 0x08, 0x01, 0x03, 0x01)

        data.add(if (isOn) 0x04 else 0x05)

        appendCrc(data)

        listener.onInfraredData(data.toByteArray(), spec)
    }

    fun comfortAir(isOn: Boolean) {
        val data = mutableListOf<Byte>(0x08, 0x08, 0x01, 0x03, 0x00)

        data.add(if (isOn) 0x09 else 0x04)

        appendCrc(data)

        listener.onInfraredData(data.toByteArray(), spec)
    }

    private fun appendCrc(data: MutableList<Byte>) {
        var crc = 0
        data.forEach { crc += it }
        data.add((crc and 0x0f).toByte())
    }

    private fun fan(powerOn: Boolean, fanSpeed: AirConditionerModel.FanSpeed) {
        val data = mutableListOf<Byte>(0x08, 0x08, 0x00)
        val command = (0x02 + if (powerOn) 0x00 else 0x08).toByte()

        data.add(command)
        data.add(0x03)
        data.add(fanSpeed.value.toByte())

        appendCrc(data)

        listener.onInfraredData(data.toByteArray(), spec)
    }

    private fun cool(powerOn: Boolean, temperature: Int, fanSpeed: AirConditionerModel.FanSpeed) {
        if ((temperature < AirConditionerModel.TEMPERATURE_MIN) or (temperature > AirConditionerModel.TEMPERATURE_MAX)) {
            return
        }

        val data = mutableListOf<Byte>(0x08, 0x08, 0x00)
        val command = (0x00 + if (powerOn) 0x00 else 0x08).toByte()

        data.add(command)
        data.add((temperature - 15).toByte()) // 온도 값 - 15
        data.add(fanSpeed.value.toByte())

        appendCrc(data)

        listener.onInfraredData(data.toByteArray(), spec)
    }

    private fun dry(powerOn: Boolean, fanSpeed: AirConditionerModel.FanSpeed) {
        val data = mutableListOf<Byte>(0x08, 0x08, 0x00)
        val command = (0x01 + if (powerOn) 0x00 else 0x08).toByte()

        data.add(command)
        data.add(0x09)
        data.add(fanSpeed.value.toByte())

        appendCrc(data)

        listener.onInfraredData(data.toByteArray(), spec)
    }

    private fun ai(powerOn: Boolean, ai: AirConditionerModel.Ai) {
        val data = mutableListOf<Byte>(0x08, 0x08, 0x00)
        val command = (0x03 + if (powerOn) 0x00 else 0x08).toByte()

        data.add(command)
        data.add(if (powerOn) 0x02 else ai.value.toByte())
        data.add(0x05)

        appendCrc(data)

        listener.onInfraredData(data.toByteArray(), spec)
    }
}