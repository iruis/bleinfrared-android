package net.iruis.android.bleinfrared.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.iruis.android.bleinfrared.BluetoothUartService
import net.iruis.android.bleinfrared.InfraredDataListener
import net.iruis.android.bleinfrared.InfraredSpec
import net.iruis.android.bleinfrared.MainViewModel
import net.iruis.android.bleinfrared.databinding.FragmentAirConditionerBinding
import net.iruis.android.bleinfrared.generator.AirConditionerGenerator
import net.iruis.android.bleinfrared.model.AirConditionerModel
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class AirConditionerFragment : Fragment() {
    companion object {
        const val TAG = "AirConditionerFragment"

        @JvmStatic
        fun newInstance() = AirConditionerFragment()
    }

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentAirConditionerBinding
    private lateinit var connection: ServiceConnection
    private lateinit var localService: BluetoothUartService
    private lateinit var airConditionerModel: AirConditionerModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        connection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
                localService = (service as BluetoothUartService.LocalBinder).getService()
            }

            override fun onServiceDisconnected(className: ComponentName?) {
            }
        }
        airConditionerModel = AirConditionerModel(
            requireContext().getSharedPreferences(
                "${requireContext().packageName}_preferences",
                Context.MODE_PRIVATE
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.liveEnabled.observe(viewLifecycleOwner) {
            airConditionerModel.enabled = it
        }

        binding = FragmentAirConditionerBinding.inflate(inflater, container, false)
        binding.model = airConditionerModel
        binding.generator = AirConditionerGenerator(object : InfraredDataListener {
            override fun onInfraredData(data: ByteArray, spec: InfraredSpec) {
                transmitInfraredCommand(data, spec)
            }
        })

        requireActivity().bindService(
            Intent(requireActivity(), BluetoothUartService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )

        return binding.root
    }

    // https://m.blog.naver.com/chandong83/221517795944
    // https://github.com/Arduino-IRremote/Arduino-IRremote/blob/master/src/ir_LG.hpp
    private fun transmitInfraredCommand(data: ByteArray, spec: InfraredSpec) {
        Log.i(TAG, "transmitCoolCommand")

        // JSON 형식으로 만들었으나 데이터가 길어지면 송신시간이 오래걸림
        val stream = ByteArrayOutputStream()
        val writer = DataOutputStream(stream)

        writer.writeShort(BluetoothUartService.CONTROLLER_MAGIC)
        writer.writeBoolean(spec.lsb)
        writer.writeBoolean(spec.nibble)
        writer.writeInt(spec.leadMark)
        writer.writeInt(spec.leadSpace)
        writer.writeInt(spec.oneMark)
        writer.writeInt(spec.oneSpace)
        writer.writeInt(spec.zeroMark)
        writer.writeInt(spec.zeroSpace)
        writer.writeInt(spec.endMark)
        // end, END 신호 후 IR은 OFF 상태로 유지가 되므로 - 신호의 시간은 의미가 없음
        writer.writeInt(spec.endSpace)

        val dataPut: (Byte) -> Unit = { value ->
            writer.writeByte(value.toInt())
        }
        data.forEach { dataPut(it) }

        writer.flush()

        localService.send(stream.toByteArray())
    }
}