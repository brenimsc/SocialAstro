package com.example.socialastro.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.socialastro.database.ArticleDao
import com.example.socialastro.Constansts
import com.example.socialastro.model.ManeiraFilter
import com.example.socialastro.model.ModelArticle
import com.example.socialastro.model.ModelFilter
import com.example.socialastro.model.OrderByFilter
import com.example.socialastro.retrofit.SpaceApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import java.net.UnknownHostException

class HomeRepository(val service: SpaceApiService, val dao: ArticleDao, val preferences: SharedPreferences) {

    suspend fun loadPostss() = flow {
        try {
            val call = service.listPosts()
            if (call.isSuccessful) {
                if (call.body() != null) {
                    val listRecent = call.body()
                    val listFavorites: List<ModelArticle>? = dao.getFavoritesList()
                    listRecent?.map { post ->
                        post.featured = false
                        if (listFavorites != null) {
                            for (item in listFavorites) {
                                if (post.id == item.id) {
                                    post.featured = item.featured
                                    return@map
                                }
                            }
                        }
                    }
                    listRecent?.let {
                        dao.saveArticles(it)
                        emit(it)
                    }
                }

            } else {
                emit(Exception("Deu erro"))
            }

        } catch (e: UnknownHostException) {
            Log.e("BRENOL", "$e")
            emit(UnknownHostException("Deu erro"))
        }
    }

    fun getPostsInternal(filter: ModelFilter): LiveData<List<ModelArticle>?> {
        return when (filter) {
            ModelFilter(OrderByFilter.DATA, ManeiraFilter.DESCRECENTE) -> {
                dao.getAllArticleDataDesc()
            }
            ModelFilter(OrderByFilter.DATA, ManeiraFilter.CRESCENTE) -> {
                dao.getAllArticleDataAsc()
            }

            ModelFilter(OrderByFilter.ALFABETICA, ManeiraFilter.CRESCENTE) -> {
                dao.getAllArticleNameAsc()
            }
            else -> {
                dao.getAllArticleNameDesc()
            }
        }
    }

    private fun modelFilterPadrao(): String {
        return Gson().toJson(ModelFilter(OrderByFilter.DATA, ManeiraFilter.DESCRECENTE))
    }

    fun getFilter(): ModelFilter {
        return Gson().fromJson(preferences.getString(Constansts.FILTER, modelFilterPadrao()), ModelFilter::class.java)
    }

    fun savePreferences(filter: ModelFilter) {

        val filterJson: String = Gson().toJson(filter)
        preferences.edit {
            putString(Constansts.FILTER, filterJson)
            commit()
        }

    }
}