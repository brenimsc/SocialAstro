package com.example.socialastro.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ModelArticle(
    @PrimaryKey
    val id: Int,
    var featured: Boolean,
    val title: String,
    val url: String,
    val imageUrl: String,
    val newsSite: String,
    val summary: String,
    val publishedAt: String
) {

}