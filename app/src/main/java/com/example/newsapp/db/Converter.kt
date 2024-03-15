package com.example.newsapp.db

import androidx.room.TypeConverter
import com.example.newsapp.models.Source

class Converter {

    @TypeConverter
    fun fromSource(source: Source):String{
        return source?.name.toString()
    }

    @TypeConverter
    fun toSource(name:String):Source{
        return Source(name, name)
    }
}