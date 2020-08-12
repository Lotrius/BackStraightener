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

// Timer wheel values
private const val MIN_VAL = 0
private const val MAX_HOUR = 100
private const val MAX_MINUTE = 60
private const val MAX_SECOND = 60

// Vibration length
private const val VIBRATE_LENGTH: Long = 1000

class MainActivity : AppCompatActivity() {
    // Number pickers
    private lateinit var npHour: NumberPicker
    private lateinit var npMinute: NumberPicker
    private lateinit var  npSecond: NumberPicker

    // Arrays for number pickers
    private val dec = DecimalFormat("00")
    private val hoursArray = Array(MAX_HOUR) {i -> dec.format(i)}
    private val minuteArray = Array(MAX_MINUTE) {i -> dec.format(i)}
    private val secondArray = Array(MAX_SECOND) {i -> dec.format(i)}

    // Countdown timer and values
    private lateinit var countDownTimer: CountDownTimer
    private var countdownStartTime: Long = 6000
    private val countdownInterval: Long = 1000

    // Whether start button was pressed or not
    private var startPressed: Boolean = false

    // Value of the second picked
    private var secondPicked: Long = 0

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
            secondPicked = newVal.toLong()
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
    private fun resetTimer() {
        // Get the time requested by the user in milliseconds
        countdownStartTime = convertTimeToMillis(npHour.value, "hour") +
                convertTimeToMillis(npMinute.value, "minute") +
                convertTimeToMillis(npSecond.value, "second")

        // Countdown timer
        countDownTimer = object: CountDownTimer(countdownStartTime, countdownInterval) {
            override fun onTick(p0: Long) {
                // Disable interaction with the number picker
                npHour.isEnabled = false
                npMinute.isEnabled = false
                npSecond.isEnabled = false

                // Count down
                val timeRemaining = p0 / countdownInterval

                if (timeRemaining % 360 == 359L && npHour.value > 0) {
                    npHour.value--
                }
                if (timeRemaining % 60 == 59L && (npMinute.value > 0 || npHour.value > 0)) {
                    npMinute.value--
                }
                if (timeRemaining % 1 == 0L) {
                    npSecond.value--
                }
            }

            override fun onFinish() {
                // Re-enable interaction with the number picker
                npHour.isEnabled = true
                npMinute.isEnabled = true
                npSecond.isEnabled = true

                // Vibrate the phone
                vibratePhone()

                // startPressed reset to false
                startPressed = false
            }
        }
    }

    /**
     * Start the timer, set startPressed to true
     *
     */
    fun startTimer() {
        // Start has been pressed
        startPressed = true

        // START THE TIMER, KRONK
        countDownTimer.start()
    }

    /**
     * Runs whenever the start button on the app is pressed
     *
     * @param view the button
     */
    fun onPressStart(view: View) {
        // Reset the timer
        resetTimer()

        // If start hasn't been pressed already, start the timer
        if (!startPressed){
            startTimer()
        }
    }

    /**
     * Vibrates the phone
     *
     */
    fun vibratePhone() {
        val timings: LongArray= longArrayOf(0, 1000)
        val amplitudes: IntArray = intArrayOf(0, 255)

        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // Vibrate for 1000 milliseconds
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


