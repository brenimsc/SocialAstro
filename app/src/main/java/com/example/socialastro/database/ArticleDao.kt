package com.example.socialastro.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.socialastro.model.ModelArticle

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveArticles(articles: List<ModelArticle>)

    @Insert
    fun saveArticle(article: ModelArticle)

    @Query("SELECT * FROM ModelArticle WHERE id = :id")
    suspend fun getArticle(id: Int): ModelArticle

    @Query("SELECT * FROM ModelArticle")
    suspend fun getAllArticle(): List<ModelArticle>

    @Query("SELECT * FROM ModelArticle ORDER BY publishedAt ASC")
    fun getAllArticleDataAsc(): LiveData<List<ModelArticle>?>

    @Query("SELECT * FROM ModelArticle ORDER BY publishedAt DESC")
    fun getAllArticleDataDesc(): LiveData<List<ModelArticle>?>

    @Query("SELECT * FROM ModelArticle ORDER BY newsSite ASC")
    fun getAllArticleNameAsc(): LiveData<List<ModelArticle>?>

    @Query("SELECT * FROM ModelArticle ORDER BY newsSite DESC")
    fun getAllArticleNameDesc(): LiveData<List<ModelArticle>?>

    @Query("DELETE FROM ModelArticle")
    fun delete()

    @Update
    suspend fun alterToFavorite(article: ModelArticle)

    @Query("SELECT * FROM ModelArticle WHERE featured = 1")
    fun getFavorites(): LiveData<List<ModelArticle>?>

    @Query("SELECT * FROM ModelArticle WHERE featured = 1")
    suspend fun getFavoritesList(): List<ModelArticle>?


}