package net.iruis.android.bleinfrared

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import net.iruis.android.bleinfrared.adapter.DeviceViewPager2Adapter
import net.iruis.android.bleinfrared.databinding.ActivityMainBinding
import net.iruis.android.bleinfrared.model.AirConditionerModel

// reference
// https://codereview.qt-project.org/c/qt/qtconnectivity/+/453298/15/src/android/bluetooth/src/org/qtproject/qt/android/bluetooth/QtBluetoothGattCharacteristic.java#26
// https://github.com/NordicSemiconductor/Android-nRF-Toolbox/blob/main/profile_uart/src/main/java/no/nordicsemi/android/uart/UartServer.kt
// https://github.com/adafruit/Adafruit_Android_BLE_UART/blob/master/app/src/main/java/com/adafruit/bleuart/BluetoothLeUart.java
class MainActivity : AppCompatActivity(), BluetoothUartServiceListener {
    companion object {
        const val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by viewModels()
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            val binder = service as BluetoothUartService.LocalBinder

            localService = binder.getService()
            localService!!.setBluetoothUartServiceListener(this@MainActivity)

            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                localService?.resume()
            }
        }

        override fun onServiceDisconnected(className: ComponentName?) {
        }
    }
    private var localService: BluetoothUartService? = null

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var devicePagerAdapter: DeviceViewPager2Adapter
    private lateinit var airConditionerModel: AirConditionerModel

    private fun bluetoothPermissionRequest(requestPermissions: Array<String>) {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (requestPermissions.all { !permissions.getOrDefault(it, false) }) {
                val snackbar = Snackbar.make(binding.root, "권한이 거부되었습니다", Snackbar.LENGTH_LONG)
                snackbar.setAction(android.R.string.ok) {
                    snackbar.dismiss()
                }
                snackbar.show();
            } else {
                localService?.resume()
            }
        }.launch(requestPermissions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences("${packageName}_preferences", Context.MODE_PRIVATE)
        devicePagerAdapter = DeviceViewPager2Adapter(supportFragmentManager, lifecycle)
        airConditionerModel = AirConditionerModel(preferences)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.viewPager2.adapter = devicePagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = devicePagerAdapter.getPageTitle(position)
        }.attach()

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        Log.i(TAG, "onStart")
        super.onStart()

        Intent(this, BluetoothUartService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        Log.i(TAG, "onStop")
        super.onStop()

        unbindService(connection)

        viewModel.enabled = false
    }

    override fun onResume() {
        Log.i(TAG, "onResume")
        super.onResume()

        localService?.resume()
    }

    override fun onPause() {
        Log.i(TAG, "onPause")
        super.onPause()

        localService?.paused()
    }

    override fun requestPermissions(permissions: Array<String>) {
        bluetoothPermissionRequest(permissions)
    }

    override fun connected() {
        runOnUiThread {
            viewModel.enabled = true
        }
    }

    override fun disconnected() {
        runOnUiThread {
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                localService?.resume()
            }
            viewModel.enabled = false
        }
    }
}
