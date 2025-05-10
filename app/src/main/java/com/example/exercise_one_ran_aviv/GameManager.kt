package com.example.exercise_one_ran_aviv

class GameManager(private val rows: Int = 6, private val cols: Int = 5) {
    var distance: Int = 0
        private set
    var score: Int = 0
        private set
    var carLane = 2
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

        // Step 1: Start with a clean top row
        grid[0] = IntArray(cols) { 0 }

        // Step 2: Possibly spawn an obstacle
        if ((0 until 100).random() < 60) { // 60% chance
            val obstacleLane = (0 until cols).random()
            grid[0][obstacleLane] = 1
        }

        // Step 3: Possibly spawn a coin, but not in the same lane as the obstacle
        if ((0 until 100).random() < 30) { // 30% chance
            var coinLane: Int
            do {
                coinLane = (0 until cols).random()
            } while (grid[0][coinLane] != 0) // make sure it's not the same lane

            grid[0][coinLane] = 2
        }

        // Step 4: Coin pickup
        if (grid[rows - 1][carLane] == 2) {
            score += 10
            grid[rows - 1][carLane] = 0
        }

        // Step 5: Collision with obstacle
        if (grid[rows - 1][carLane] == 1) {
            grid[rows - 1][carLane] = 0
            lives--
            if (lives <= 0) {
                resetGame()
            }
            return true

        }
        distance++
        score += 5
        return false
    }

    fun resetGame() {
        score = 0
        lives = 3
        carLane = 2
        grid = Array(rows) { IntArray(cols) { 0 } }
        distance = 0
    }
}
