package com.example.assignmentbyrachittripathi.interfaces

import com.example.assignmentbyrachittripathi.classes.LoginRequest
import com.example.assignmentbyrachittripathi.classes.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("login.php")
    suspend fun login(@Body request: LoginRequest) : Response<LoginResponse>
}