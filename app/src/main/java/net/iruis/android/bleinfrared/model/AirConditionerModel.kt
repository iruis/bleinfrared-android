package net.iruis.android.bleinfrared.model

import android.content.SharedPreferences
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import net.iruis.android.bleinfrared.BR

class AirConditionerModel(private val preferences: SharedPreferences) : BaseObservable() {
    companion object {
        private const val KEY_MODE = "ac_mode"
        private const val KEY_FAN_SPEED = "ac_fan_speed"
        private const val KEY_TEMPERATURE = "ac_temperature"
        private const val KEY_AI = "ac_ai"

        const val TEMPERATURE_MIN: Int = 18
        const val TEMPERATURE_MAX: Int = 30
    }

    enum class Mode(val value: Int) {
        Fan(0), Cool(1), Dry(2), Ai(3);
    }

    enum class FanSpeed(val value: Int) {
        Low(0), Medium(2), High(4), Nature(5);
    }

    enum class Ai(val value: Int) {
        N2(0), N1(1), Zero(2), P1(3), P2(4);
    }

    @get:Bindable
    var enabled = false
        set(value) {
            if (field != value) {
                field = value

                notifyPropertyChanged(BR.enabled)
            }
        }

    @get:Bindable
    var mode = Mode.Cool
        set(value) {
            if (field != value) {
                field = value

                val edit = preferences.edit()
                edit.putInt(KEY_MODE, value.value)
                edit.apply()

                notifyPropertyChanged(BR.mode)
            }
        }

    @get:Bindable
    var fanSpeed = FanSpeed.Medium
        set(value) {
            if (field != value) {
                field = value

                val edit = preferences.edit()
                edit.putInt(KEY_FAN_SPEED, value.value)
                edit.apply()

                notifyPropertyChanged(BR.fanSpeed)
            }
        }

    @get:Bindable
    var ai = Ai.Zero
        set(value) {
            if (field != value) {
                field = value

                val edit = preferences.edit()
                edit.putInt(KEY_AI, value.value)
                edit.apply()

                notifyPropertyChanged(BR.ai)
            }
        }

    @get:Bindable
    var temperature: Int = 27
        set(value) {
            if (field != value) {
                field = value

                val edit = preferences.edit()
                edit.putInt(KEY_TEMPERATURE, value)
                edit.apply()

                notifyPropertyChanged(BR.temperature)
            }
        }

    fun temperatureDecrement() {
        temperature = TEMPERATURE_MIN.coerceAtLeast(temperature - 1)
    }

    fun temperatureIncrement() {
        temperature = TEMPERATURE_MAX.coerceAtMost(temperature + 1)
    }

    init {
        try {
            mode = Mode.valueOf(preferences.getString(KEY_MODE, mode.name) ?: mode.name)
            fanSpeed = FanSpeed.valueOf(
                preferences.getString(KEY_FAN_SPEED, fanSpeed.name) ?: fanSpeed.name
            )
            temperature = preferences.getInt(KEY_TEMPERATURE, 27)
            ai = Ai.valueOf(preferences.getString(KEY_AI, ai.name) ?: ai.name)
        } catch (_: Exception) {
            val edit = preferences.edit()
            edit.putString(KEY_MODE, mode.name)
            edit.putString(KEY_FAN_SPEED, fanSpeed.name)
            edit.putInt(KEY_TEMPERATURE, temperature)
            edit.putString(KEY_AI, ai.name)
            edit.apply()
        }
    }
}
