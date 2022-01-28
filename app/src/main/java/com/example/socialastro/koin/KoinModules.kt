package com.example.socialastro.koin

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.socialastro.database.ArticleDatabase
import com.example.socialastro.repository.FavoritesRepository
import com.example.socialastro.repository.HomeRepository
import com.example.socialastro.retrofit.SpaceApiService
import com.example.socialastro.viewmodel.FavoritesViewModel
import com.example.socialastro.viewmodel.HomeViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "https://api.spaceflightnewsapi.net/v3/"

val moduleNetwork = module {
    single {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
    single {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    single {
        createService<SpaceApiService>(get(), get())
    }

}

val dataSource = module {
    single { ArticleDatabase.getInstance(androidContext()) }
    single { ArticleDatabase.getInstance(androidContext()).articleDatabaseDao }
    single <SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
}

val viewModels = module {
    viewModel { HomeViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
}

val repoModule = module {
    single { HomeRepository(get(), get(), get()) }
    single { FavoritesRepository(get())}
}

private inline fun <reified T> createService(
    client: OkHttpClient,
    factory: Moshi,
) : T {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(factory))
        .build()
        .create(T::class.java)
}