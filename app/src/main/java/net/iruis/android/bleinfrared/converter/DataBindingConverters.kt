package net.iruis.android.bleinfrared.converter

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.slider.Slider
import net.iruis.android.bleinfrared.R
import net.iruis.android.bleinfrared.model.AirConditionerModel

class DataBindingConverters {
    companion object {
        @JvmStatic
        fun convertIntToString(value: Int): String {
            return value.toString()
        }

        @JvmStatic
        @BindingAdapter("checkedButtonAttrChanged")
        fun setMaterialButtonToggleGroupListeners(
            view: MaterialButtonToggleGroup,
            attrChange: InverseBindingListener
        ) {
            view.addOnButtonCheckedListener { _, _, _ ->
                attrChange.onChange()
            }
        }

        @JvmStatic
        @BindingAdapter("checkedButton")
        fun convertAirConditionerModeToId(
            view: MaterialButtonToggleGroup,
            value: AirConditionerModel.Mode
        ) {
            when (value) {
                AirConditionerModel.Mode.Fan -> view.check(R.id.ac_mode_fan)
                AirConditionerModel.Mode.Cool -> view.check(R.id.ac_mode_cool)
                AirConditionerModel.Mode.Dry -> view.check(R.id.ac_mode_dry)
                AirConditionerModel.Mode.Ai -> view.check(R.id.ac_mode_ai)
            }
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "checkedButton")
        fun convertIdToAirConditionerMode(view: MaterialButtonToggleGroup): AirConditionerModel.Mode {
            when (view.checkedButtonId) {
                R.id.ac_mode_fan -> return AirConditionerModel.Mode.Fan
                R.id.ac_mode_cool -> return AirConditionerModel.Mode.Cool
                R.id.ac_mode_dry -> return AirConditionerModel.Mode.Dry
                R.id.ac_mode_ai -> return AirConditionerModel.Mode.Ai
            }
            return AirConditionerModel.Mode.Cool
        }

        @JvmStatic
        @BindingAdapter("checkedButton")
        fun convertAirConditionFanSpeedToId(
            view: MaterialButtonToggleGroup,
            value: AirConditionerModel.FanSpeed
        ) {
            when (value) {
                AirConditionerModel.FanSpeed.Low -> view.check(R.id.ac_fan_speed_low)
                AirConditionerModel.FanSpeed.Medium -> view.check(R.id.ac_fan_speed_medium)
                AirConditionerModel.FanSpeed.High -> view.check(R.id.ac_fan_speed_high)
                AirConditionerModel.FanSpeed.Nature -> view.check(R.id.ac_fan_speed_nature)
            }
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "checkedButton")
        fun convertIdToAirConditionFanSpeed(view: MaterialButtonToggleGroup): AirConditionerModel.FanSpeed {
            when (view.checkedButtonId) {
                R.id.ac_fan_speed_low -> return AirConditionerModel.FanSpeed.Low
                R.id.ac_fan_speed_medium -> return AirConditionerModel.FanSpeed.Medium
                R.id.ac_fan_speed_high -> return AirConditionerModel.FanSpeed.High
                R.id.ac_fan_speed_nature -> return AirConditionerModel.FanSpeed.Nature
            }
            return AirConditionerModel.FanSpeed.Low
        }

        @JvmStatic
        @BindingAdapter("android:valueAttrChanged")
        fun setSliderListeners(view: Slider, attrChange: InverseBindingListener) {
            view.addOnChangeListener { _, _, _ ->
                attrChange.onChange()
            }
        }

        @JvmStatic
        @BindingAdapter("android:value")
        fun convertIntToFloat(view: Slider, value: Int) {
            view.value = value.toFloat()
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "android:value")
        fun convertFloatToInt(view: Slider): Int {
            return view.value.toInt()
        }
    }
}