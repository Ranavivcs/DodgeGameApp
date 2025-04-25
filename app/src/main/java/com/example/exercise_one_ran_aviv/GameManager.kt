package com.example.exercise_one_ran_aviv

class GameManager(private val rows: Int = 6, private val cols: Int = 3) {

    var carLane = 1
    var lives = 3
        private set
    var justSpawnedObstacle = false
    var grid = Array(rows) { IntArray(cols) { 0 } }

    fun moveCarLeft() {
        if (carLane > 0) carLane--
    }

    fun moveCarRight() {
        if (carLane < cols - 1) carLane++
    }

    fun updateGame(): Boolean {
        for (i in rows - 1 downTo 1) {
            grid[i] = grid[i - 1].copyOf()
        }

        grid[0] = if (justSpawnedObstacle) {
            justSpawnedObstacle = false
            IntArray(cols) { 0 }
        } else {
            val generatePercent = (0 until 100).random()
            if (generatePercent < 90) {
                val obstacleLane = (0 until cols).random()
                justSpawnedObstacle = true
                IntArray(cols) { if (it == obstacleLane) 1 else 0 }
            } else {
                justSpawnedObstacle = false
                IntArray(cols) { 0 }
            }
        }

        if (grid[rows - 1][carLane] == 1) {
            lives--
            if (lives <= 0) {
                resetGame()
            }
            return true // collision
        }

        return false
    }

    fun resetGame() {
        lives = 3
        carLane = 1
        grid = Array(rows) { IntArray(cols) { 0 } }
    }
}
