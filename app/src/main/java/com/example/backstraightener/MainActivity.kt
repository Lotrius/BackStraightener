package com.example.backstraightener

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {

    // Timer wheel values
    internal val MIN_VAL = 0
    internal val MAX_HOUR = 100
    internal val MAX_MINUTE = 60
    internal val MAX_SECOND = 60

    internal val dec = DecimalFormat("00")
    internal val hoursArray = Array(MAX_HOUR) {i -> dec.format(i)}
    internal val minuteArray = Array(MAX_MINUTE) {i -> dec.format(i)}
    internal val secondArray = Array(MAX_SECOND) {i -> dec.format(i)}

    // Vibration length
    internal val VIBRATE_LENGTH: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hour
        val npHour = findViewById<NumberPicker>(R.id.countdown_hour)
        initializeTimer(npHour, MIN_VAL, MAX_HOUR, hoursArray)
        changeKeyboardType(npHour)

        // Minutes
        val npMinute = findViewById<NumberPicker>(R.id.countdown_minute)
        initializeTimer(npMinute, MIN_VAL, MAX_MINUTE, minuteArray)
        changeKeyboardType(npMinute)

        // Seconds
        val npSecond = findViewById<NumberPicker>(R.id.countdown_second)
        initializeTimer(npSecond, MIN_VAL, MAX_SECOND, minuteArray)
        changeKeyboardType(npSecond)

    }

    /**
     * Sets up timer values
     *
     * @param np the NumberPicker
     * @param minVal the minimum value of the picker
     * @param maxVal the maximum value of the picker
     * @param values the array of values that will displayed in the picker
     */
    private fun initializeTimer(np: NumberPicker, minVal: Int, maxVal: Int, values: Array<String>): Unit {
        // Set values
        np.minValue = minVal
        np.maxValue = maxVal - 1
        np.displayedValues = values
    }

    /**
     * Change keyboard type to TYPE_CLASS_NUMBER when NumberPicker is selected, otherwise a full
     * keyboard is displayed instead
     *
     * @param np the NumberPicker
     */
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    private fun changeKeyboardType(np: NumberPicker): Unit {
        // Set keyboard to display only numbers instead of full keyboard
        val input = findInput(np)
        if (input != null) {
            input.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }

    /**
     * Converts times in NumberPicker to milliseconds
     *
     * @param timeLength the length of the unit of time input
     * @param timeUnit the type of unit of time (hour, minute, second
     * @return Long
     */
    private fun convertTimeToMillis(timeLength: String, timeUnit: String): Long {
        when(timeUnit) {
            "hour" ->  return timeLength.toLong() * 3600000
            "minute" -> return timeLength.toLong() * 60000
            else -> return timeLength.toLong() * 1000
        }
    }

    fun onPressStart(view: View) {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(VIBRATE_LENGTH, VibrationEffect.DEFAULT_AMPLITUDE))
        } else { //deprecated in API 26
            v.vibrate(VIBRATE_LENGTH)
        }
    }

    // Code taken from Alan Moore at
    // https://stackoverflow.com/questions/18794265/restricting-android-numberpicker-to-numeric-keyboard-for-numeric-input-not-alph
    private fun findInput(np: ViewGroup): EditText? {
        val count = np.childCount
        for (i in 0 until count) {
            val child: View = np.getChildAt(i)
            if (child is ViewGroup) {
                findInput(child as ViewGroup)
            } else if (child is EditText) {
                return child
            }
        }
        return null
    }
}


