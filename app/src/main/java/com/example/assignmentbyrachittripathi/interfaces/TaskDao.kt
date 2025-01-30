package com.example.assignmentbyrachittripathi.interfaces

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.assignmentbyrachittripathi.classes.Task

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task)

    @Query("SELECT * FROM tasks ORDER BY date ASC")
    suspend fun getTasks(): List<Task>
}