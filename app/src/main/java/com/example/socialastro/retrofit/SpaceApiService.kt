package com.example.socialastro.retrofit

import com.example.socialastro.model.ModelArticle
import retrofit2.Response
import retrofit2.http.GET

interface SpaceApiService {

    @GET("articles")
    suspend fun listPosts() : Response<List<ModelArticle>>
}