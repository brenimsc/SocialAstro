package com.example.socialastro.repository

import androidx.lifecycle.LiveData
import com.example.socialastro.database.ArticleDao
import com.example.socialastro.model.ModelArticle

class FavoritesRepository(val dao: ArticleDao) {

    suspend fun alterToFavorites(item: ModelArticle) {
        dao.alterToFavorite(item)
    }

    fun getAllFavorites(): LiveData<List<ModelArticle>?> {
        return dao.getFavorites()
    }

    suspend fun getArticle(id: Int) : ModelArticle {
        return dao.getArticle(id)
    }

    suspend fun deleteFavorites() {
        val listAll = dao.getAllArticle()
        listAll.map {
            it.featured = false
        }
        dao.saveArticles(listAll)
    }
}