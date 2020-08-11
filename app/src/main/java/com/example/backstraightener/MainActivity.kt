package com.example.backstraightener

import android.content.Context
import android.os.*
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
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

    // Other objects
    internal lateinit var npHour: NumberPicker
    internal lateinit var npMinute: NumberPicker
    internal lateinit var  npSecond: NumberPicker

    internal lateinit var countDownTimer: CountDownTimer
    internal var countdownStartTime: Long = 6000
    internal val countdownInterval: Long = 1000

    internal var startPressed: Boolean = false

    var secondPicked = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hour
        npHour = findViewById<NumberPicker>(R.id.countdown_hour)
        initializeTimer(npHour, MIN_VAL, MAX_HOUR, hoursArray)
        changeKeyboardType(npHour)

        // Minutes
        npMinute = findViewById<NumberPicker>(R.id.countdown_minute)
        initializeTimer(npMinute, MIN_VAL, MAX_MINUTE, minuteArray)
        changeKeyboardType(npMinute)

        // Seconds
        npSecond = findViewById<NumberPicker>(R.id.countdown_second)
        initializeTimer(npSecond, MIN_VAL, MAX_SECOND, secondArray)
        changeKeyboardType(npSecond)
        npSecond.setOnValueChangedListener { picker, oldVal, newVal ->
            secondPicked = newVal
        }
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
    private fun changeKeyboardType(np: NumberPicker) {
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
    private fun convertTimeToMillis(timeLength: Int, timeUnit: String): Long {
        when(timeUnit) {
            "hour" ->  return timeLength.toLong() * 3600000
            "minute" -> return timeLength.toLong() * 60000
            else -> return timeLength.toLong() * 1000
        }
    }

    /**
     * Resets the timer
     *
     */
    fun resetTimer() {

        countdownStartTime = convertTimeToMillis(npHour.value, "hour") +
                convertTimeToMillis(npMinute.value, "minute") +
                convertTimeToMillis(npSecond.value, "second")

        val test = convertTimeToMillis(secondPicked, "second")

        countDownTimer = object: CountDownTimer(test, 1000) {
            override fun onTick(p0: Long) {
                npSecond.isEnabled = false
                val timeRemaining = p0 / 1000
                npSecond.value = timeRemaining.toInt()
            }

            override fun onFinish() {
                npSecond.isEnabled = true
                vibratePhone()
            }
        }

        startPressed = false
    }

    fun startTimer() {
        countDownTimer.start()
        startPressed = true
    }

    /**
     * Runs whenever the start button on the app is pressed
     *
     * @param view the button
     */
    fun onPressStart(view: View) {
        resetTimer()

        if (!startPressed){
            startTimer()
        }
    }

    /**
     * Vibrates the phone
     *
     */
    fun vibratePhone() {
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


