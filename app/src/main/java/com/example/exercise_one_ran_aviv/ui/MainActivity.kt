package com.example.exercise_one_ran_aviv.ui

import android.os.Bundle
import android.view.View
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.activity.enableEdgeToEdge
import android.widget.TextView
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AlertDialog
import android.widget.Button
import kotlinx.coroutines.Job
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.widget.EditText
import android.widget.ImageButton
import android.media.MediaPlayer
import com.example.exercise_one_ran_aviv.R
import com.example.exercise_one_ran_aviv.logic.GameManager
import com.example.exercise_one_ran_aviv.data.HighScore
import com.example.exercise_one_ran_aviv.data.HighScoreManager
import com.example.exercise_one_ran_aviv.utils.SignalManager


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var main_TXT_distance: TextView

    private val ROWS = 6
    private val COLS = 5

    private lateinit var crashSoundPlayer: MediaPlayer
    private var lastLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isGamePaused = false
    private lateinit var main_TXT_score: TextView
    private lateinit var gameManager: GameManager
    private lateinit var cellViews: List<List<AppCompatImageView>>
    private lateinit var heartViews: List<AppCompatImageView>
    private lateinit var main_BTN_pause: ImageButton
    private lateinit var main_BTN_left: MaterialButton
    private lateinit var main_BTN_right: MaterialButton
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastMoveTime: Long = 0L
    private var useSensors: Boolean = false
    private var isFastMode: Boolean = false
    private var gameLoopJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
        crashSoundPlayer = MediaPlayer.create(this, R.raw.crash_sound)
        startGameLoop()

    }

    private fun playCrashSound() {
        if (crashSoundPlayer.isPlaying) {
            crashSoundPlayer.stop()
            crashSoundPlayer.prepare()
        }
        crashSoundPlayer.start()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]

            val currentTime = System.currentTimeMillis()
            if (currentTime - lastMoveTime > 400) {
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
    }

    private fun findViews() {

        main_TXT_score = findViewById(R.id.main_TXT_score)
        main_BTN_pause = findViewById(R.id.main_BTN_pause)
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
        main_BTN_pause.setOnClickListener {
            isGamePaused = true
            showPauseDialog()
        }

        main_BTN_right.setOnClickListener {
            gameManager.moveCarRight()
            updateUI()
        }
    }


    private fun handleGameOver() {
        gameLoopJob?.cancel()

        val dialogView = layoutInflater.inflate(R.layout.game_over_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val txtScore = dialogView.findViewById<TextView>(R.id.dialog_TXT_score)
        val btnRetry = dialogView.findViewById<Button>(R.id.dialog_BTN_retry)
        val btnMenu = dialogView.findViewById<Button>(R.id.dialog_BTN_mainmenu)
        val btnViewScores = dialogView.findViewById<Button>(R.id.dialog_BTN_viewscores)

        txtScore.text = "Score: ${gameManager.score}\nDistance: ${gameManager.distance}"


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            dialog.show()
            return
        }


        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val currentScore = gameManager.score
            val currentDistance = gameManager.distance
            val scores = HighScoreManager.getHighScores()
            val minScore = scores.minByOrNull { it.score }?.score ?: 0

            fun showGameOverDialog() {
                dialog.show()
            }

            if (scores.size < 10 || currentScore > minScore) {
                promptForName(
                    onNameEntered = { playerName ->
                        val highScore = HighScore(
                            name = playerName,
                            score = currentScore,
                            distance = currentDistance,
                            latitude = location?.latitude ?: 0.0,
                            longitude = location?.longitude ?: 0.0
                        )
                        HighScoreManager.saveHighScore(highScore)
                        showGameOverDialog()
                    },
                    onCancel = {
                        showGameOverDialog()
                    }
                )
            } else {
                showGameOverDialog()
            }
        }

        btnRetry.setOnClickListener {
            gameManager.resetGame()
            isGamePaused = false
            updateUI()
            startGameLoop()
            dialog.dismiss()
        }

        btnViewScores.setOnClickListener {
            val intent = Intent(this, HighScoresActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        btnMenu.setOnClickListener {
            dialog.dismiss()
            finish()
        }
    }


    private fun promptForName(onNameEntered: (String) -> Unit, onCancel: () -> Unit) {
        val editText = EditText(this)
        editText.hint = "Enter your name"
        AlertDialog.Builder(this)
            .setTitle("New High Score!")
            .setView(editText)
            .setPositiveButton("Save") { dialog, _ ->
                val name = editText.text.toString().ifBlank { "Unknown" }
                onNameEntered(name)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                onCancel()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
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

    private fun handleCrash2() {
        playCrashSound()
        SignalManager
            .getInstance()
            .toast("💥 Crash! You lost a life")

        SignalManager
            .getInstance()
            .vibrate()
    }

    override fun onResume() {
        super.onResume()
        if (useSensors) {
            accelerometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            }
        }
    }

    private fun startGameLoop() {

        gameLoopJob?.cancel()

        gameLoopJob = lifecycleScope.launch {
            while (true) {
                val delayTime = if (isFastMode) 600L else 1500L
                delay(delayTime)
                if (!isGamePaused) {
                    val crashed = gameManager.updateGame()
                    if (crashed) {
                        handleCrash2()
                        if (gameManager.lives <= 0) {
                            isGamePaused = true
                            delay(300)
                            handleGameOver()
                        }
                    }
                    updateUI()
                }
            }
        }
    }



    override fun onPause() {
        super.onPause()
        if (useSensors) {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        crashSoundPlayer.release()
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

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                lastLocation = location
                println("Location cached: ${location.latitude}, ${location.longitude}")
            }
        }
    }

    private fun showPauseDialog() {

        val dialogView = layoutInflater.inflate(R.layout.pause_dialog, null)
        val btnContinue = dialogView.findViewById<Button>(R.id.pause_BTN_continue)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val btnExit = dialogView.findViewById<Button>(R.id.pause_BTN_exit)
        val btnRetry = dialogView.findViewById<Button>(R.id.pause_BTN_retry)

        btnExit.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        btnContinue.setOnClickListener {
            isGamePaused = false
            dialog.dismiss()
        }

        btnRetry.setOnClickListener {
            gameManager.resetGame()
            updateUI()
            isGamePaused = false
            startGameLoop()
            dialog.dismiss()
        }

        dialog.show()
    }
}



