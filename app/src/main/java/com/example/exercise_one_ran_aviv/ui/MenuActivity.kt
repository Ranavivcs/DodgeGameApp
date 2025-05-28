package com.example.exercise_one_ran_aviv.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.example.exercise_one_ran_aviv.R
import com.example.exercise_one_ran_aviv.data.HighScoreManager

class MenuActivity : AppCompatActivity() {

    private lateinit var toggleControls: ToggleButton
    private lateinit var toggleSpeed: ToggleButton
    private lateinit var btnStart: Button
    private lateinit var btnHighScores: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)

        HighScoreManager.init(this)

        toggleControls = findViewById(R.id.menu_TOGGLE_controls)
        toggleSpeed = findViewById(R.id.menu_TOGGLE_speed)
        btnStart = findViewById(R.id.menu_BTN_start)
        btnHighScores = findViewById(R.id.menu_BTN_highscores)

        btnStart.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("USE_SENSORS", toggleControls.isChecked)
            intent.putExtra("IS_FAST_MODE", toggleSpeed.isChecked)
            startActivity(intent)
        }

        btnHighScores.setOnClickListener {
            val intent = Intent(this, HighScoresActivity::class.java)
            startActivity(intent)
        }
    }
}