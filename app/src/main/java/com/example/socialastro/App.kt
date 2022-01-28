package com.example.socialastro

import android.app.Application
import com.example.socialastro.koin.dataSource
import com.example.socialastro.koin.moduleNetwork
import com.example.socialastro.koin.repoModule
import com.example.socialastro.koin.viewModels
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.) propriedades para definir night mode
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    moduleNetwork,
                    viewModels,
                    repoModule,
                    dataSource
                )
            )
        }
    }
}