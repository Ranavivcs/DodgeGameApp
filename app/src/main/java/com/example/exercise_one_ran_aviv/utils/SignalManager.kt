package com.example.exercise_one_ran_aviv.utils

import android.content.Context
import android.content.Context.VIBRATOR_MANAGER_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import java.lang.ref.WeakReference

class SignalManager private constructor(context: Context) {
    private var contextRef = WeakReference(context)

    companion object {
        private var instance: SignalManager? = null

        fun init(context: Context): SignalManager {
            if (instance == null) {
                instance = SignalManager(context)
            } else {

                instance!!.contextRef.clear()
                instance!!.contextRef = WeakReference(context)
            }
            return instance!!
        }

        fun getInstance(): SignalManager {
            return instance
                ?: throw IllegalStateException("SignalManager is not initialized, call init(context) first")
        }
    }

    fun toast(text: String) {
        contextRef.get()?.let { context ->
            Toast
                .makeText(
                    context,
                    text,
                    Toast.LENGTH_SHORT
                )
                .show()
        }
    }

    fun vibrate() {
        contextRef.get()?.let { context: Context ->
            val vibrator: Vibrator =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager =
                        context.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    vibratorManager.defaultVibrator
                } else {
                    context.getSystemService(VIBRATOR_SERVICE) as Vibrator
                }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val SOSPattern = longArrayOf(
                    0,
                    200,
                    100,
                    200,
                    100,
                    200,
                    300,
                    500,
                    100,
                    500,
                    100,
                    500,
                    300,
                    200,
                    100,
                    200,
                    100,
                    200
                )

                val waveFormVibrationEffect =
                    VibrationEffect.createWaveform(
                        SOSPattern,
                        -1
                    )

                val oneShotVibrationEffect =
                    VibrationEffect.createOneShot(
                        500,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )

//                vibrator.vibrate(oneShotVibrationEffect)
                vibrator.vibrate(waveFormVibrationEffect)
            } else {
                vibrator.vibrate(500)
            }
        }
    }
}