package com.example.newsapp.api

import com.example.newsapp.Util.Constants.Companion.Base_url
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object{
       private val retrofit by lazy {
             Retrofit.Builder()
               .baseUrl(Base_url)
               .addConverterFactory(GsonConverterFactory.create())
               .build()
       }

        val newsApi:NewsApi by lazy {
            retrofit.create(NewsApi::class.java)
        }

        }

    }
