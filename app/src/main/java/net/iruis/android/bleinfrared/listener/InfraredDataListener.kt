package net.iruis.android.bleinfrared.listener

import net.iruis.android.bleinfrared.InfraredSpec

interface InfraredDataListener {
    fun onInfraredData(data: ByteArray, spec: InfraredSpec)
}
