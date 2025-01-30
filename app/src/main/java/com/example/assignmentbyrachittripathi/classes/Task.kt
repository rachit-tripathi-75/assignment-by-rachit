package com.example.assignmentbyrachittripathi.classes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(@PrimaryKey(autoGenerate = true) val id: Int = 0, val task: String, val date: String, val time: String)
