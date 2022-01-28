package com.example.socialastro.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.socialastro.model.ModelArticle

@Database(entities = [ModelArticle::class], version = 1, exportSchema = false)
abstract class ArticleDatabase : RoomDatabase() {

    abstract val articleDatabaseDao: ArticleDao

    companion object {
        @Volatile
        private var INSTANCE: ArticleDatabase? = null

        fun getInstance(context: Context): ArticleDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,
                    ArticleDatabase::class.java,
                    "article_database")
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}