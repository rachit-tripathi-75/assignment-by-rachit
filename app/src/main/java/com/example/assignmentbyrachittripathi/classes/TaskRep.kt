package com.example.assignmentbyrachittripathi.classes

import androidx.lifecycle.LiveData
import com.example.assignmentbyrachittripathi.interfaces.TaskDao

class TaskRep(private val taskDao: TaskDao) {
//    val allTasks: List<Task> = taskDao.getTasks()

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }
}