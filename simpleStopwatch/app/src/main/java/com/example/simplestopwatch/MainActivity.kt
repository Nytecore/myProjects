package com.example.simplestopwatch

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.simplestopwatch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var number = 0
    var runnable : Runnable = Runnable {  }
    var handler : Handler = Handler(Looper.getMainLooper())
    var arrayList = ArrayList<Int>()
    lateinit var arrayAdapter: ArrayAdapter<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun start(view: View){

        if (number != 0) {
            binding.pauseButton.isEnabled = true
            runnable = object : Runnable {
                @SuppressLint("SetTextI18n")
                override fun run() {
                    number += 1
                    binding.textView.text = "Time: $number sec"
                    handler.postDelayed(this, 1000)
                }
            }
            handler.post(runnable)
            binding.startButton.isEnabled = false
        } else {
            binding.stopButton.isEnabled = true
            binding.pauseButton.isEnabled = true
            number = 0
            runnable = object : Runnable {
                @SuppressLint("SetTextI18n")
                override fun run() {
                    number += 1
                    binding.textView.text = "Time: $number sec"
                    handler.postDelayed(this, 1000)
                }
            }
            handler.post(runnable)
            binding.startButton.isEnabled = false
        }
    }

    @SuppressLint("SetTextI18n")
    fun stop(view: View) {
        binding.stopButton.isEnabled = false
        binding.startButton.isEnabled = true
        binding.pauseButton.isEnabled = true
        number = 0
        binding.textView.text = "Time: 0"
        handler.removeCallbacks(runnable)
        binding.listView.adapter = null
        arrayList.clear()
    }

    @SuppressLint("SetTextI18n")
    fun pause(view: View) {
        binding.pauseButton.isEnabled = false
        binding.textView.text = "Time: $number sec"
        handler.removeCallbacks(runnable)
        binding.startButton.isEnabled = true
    }

    fun lap(view: View) {
        arrayList.add(number)

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList)
        binding.listView.adapter =  arrayAdapter
    }
}