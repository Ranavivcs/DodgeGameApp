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



class MainActivity : AppCompatActivity() {

    private val ROWS = 6
    private val COLS = 3

    private lateinit var gameManager: GameManager
    private lateinit var cellViews: List<List<AppCompatImageView>>
    private lateinit var heartViews: List<AppCompatImageView>
    private lateinit var main_BTN_left: MaterialButton
    private lateinit var main_BTN_right: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        SignalManager.init(this)

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gameManager = GameManager(ROWS, COLS)
        findViews()
        setupButtons()

        lifecycleScope.launch {
            while (true) {
                delay(1500L)
                val crashed = gameManager.updateGame()
                if (crashed) handleCrash2()
                updateUI()
            }
        }
    }

    private fun findViews() {
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

    private fun updateUI() {
        for (i in 0 until ROWS) {
            for (j in 0 until COLS) {
                val cell = cellViews[i][j]
                val isCar = (i == ROWS - 1 && j == gameManager.carLane)
                val isObstacle = gameManager.grid[i][j] == 1

                cell.setImageResource(
                    when {
                        isCar -> R.drawable.spaceship_final1
                        isObstacle -> R.drawable.orange_meteor
                        else -> android.R.color.transparent
                    }
                )
            }
        }

        for (i in heartViews.indices) {
            heartViews[i].visibility = if (i < gameManager.lives) View.VISIBLE else View.INVISIBLE
        }
    }
}
