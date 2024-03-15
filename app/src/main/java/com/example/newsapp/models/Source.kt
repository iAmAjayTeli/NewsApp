package com.example.newsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "sources")
data class Source(
    val id: String,
    val name: String
) : Serializable
{
    // Override hashCode() to handle null values
    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
