package it.unipi.masss.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import it.unipi.masss.Action
import it.unipi.masss.R
import it.unipi.masss.ShakingDetector
import it.unipi.masss.Util.isServiceRunning
import it.unipi.masss.databinding.FragmentHomeBinding
import it.unipi.masss.recordingservice.RecordingService

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val alertStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateButtonColor(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val startMonBtn = view.findViewById<MaterialButton>(R.id.start_mon_btn)
        val manualSosBtn = view.findViewById<MaterialButton>(R.id.manual_sos_btn)

        if (requireContext().isServiceRunning(RecordingService::class.java))
            updateButtonColor()
        else
            updateButtonColor(true)

        startMonBtn.setOnClickListener {

            // Detect if the recording service is already running
            if (!requireContext().isServiceRunning(RecordingService::class.java)) {
                // Start Recording service
                Intent(context?.applicationContext, RecordingService::class.java).also {
                    it.action = Action.START_RECORDING.toString()
                    context?.applicationContext?.startService(it)
                }

                Intent(context?.applicationContext, ShakingDetector::class.java).also {
                    it.action = Action.START_SHAKING_DETECTION.toString()
                    context?.applicationContext?.startService(it)
                }

                updateButtonColor()
            } else {
                Intent(context?.applicationContext, RecordingService::class.java).also {
                    it.action = Action.STOP_RECORDING.toString()
                    context?.applicationContext?.startService(it)
                }

                Intent(context?.applicationContext, ShakingDetector::class.java).also {
                    it.action = Action.STOP_SHAKING_DETECTION.toString()
                    context?.applicationContext?.startService(it)
                }
                updateButtonColor(true)
            }
        }

        manualSosBtn.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.send_alert_confirmation_title))
                .setMessage(getString(R.string.send_alert_confirmation_text))
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    context?.sendBroadcast(Intent(Action.SEND_ALERT.toString()))
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(
            alertStateReceiver,
            IntentFilter(Action.SEND_ALERT.toString())
        )

        if (!requireContext().isServiceRunning(RecordingService::class.java))
            updateButtonColor(true)
        else
            updateButtonColor()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(alertStateReceiver)
    }

    private fun updateButtonColor(red: Boolean = false) {
        val colorStateList = if (red)
            ColorStateList.valueOf(Color.parseColor("#FF0000"))
        else
            ColorStateList.valueOf(Color.parseColor("#470000"))
        requireView().findViewById<MaterialButton>(R.id.start_mon_btn).backgroundTintList =
            colorStateList
    }



    companion object {
        private const val TAG = "HomeFragment"
    }
}