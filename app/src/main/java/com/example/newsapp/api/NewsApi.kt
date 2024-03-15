package com.example.newsapp.api

import com.example.newsapp.models.NewsResponse
import com.example.newsapp.Util.Constants.Companion.api_key
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

   @GET("v2/top-headlines")
   suspend fun getHeadlines(
       @Query("country")
       countryCode:String="us",
       @Query("page")
       pageNumber:Int=1,
       @Query("apiKey")
       apiKey:String= api_key

   ):Response<NewsResponse>

   @GET("v2/everything")
   suspend fun searchForNews(
       @Query("q")
       searchQuary:String,
       @Query("page")
       pageNumber: Int=1,
       @Query("apiKey")
       apiKey: String= api_key
   ):Response<NewsResponse>
}