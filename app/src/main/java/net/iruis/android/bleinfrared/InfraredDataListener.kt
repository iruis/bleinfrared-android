package net.iruis.android.bleinfrared

interface InfraredDataListener {
    fun onInfraredData(data: ByteArray, spec: InfraredSpec)
}
