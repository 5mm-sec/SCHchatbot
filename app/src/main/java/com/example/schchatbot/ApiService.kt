package com.example.schchatbot

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/text_processing/") // API 엔드포인트 경로
    fun processText(@Body request: TextProcessingRequest): Call<TextProcessingResponse>
}