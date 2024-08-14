package net.iruis.android.bleinfrared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private var mutableEnabled = MutableLiveData<Boolean>()

    val liveEnabled: LiveData<Boolean>
        get() {
            return mutableEnabled
        }

    var enabled: Boolean
        get() {
            return mutableEnabled.value ?: false
        }
        set(value) {
            if (mutableEnabled.value != value) {
                mutableEnabled.postValue(value)
            }
        }
}