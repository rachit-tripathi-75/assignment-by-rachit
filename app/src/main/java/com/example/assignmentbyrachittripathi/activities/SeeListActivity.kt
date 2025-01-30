package com.example.assignmentbyrachittripathi.activities

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignmentbyrachittripathi.R
import com.example.assignmentbyrachittripathi.classes.Task
import com.example.assignmentbyrachittripathi.classes.TaskAdapter
import com.example.assignmentbyrachittripathi.classes.TaskDatabase
import com.example.assignmentbyrachittripathi.databinding.ActivitySeeListBinding
import kotlinx.coroutines.launch

class SeeListActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeeListBinding
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySeeListBinding.inflate(layoutInflater)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.tvYourTasks.visibility = View.INVISIBLE
        binding.recyclerView.visibility = View.INVISIBLE
        binding.llEmptyPlaceholder.visibility = View.VISIBLE


        binding.recyclerView.layoutManager = LinearLayoutManager(this@SeeListActivity)

        taskAdapter = TaskAdapter()
        binding.recyclerView.adapter = taskAdapter
        fetchDataFromDatabase()

    }

    private fun fetchDataFromDatabase() {

        lifecycleScope.launch {
            val tasks = getTasksFromRoomDatabase()
            if(tasks.isEmpty()) {
                binding.tvYourTasks.visibility = View.INVISIBLE
                binding.recyclerView.visibility = View.INVISIBLE
                binding.llEmptyPlaceholder.visibility = View.VISIBLE
            } else {
                binding.tvYourTasks.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE
                binding.llEmptyPlaceholder.visibility = View.INVISIBLE
            }
            taskAdapter.submitList(tasks)
        }



    }

    private suspend fun getTasksFromRoomDatabase(): List<Task> {
        val taskDao = TaskDatabase.getDatabase(this@SeeListActivity).taskDao()
        return taskDao.getTasks()
    }
}