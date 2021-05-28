package com.netflixclone

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

open class MyApplication : Application() {
    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate()
    }
}
