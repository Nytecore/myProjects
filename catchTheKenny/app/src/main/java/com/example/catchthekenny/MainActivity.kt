package com.example.catchthekenny

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.catchthekenny.databinding.ActivityMainBinding
import kotlinx.coroutines.Runnable
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding           //ViewBinding
    private var isGameRunning = true                            // Game is running?
    private var runnable : Runnable = Runnable {  }             // Runnable interface
    private var handler = Handler(Looper.getMainLooper())       // Handler Class
    private var score = 0                                       // Score
    private lateinit var aDialog: AlertDialog.Builder           // AlertDialog = Restart game
    private lateinit var bDialog: AlertDialog.Builder           // AlertDialog = Quit game


        // Coordinate calculation - image redrawing and move Kenny
    private fun moveKenny() {
        runnable = object : Runnable {
            override fun run() {
                val layoutWidth = binding.kennyLayout.width
                val layoutHeight = binding.kennyLayout.height

                val kennyWidth = binding.kennyImage.width
                val kennyHeight = binding.kennyImage.height

                val randomX = Random.nextInt(0, maxOf(1, layoutWidth - kennyWidth)).toFloat()
                val randomY = Random.nextInt(0, maxOf(1, layoutHeight - kennyHeight)).toFloat()

                binding.kennyImage.x = randomX
                binding.kennyImage.y = randomY

                handler.postDelayed(this, 500)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

            //ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

            // AlertDialogs initialize...
        aDialog = AlertDialog.Builder(this)
        bDialog = AlertDialog.Builder(this)



            // Start moving Kenny
        moveKenny()
        handler.post(runnable)

            // Kenny's image clicker listener here
        binding.kennyImage.setOnClickListener {
            if (isGameRunning) {
                score++
                binding.scoreTextView.text = "Score: $score"
            }
        }


            // COUNTDOWN ---> The CountDownTimer function is used here.
        object : CountDownTimer(15000 , 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(p0: Long) {
                binding.timeTextView.text = "Time: ${p0 / 1000}"
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                handler.removeCallbacks(runnable)           // Stop Kenny
                isGameRunning = false
                binding.timeTextView.text = "Time Over"     // Time Over


                    // The first AlertDialog here -->
                aDialog.setTitle("Game Over")
                aDialog.setMessage("Play again?")
                aDialog.setPositiveButton("YES" , DialogInterface.OnClickListener {dialogInterface, i ->
                    restartGame()
                })
                aDialog.setNegativeButton("NO" , DialogInterface.OnClickListener {dialogInterface, i ->
                    quitGame()
                } )
                aDialog.show()

            }

        }.start()
    }

            // Restart Game Function
    @SuppressLint("SetTextI18n")
    private fun restartGame() {
        score = 0
        binding.scoreTextView.text = "Score: 0"
        isGameRunning = true

                // Start moving Kenny
        moveKenny()
        handler.post(runnable)

                // Kenny's image clicker listener here
        binding.kennyImage.setOnClickListener {
            if (isGameRunning) {
                score++
                binding.scoreTextView.text = "Score: $score"
            }
        }

                // COUNTDOWN ---> The CountDownTimer function is used here.
        object : CountDownTimer(15000 , 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(p0: Long) {
                binding.timeTextView.text = "Time: ${p0 / 1000}"
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                handler.removeCallbacks(runnable)           // Stop Kenny
                isGameRunning = false
                binding.timeTextView.text = "Time Over"     // Time Over


                // AlertDialog Here -->
                aDialog.setTitle("Game Over")
                aDialog.setMessage("Play again?")
                aDialog.setPositiveButton("YES" , DialogInterface.OnClickListener {dialogInterface, i ->
                    restartGame()
                })
                aDialog.setNegativeButton("NO" , DialogInterface.OnClickListener {dialogInterface, i ->
                    quitGame()
                } )
                aDialog.show()

            }
        }.start()

    }

            // Quit Game function here
    private fun quitGame() {

        bDialog.setTitle("GAME OVER")
        bDialog.setMessage("Your score: $score")
        bDialog.setNegativeButton("EXIT" , DialogInterface.OnClickListener {dialogInterface, i ->
            System.exit(0)
        } )
        bDialog.show()
    }
}