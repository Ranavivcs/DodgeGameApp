package com.example.exercise_one_ran_aviv.data


import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken




object HighScoreManager {
    private const val PREFS_NAME = "high_scores_prefs"
    private const val KEY_SCORES = "high_scores"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveHighScore(newScore: HighScore) {
        val scores = getHighScores().toMutableList()
        scores.add(newScore)
        scores.sortByDescending { it.score }
        if (scores.size > 10) scores.removeAt(scores.size - 1)

        val json = Gson().toJson(scores)
        prefs.edit().putString(KEY_SCORES, json).apply()
    }

    fun getHighScores(): List<HighScore> {
        val json = prefs.getString(KEY_SCORES, null) ?: return emptyList()
        val type = object : TypeToken<List<HighScore>>() {}.type
        return Gson().fromJson(json, type)
    }
}