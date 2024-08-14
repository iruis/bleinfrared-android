package net.iruis.android.bleinfrared.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.iruis.android.bleinfrared.InfraredSpec
import net.iruis.android.bleinfrared.databinding.FragmentTvBinding
import net.iruis.android.bleinfrared.generator.TvGenerator
import net.iruis.android.bleinfrared.listener.InfraredDataListener
import net.iruis.android.bleinfrared.model.MainViewModel
import net.iruis.android.bleinfrared.model.TvModel
import net.iruis.android.bleinfrared.service.BluetoothUartService

class TvFragment : Fragment() {
    companion object {
        const val TAG = "TvFragment"

        @JvmStatic
        fun newInstance() = TvFragment()
    }

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentTvBinding
    private lateinit var connection: ServiceConnection
    private lateinit var localService: BluetoothUartService
    private lateinit var tvModel: TvModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        connection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
                localService = (service as BluetoothUartService.LocalBinder).getService()
            }

            override fun onServiceDisconnected(className: ComponentName?) {
            }
        }
        tvModel = TvModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.liveEnabled.observe(viewLifecycleOwner) {
            tvModel.enabled = it
        }

        binding = FragmentTvBinding.inflate(inflater, container, false)
        binding.model = tvModel
        binding.generator = TvGenerator(object : InfraredDataListener {
            override fun onInfraredData(data: ByteArray, spec: InfraredSpec) {
                localService.send(data, spec)
            }
        })

        requireActivity().bindService(
            Intent(requireActivity(), BluetoothUartService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )

        return binding.root
    }
}