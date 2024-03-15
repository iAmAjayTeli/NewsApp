package com.example.newsapp.repository

import com.example.newsapp.api.NewsApi
import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.db.ArticleDatabase
import com.example.newsapp.db.NewsDao
import com.example.newsapp.models.Article
import retrofit2.http.Query

class NewsRepository(val db:ArticleDatabase) {



    suspend fun getHeadlines(countryCode:String, pageNumber:Int)=
        RetrofitInstance.newsApi.getHeadlines(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int)=
        RetrofitInstance.newsApi.searchForNews(searchQuery,pageNumber)

    suspend fun insert(article:Article)=
        db.getNewsDao().insert(article)

    suspend fun delete(article:Article)=
        db.getNewsDao().delete(article)


    fun getAllArticles()=
        db.getNewsDao().getAllArticles()
}