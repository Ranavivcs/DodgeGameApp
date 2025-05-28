# 🚀 Space Dodge

A fast-paced Android game where you control a spaceship dodging incoming meteors and collecting coins. Features multiple control modes, high score tracking with location, and polished UI with sound effects and animations.

---

## 🕹 Gameplay

- Move your spaceship left or right to dodge obstacles and collect coins.
- Each coin gives you points, each obstacle reduces a life.
- The game ends when you lose all lives.


---
## 🎮 Features

- Control Modes:
  - Button controls
  - Tilt controls (accelerometer)
- Speed Modes:
  - Normal
  - Fast
- Game pause functionality with options to:
  - Resume
  - Retry
  - Exit to main menu
- Game Over screen with:
  - Score summary
  - High score saving (with location and name)
  - View top 10 scores on map
- Crash sound effects and vibrations on hit
  
---
```
## 📦 Project Structure

com.example.exercise_one_ran_aviv/
│
├── data/
│ ├── HighScore.kt
│ └── HighScoreManager.kt
│
├── logic/
│ └── GameManager.kt
│
├── ui/
│ ├── MainActivity.kt
│ ├── MenuActivity.kt
│ └── HighScoresActivity.kt
│
├── ui/adapters/
│ └── HighScoreAdapter.kt
│
├── utils/
│ └── SignalManager.kt
```
## 🗺 High Scores with Location

- When a game ends and you earn a high score, you're prompted to enter your name.
- The app automatically captures your device's last known location.
- On the High Scores screen, scores are listed and marked on a map.

---

## 📱 Requirements

- Android 7.0 (API 24) and above
- Permissions:
  - `ACCESS_FINE_LOCATION` for location-based high scores

---

## 🔧 Setup Instructions

1. Clone the repository.
2. Open in Android Studio.
3. Build and run on emulator or real device.
4. Ensure location permission is granted for full functionality.

---

## 📍 Notes

- Data is saved using `SharedPreferences` and serialized with `Gson`.
- The app supports both Hebrew and English locales.

---

## 🎨 Assets

- Spaceship, meteor, and coin graphics are custom or sourced from free asset libraries.
- Crash sound is triggered when colliding with obstacles.

---

## 👨‍💻 Developer

Made with Kotlin and Android Studio  
By Ran Aviv ✌️

