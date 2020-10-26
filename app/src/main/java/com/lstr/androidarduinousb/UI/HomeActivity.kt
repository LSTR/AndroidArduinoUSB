package com.lstr.androidarduinousb.UI

import android.hardware.usb.UsbDevice
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lstr.androidarduinousb.R
import com.lstr.androidarduinousb.databinding.ActivityMainBinding
import me.aflak.arduino.Arduino
import me.aflak.arduino.ArduinoListener

class HomeActivity : AppCompatActivity(), ArduinoListener {

    private lateinit var arduino:Arduino
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        setup()
    }

    private fun setup() {
        arduino = Arduino(this)
        binding.btnTest.setOnClickListener {
            val message = binding.input.text.toString()
            arduino.send(message.toByteArray())
            binding.input.setText("")
        }
        binding.turnLed.setOnCheckedChangeListener { compoundButton, b ->
            val message = if (compoundButton.isChecked) "YES" else "NO"
            arduino.send(message.toByteArray())
        }
    }

    override fun onStart() {
        super.onStart()
        arduino.setArduinoListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        arduino.unsetArduinoListener()
        arduino.close()
    }

    override fun onArduinoAttached(device: UsbDevice?) {
        display("arduino attached...")
        arduino.open(device)
    }

    override fun onArduinoDetached() {
        display("arduino detached.")
    }

    override fun onArduinoMessage(bytes: ByteArray) {
        display(String(bytes))
    }

    override fun onArduinoOpened() {
        val str = "arduino opened..."
        display(str)
    }

    private fun display(message: String) {
        runOnUiThread { binding.message.append("$message\n") }
    }
}