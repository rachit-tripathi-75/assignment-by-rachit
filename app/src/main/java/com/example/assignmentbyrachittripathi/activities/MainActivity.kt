package com.example.assignmentbyrachittripathi.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.assignmentbyrachittripathi.R
import com.example.assignmentbyrachittripathi.classes.Task
import com.example.assignmentbyrachittripathi.classes.TaskDatabase
import com.example.assignmentbyrachittripathi.classes.TaskRep
import com.example.assignmentbyrachittripathi.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private var flag1 = false
    private var flag2 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        listeners()
        setInfo()

    }

    private fun setInfo() {
        val sharedPreferences2 = getSharedPreferences("userDetailsPrefs", MODE_PRIVATE)
        val str_id = sharedPreferences2.getInt("id", -1)
        val str_username = sharedPreferences2.getString("username", "null")
        val str_email = sharedPreferences2.getString("email", "null")
        val str_role = sharedPreferences2.getString("role", "null")

        binding.tvId.text = "ID: $str_id"
        binding.tvUsername.text = "Username: $str_username"
        binding.tvEmail.text = "Email: $str_email"
        binding.tvRole.text = "Role: $str_role"

    }

    private fun listeners() {

        binding.tvSelectedDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(this@MainActivity, { _, selectedYear, selectedMonth, selectedDay ->
                    selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.tvSelectedDate.text = selectedDate
                }, year, month, day)

            datePickerDialog.show()
            flag1 = true
        }

        binding.tvSelectedTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog =
                TimePickerDialog(this@MainActivity, { _, selectedHour, selectedMinute ->
                    selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    binding.tvSelectedTime.text = selectedTime
                }, hour, minute, true)

            timePickerDialog.show()
            flag2 = true
        }

        binding.btnSave.setOnClickListener {
            if (isValid()) {
                lifecycleScope.launch {
                    saveToDatabase()
                }
            }

        }

        binding.btnSeeList.setOnClickListener {
            startActivity(Intent(this@MainActivity, SeeListActivity::class.java))
        }

        binding.btnLogOut.setOnClickListener {

            val dialog = AlertDialog.Builder(this)
                .setTitle("Log out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("yes") { dialog, which ->
                    logoutUser(this@MainActivity)
                    startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                    finishAffinity()

                }
                .setNegativeButton("No") { dialog, which ->

                }
                .create()
            dialog.show()


        }
    }

    private suspend fun saveToDatabase() {

        val taskDao = TaskDatabase.getDatabase(this).taskDao()
        val taskRep = TaskRep(taskDao)
        val task =
            Task(task = binding.etTask.text.toString(), date = selectedDate, time = selectedTime)
        taskRep.insert(task)

        Toast.makeText(
            this@MainActivity,
            "Inserted successfully",
            Toast.LENGTH_SHORT
        ).show()

    }

    private fun isValid(): Boolean {
        if (binding.etTask.text.toString().isEmpty()) {
            Toast.makeText(
                this@MainActivity,
                "Task can't be left empty. Enter something",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (flag1 == false) {
            Toast.makeText(this@MainActivity, "Please select date", Toast.LENGTH_SHORT).show()
            return false
        } else if (flag2 == false) {
            Toast.makeText(this@MainActivity, "Please select time", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun logoutUser(context: Context) {
        val sharedPreferences = context.getSharedPreferences("userSessionPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()
    }
}