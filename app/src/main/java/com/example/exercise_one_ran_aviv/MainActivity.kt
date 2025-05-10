package com.example.exercise_one_ran_aviv

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.example.exercise_one_ran_aviv.GameManager
import androidx.activity.enableEdgeToEdge
import android.os.Vibrator
import android.os.VibrationEffect
import android.widget.TextView
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager



class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var main_TXT_distance: TextView
    private val ROWS = 6
    private val COLS = 5




    private lateinit var main_TXT_score: TextView
    private lateinit var gameManager: GameManager
    private lateinit var cellViews: List<List<AppCompatImageView>>
    private lateinit var heartViews: List<AppCompatImageView>
    private lateinit var main_BTN_left: MaterialButton
    private lateinit var main_BTN_right: MaterialButton
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastMoveTime: Long = 0L
    private var useSensors: Boolean = false
    private var isFastMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        SignalManager.init(this)

        setContentView(R.layout.activity_main)
        useSensors = intent.getBooleanExtra("USE_SENSORS", false)
        isFastMode = intent.getBooleanExtra("IS_FAST_MODE", false)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gameManager = GameManager(ROWS, COLS)
        findViews()
        if (useSensors) {
            main_BTN_left.visibility = View.GONE
            main_BTN_right.visibility = View.GONE
        } else {
            main_BTN_left.visibility = View.VISIBLE
            main_BTN_right.visibility = View.VISIBLE
        }
        setupButtons()
        if (useSensors) {
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }

        lifecycleScope.launch {
            while (true) {
                val delayTime = if (isFastMode) 600L else 1500L
                delay(delayTime)
                val crashed = gameManager.updateGame()
                if (crashed) handleCrash2()
                updateUI()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0] // Left/Right tilt

            val currentTime = System.currentTimeMillis()
            if (currentTime - lastMoveTime > 400) { // Prevent spamming
                if (x > 3) {
                    gameManager.moveCarLeft()
                    lastMoveTime = currentTime
                } else if (x < -3) {
                    gameManager.moveCarRight()
                    lastMoveTime = currentTime
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // we don't care here
    }

    private fun findViews() {
        main_TXT_score = findViewById(R.id.main_TXT_score)
        main_TXT_distance = findViewById(R.id.main_TXT_distance)
        cellViews = List(ROWS) { row ->
            List(COLS) { col ->
                val id = resources.getIdentifier("cell_${row}_${col}", "id", packageName)
                findViewById(id)
            }
        }

        heartViews = listOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )

        main_BTN_left = findViewById(R.id.main_FAB_Left)
        main_BTN_right = findViewById(R.id.main_FAB_Right)
    }

    private fun setupButtons() {
        main_BTN_left.setOnClickListener {
            gameManager.moveCarLeft()
            updateUI()
        }

        main_BTN_right.setOnClickListener {
            gameManager.moveCarRight()
            updateUI()
        }
    }


    private fun handleCrash2()
    {
        SignalManager
            .getInstance()
            .toast("Crash")
        SignalManager
            .getInstance()
            .vibrate()




    }

//    private fun handleCrash() //old vibrate
//    {
//        Toast.makeText(this, "\uD83D\uDCA5 Crash!", Toast.LENGTH_SHORT).show()
//        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            vibrator.vibrate(
//                VibrationEffect.createOneShot(
//                    500,
//                    VibrationEffect.DEFAULT_AMPLITUDE
//                )
//            )
//        } else {
//            @Suppress("DEPRECATION")
//            vibrator.vibrate(500)
//        }
//    }

    override fun onResume() {
        super.onResume()
        if (useSensors) {
            accelerometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (useSensors) {
            sensorManager.unregisterListener(this)
        }
    }

    private fun updateUI() {
        for (i in 0 until ROWS) {
            for (j in 0 until COLS) {
                val cell = cellViews[i][j]
                val cellValue = gameManager.grid[i][j]
                val isCar = (i == ROWS - 1 && j == gameManager.carLane)

                cell.setImageResource(
                    when {
                        isCar -> R.drawable.spaceship_final1
                        cellValue == 1 -> R.drawable.orange_meteor
                        cellValue == 2 -> R.drawable.cashflow
                        else -> android.R.color.transparent
                    }
                )
                // Add padding only for coins to make them appear smaller
                cell.setPadding(
                    if (cellValue == 2) 20 else 0,
                    if (cellValue == 2) 20 else 0,
                    if (cellValue == 2) 20 else 0,
                    if (cellValue == 2) 20 else 0
                )
            }
        }

        for (i in heartViews.indices) {
            heartViews[i].visibility = if (i < gameManager.lives) View.VISIBLE else View.INVISIBLE
        }
        main_TXT_distance.text = "Distance: ${gameManager.distance}"
        main_TXT_score.text = "Score: ${gameManager.score}"
    }
}


