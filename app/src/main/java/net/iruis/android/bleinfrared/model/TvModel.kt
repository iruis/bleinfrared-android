package net.iruis.android.bleinfrared.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import net.iruis.android.bleinfrared.BR

class TvModel : BaseObservable() {
    @get:Bindable
    var enabled = false
        set(value) {
            if (field != value) {
                field = value

                notifyPropertyChanged(BR.enabled)
            }
        }
}