package com.example.db_test1

//import android.R
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.TypeConverter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
//import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


//class MainActivity : AppCompatActivity() {
//
//    lateinit var database: PersonDatabase
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        database = Room.databaseBuilder(
//            applicationContext,
//            PersonDatabase::class.java, "persons_db"
//        ).allowMainThreadQueries().build()
//
//        btnAdd.setOnClickListener{
//            val newPerson = Person(
//                inputName.text.toString(),
//                inputSName.text.toString(),
//                inputAge.text.toString().toInt()
//            )
//
//            database.personDao().insertPerson(newPerson)
//        }
//
//        btnSelectAll.setOnClickListener{
//            val list = database.personDao().getAllPersons()
//            val display = findViewById<EditText>(R.id.display)
//            display.setText(list.toString())
//        }
//
//        btnClear.setOnClickListener{
//            database.personDao().clearDB()
//        }
//
//
//
//    }
//}

class MainActivity : AppCompatActivity() {
    // Timer setup
    var pTimeMin = 2
    var breakTimeMin = 1
    var breakFlag = false
    var cancelButtonFlag = false

    lateinit var database: SessionDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // DB
        database = Room.databaseBuilder(
                applicationContext,
                SessionDatabase::class.java, "sessions_db"
        ).allowMainThreadQueries().build()

        val sessionsView = findViewById<TextView>(R.id.sessionsCounter)
        val button: Button = findViewById(R.id.startButton)
        lateinit var timer: CountDownTimer

        sessionsView.setText("All sessions: ${getAllSessionsCounter()}, yesterday: ${getYesterdaySessionsCounter()}, today: ${getTodaySessionsCounter()}")

        button.setOnClickListener(View.OnClickListener {
            if (!cancelButtonFlag) {
                cancelButtonFlag = true
                button.setText("Cancel timer")
                if (!breakFlag) {
                    timer = addTimer(pTimeMin.toLong(), button)
                } else {
                    timer = addTimer(breakTimeMin.toLong(), button)
                }
                timer.start()
            } else {
                timer.cancel()
                cancelButtonFlag = false
                button.setText("Start timer")
            }
        })
    }

    fun addTimer(pTime: Long, button: Button): CountDownTimer {
        var timer = object : CountDownTimer((pTime * 60 * 1000).toLong(), 1000) {
            val display = findViewById<TextView>(R.id.textView)
            override fun onFinish() {
//                    it.setEnabled(true)
                if (!breakFlag) {
                    breakFlag = true
                    // In order to prevent timer cancelling
                    cancelButtonFlag = false
                    display.setText("Time to take a break")
                    button.setText("Start timer")
                    addSession()
                } else {
                    breakFlag = false
                    // In order to prevent timer cancelling
                    cancelButtonFlag = false
                    display.setText("Time to get back to work")
                    button.setText("Start timer")
                }
            }

            override fun onTick(millisUntilFinished: Long) {
//                    Log.d("t1", "$millisUntilFinished")
                val secAll = millisUntilFinished / 1000
                val min = secAll / 60
                display.setText("${min} : ${secAll % 60}")
            }

        }

        return timer
    }

    fun addSession() {
        val sessionsView = findViewById<TextView>(R.id.sessionsCounter)
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(getDaysAgo(0))
        var todayCounter = getTodaySessionsCounter()
        if (todayCounter > 0) {
            var existingSession = database.sessionDao().getByTimestamp(today.toLong())
            database.sessionDao().updateCount(existingSession.id, existingSession.count + 1)
            sessionsView.setText("All sessions: ${getAllSessionsCounter()}, yesterday: ${getYesterdaySessionsCounter()}, today: ${getTodaySessionsCounter()}")
        } else {
            val newSession = Session(1,today.toLong())
            database.sessionDao().insertSession(newSession)
            sessionsView.setText("All sessions: ${getAllSessionsCounter()}, yesterday: ${getYesterdaySessionsCounter()}, today: ${1}")
        }
    }

    fun getTodaySessionsCounter(): Int {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(getDaysAgo(0))
        var isExists = database.sessionDao().isRowIsExist(today.toLong())
        if (isExists) {
            var existingSession = database.sessionDao().getByTimestamp(today.toLong())
            return existingSession.count
        } else {
            return 0
        }
    }

    fun getYesterdaySessionsCounter(): Int {
        val yesterday = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(getDaysAgo(1))
        val yesterdayExists = database.sessionDao().isRowIsExist(yesterday.toLong())
        var yesterdaySessions = 0
        if (yesterdayExists) {
            yesterdaySessions = database.sessionDao().getByTimestamp(yesterday.toLong()).count
        }
        return yesterdaySessions
    }

    fun getAllSessionsCounter(): Int {
        return database.sessionDao().getAllSessionsCount()
    }

//    fun getYesterdayAndAllSessions(): String {
//        val yesterday = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(getDaysAgo(1))
//        val yesterdayExists = database.sessionDao().isRowIsExist(yesterday.toLong())
//        var yesterdaySessions = 0
//        if (yesterdayExists) {
//            yesterdaySessions = database.sessionDao().getByTimestamp(yesterday.toLong()).count
//        }
//        val allSessionsCount = database.sessionDao().getAllSessionsCount()
//        return "All sessions: ${allSessionsCount}, yesterday: ${yesterdaySessions}"
//    }

    fun getDaysAgo(daysAgo: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

        return calendar.time
    }
}

class DateConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}