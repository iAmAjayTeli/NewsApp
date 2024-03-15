package com.example.newsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapp.models.Article
import java.util.concurrent.locks.Lock


@Database(entities = [Article::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getNewsDao():NewsDao
   companion object{
       @Volatile  //changes made by one thread are immediately invisible to other theads
       private var instance:ArticleDatabase?=null
       private val LOCK=Any() //only one thread can execute code inside this block at a time



       operator fun invoke(context: Context)= instance ?: synchronized(LOCK){ // it will first check the instance variable is initialized or not.
           //if not initialized then it will enters into the synchronized block.
           instance ?: createDatabase(context).also {// After entering into synchronized block it again check that the instace variable is null or not
               //if not initialized then it will call the createDatabase function and create database will create room database
               instance=it //assigning the ArticleDataBase Instance to instance variable
           }
       }


       private fun createDatabase(context: Context) =
           Room.databaseBuilder(
               context.applicationContext,
               ArticleDatabase::class.java,
               "article_db.db").build()

   }

   }